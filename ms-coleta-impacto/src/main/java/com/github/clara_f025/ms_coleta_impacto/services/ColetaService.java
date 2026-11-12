package com.github.clara_f025.ms_coleta_impacto.services;

import com.github.clara_f025.ms_coleta_impacto.clients.ColetarLoteRemoteRequest;
import com.github.clara_f025.ms_coleta_impacto.clients.GeradorLotesClient;
import com.github.clara_f025.ms_coleta_impacto.clients.LoteRemotoResponse;
import com.github.clara_f025.ms_coleta_impacto.clients.ReservarLoteRemoteRequest;
import com.github.clara_f025.ms_coleta_impacto.dtos.AtualizarColetaRequest;
import com.github.clara_f025.ms_coleta_impacto.dtos.BaixaColetaRequest;
import com.github.clara_f025.ms_coleta_impacto.dtos.ColetaResponse;
import com.github.clara_f025.ms_coleta_impacto.dtos.LoteDisponivelDTO;
import com.github.clara_f025.ms_coleta_impacto.dtos.ReciboImpactoResponse;
import com.github.clara_f025.ms_coleta_impacto.dtos.ReservarColetaRequest;
import com.github.clara_f025.ms_coleta_impacto.entities.Coleta;
import com.github.clara_f025.ms_coleta_impacto.entities.Coletor;
import com.github.clara_f025.ms_coleta_impacto.entities.StatusColeta;
import com.github.clara_f025.ms_coleta_impacto.exceptions.BusinessRuleException;
import com.github.clara_f025.ms_coleta_impacto.exceptions.ConflictException;
import com.github.clara_f025.ms_coleta_impacto.exceptions.ResourceNotFoundException;
import com.github.clara_f025.ms_coleta_impacto.exceptions.ServiceUnavailableException;
import com.github.clara_f025.ms_coleta_impacto.repositories.ColetaRepository;
import com.github.clara_f025.ms_coleta_impacto.repositories.ColetorRepository;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Regras da operação de coleta. Chamadas ao ms-gerador-lotes (OpenFeign) ocorrem fora de
 * transação de banco; as alterações locais são aplicadas depois, de forma atômica.
 */
@Service
public class ColetaService {

    private static final Logger log = LoggerFactory.getLogger(ColetaService.class);
    private static final String MSG_FALHA_GERADOR = "Falha ao comunicar com ms-gerador-lotes";

    private final ColetaRepository coletaRepository;
    private final ColetorRepository coletorRepository;
    private final GeradorLotesClient geradorLotesClient;
    private final ImpactoAmbientalService impactoAmbientalService;
    private final TransactionTemplate transactionTemplate;

    public ColetaService(ColetaRepository coletaRepository, ColetorRepository coletorRepository,
                         GeradorLotesClient geradorLotesClient, ImpactoAmbientalService impactoAmbientalService,
                         TransactionTemplate transactionTemplate) {
        this.coletaRepository = coletaRepository;
        this.coletorRepository = coletorRepository;
        this.geradorLotesClient = geradorLotesClient;
        this.impactoAmbientalService = impactoAmbientalService;
        this.transactionTemplate = transactionTemplate;
    }

    public List<LoteDisponivelDTO> listarLotesDisponiveis(String cep) {
        try {
            return geradorLotesClient.listarDisponiveis(cep).stream()
                    .map(lote -> new LoteDisponivelDTO(lote.protocolo(), lote.cep(), lote.bairro(),
                            lote.tipoResiduo(), lote.pesoKg(), lote.janelaHorario()))
                    .toList();
        } catch (FeignException e) {
            throw new ServiceUnavailableException(MSG_FALHA_GERADOR, e);
        }
    }

    @Transactional(readOnly = true)
    public List<ColetaResponse> listar() {
        return coletaRepository.findAll().stream().map(ColetaResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ColetaResponse buscarPorId(Long id) {
        return ColetaResponse.from(obterColeta(id));
    }

    /** Reserva o lote: altera o status do Lote para RESERVADO no ms-gerador-lotes via OpenFeign. */
    public ColetaResponse reservar(ReservarColetaRequest request) {
        Coletor coletor = obterColetor(request.idColetor());
        if (coletaRepository.existsByProtocoloLote(request.protocolo())) {
            throw new ConflictException("Já existe uma coleta registrada para o lote " + request.protocolo());
        }

        LoteRemotoResponse lote;
        try {
            lote = geradorLotesClient.reservar(request.protocolo(), new ReservarLoteRemoteRequest(coletor.getCpfCnpj()));
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Lote não encontrado. Protocolo: " + request.protocolo());
        } catch (FeignException.Conflict e) {
            throw new ConflictException("Lote não está mais disponível para reserva");
        } catch (FeignException e) {
            throw new ServiceUnavailableException(MSG_FALHA_GERADOR, e);
        }

        try {
            Coleta coleta = new Coleta();
            coleta.setProtocoloLote(lote.protocolo());
            coleta.setColetor(coletor);
            coleta.setPesoKg(lote.pesoKg());
            coleta.setStatus(StatusColeta.RESERVADA);
            coleta.setDtReserva(LocalDateTime.now());
            coleta.setObservacao(request.observacao());
            return ColetaResponse.from(coletaRepository.save(coleta));
        } catch (RuntimeException e) {
            compensarReserva(lote.protocolo());
            throw e;
        }
    }

    @Transactional
    public ColetaResponse atualizar(Long id, AtualizarColetaRequest request) {
        Coleta coleta = obterColeta(id);
        coleta.setObservacao(request.observacao());
        return ColetaResponse.from(coletaRepository.save(coleta));
    }

    /** Cancela a reserva: devolve o lote a DISPONIVEL no ms-gerador-lotes e remove a coleta. */
    public void cancelar(Long id) {
        Coleta coleta = obterColeta(id);
        if (coleta.getStatus() == StatusColeta.CONCLUIDA) {
            throw new ConflictException("Coleta concluída não pode ser excluída");
        }
        try {
            geradorLotesClient.liberar(coleta.getProtocoloLote());
        } catch (FeignException.NotFound | FeignException.Conflict e) {
            log.warn("Lote {} já não está reservado no ms-gerador-lotes; removendo apenas a coleta local",
                    coleta.getProtocoloLote());
        } catch (FeignException e) {
            throw new ServiceUnavailableException(MSG_FALHA_GERADOR, e);
        }
        coletaRepository.delete(coleta);
    }

    /**
     * Baixa da coleta: o ms-gerador-lotes valida o PIN e altera o Lote para COLETADO (via OpenFeign);
     * em seguida são calculados o impacto ESG e atualizados o saldo e o CO2 do coletor.
     */
    public ReciboImpactoResponse baixar(String protocolo, BaixaColetaRequest request) {
        Coletor coletor = obterColetor(request.idColetor());
        Optional<Coleta> existente = coletaRepository.findByProtocoloLote(protocolo);
        existente.ifPresent(coleta -> {
            if (coleta.getStatus() == StatusColeta.CONCLUIDA) {
                throw new ConflictException("Coleta já concluída para o lote " + protocolo);
            }
            if (!coleta.getColetor().getCpfCnpj().equals(coletor.getCpfCnpj())) {
                throw new ConflictException("Lote reservado por outro coletor");
            }
        });

        LoteRemotoResponse lote;
        try {
            lote = geradorLotesClient.coletar(protocolo,
                    new ColetarLoteRemoteRequest(coletor.getCpfCnpj(), request.pinInformado()));
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Lote não encontrado. Protocolo: " + protocolo);
        } catch (FeignException.BadRequest e) {
            throw new BusinessRuleException("Código PIN inválido");
        } catch (FeignException.Conflict e) {
            throw new ConflictException("Lote indisponível para baixa (já coletado ou reservado por outro coletor)");
        } catch (FeignException e) {
            throw new ServiceUnavailableException(MSG_FALHA_GERADOR, e);
        }

        BigDecimal pesoKg = lote.pesoKg();
        BigDecimal co2EvitadoKg = impactoAmbientalService.calcularCo2EvitadoKg(pesoKg);
        long pontos = impactoAmbientalService.calcularPontos(pesoKg);

        transactionTemplate.executeWithoutResult(status -> {
            Coleta coleta = existente.orElseGet(Coleta::new);
            Coletor coletorAtual = coletorRepository.buscarParaAtualizacao(coletor.getCpfCnpj())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Coletor não encontrado. Documento: " + coletor.getCpfCnpj()));

            if (coleta.getId() == null) {
                coleta.setProtocoloLote(protocolo);
                coleta.setColetor(coletorAtual);
                coleta.setDtReserva(LocalDateTime.now());
            }
            coleta.setPesoKg(pesoKg);
            coleta.setCo2EvitadoKg(co2EvitadoKg);
            coleta.setPontosGanhos(pontos);
            coleta.setStatus(StatusColeta.CONCLUIDA);
            coleta.setDtConclusao(LocalDateTime.now());
            coletaRepository.save(coleta);

            coletorAtual.setSaldoKg(coletorAtual.getSaldoKg().add(pesoKg));
            coletorAtual.setCo2EvitadoKg(coletorAtual.getCo2EvitadoKg().add(co2EvitadoKg));
            coletorAtual.setPontosAcumulados(coletorAtual.getPontosAcumulados() + pontos);
            coletorRepository.save(coletorAtual);
        });

        String mensagem = "Coleta confirmada! Você evitou " + co2EvitadoKg + " kg de CO2 e ganhou " + pontos + " pontos.";
        return new ReciboImpactoResponse(true, protocolo, pesoKg, co2EvitadoKg, pontos, mensagem);
    }

    private void compensarReserva(String protocolo) {
        try {
            geradorLotesClient.liberar(protocolo);
        } catch (FeignException e) {
            log.error("Falha ao desfazer a reserva do lote {} no ms-gerador-lotes: {}", protocolo, e.getMessage());
        }
    }

    private Coleta obterColeta(Long id) {
        return coletaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coleta não encontrada. ID: " + id));
    }

    private Coletor obterColetor(String cpfCnpj) {
        return coletorRepository.findById(cpfCnpj)
                .orElseThrow(() -> new ResourceNotFoundException("Coletor não encontrado. Documento: " + cpfCnpj));
    }
}
