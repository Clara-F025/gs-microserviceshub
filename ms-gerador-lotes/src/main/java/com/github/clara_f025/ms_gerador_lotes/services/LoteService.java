package com.github.clara_f025.ms_gerador_lotes.services;

import com.github.clara_f025.ms_gerador_lotes.clients.ViaCepClient;
import com.github.clara_f025.ms_gerador_lotes.clients.ViaCepResponse;
import com.github.clara_f025.ms_gerador_lotes.dtos.AtualizarLoteRequest;
import com.github.clara_f025.ms_gerador_lotes.dtos.ColetarLoteRequest;
import com.github.clara_f025.ms_gerador_lotes.dtos.CriarLoteRequest;
import com.github.clara_f025.ms_gerador_lotes.dtos.LoteCriadoResponse;
import com.github.clara_f025.ms_gerador_lotes.dtos.LoteResponse;
import com.github.clara_f025.ms_gerador_lotes.dtos.ReservarLoteRequest;
import com.github.clara_f025.ms_gerador_lotes.entities.Gerador;
import com.github.clara_f025.ms_gerador_lotes.entities.Lote;
import com.github.clara_f025.ms_gerador_lotes.entities.StatusGerador;
import com.github.clara_f025.ms_gerador_lotes.entities.StatusLote;
import com.github.clara_f025.ms_gerador_lotes.exceptions.BusinessRuleException;
import com.github.clara_f025.ms_gerador_lotes.exceptions.ConflictException;
import com.github.clara_f025.ms_gerador_lotes.exceptions.ResourceNotFoundException;
import com.github.clara_f025.ms_gerador_lotes.repositories.GeradorRepository;
import com.github.clara_f025.ms_gerador_lotes.repositories.LoteRepository;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class LoteService {

    private static final Logger log = LoggerFactory.getLogger(LoteService.class);
    private static final BigDecimal PESO_MINIMO_KG = new BigDecimal("10.0");
    private static final String MSG_PESO_MINIMO = "Peso mínimo de 10kg";
    private static final String MSG_PIN_INVALIDO = "Código PIN inválido";

    private final LoteRepository loteRepository;
    private final GeradorRepository geradorRepository;
    private final PinGeneratorService pinGeneratorService;
    private final ViaCepClient viaCepClient;
    private final SecureRandom random = new SecureRandom();

    public LoteService(LoteRepository loteRepository, GeradorRepository geradorRepository,
                       PinGeneratorService pinGeneratorService, ViaCepClient viaCepClient) {
        this.loteRepository = loteRepository;
        this.geradorRepository = geradorRepository;
        this.pinGeneratorService = pinGeneratorService;
        this.viaCepClient = viaCepClient;
    }

    @Transactional(readOnly = true)
    public List<LoteResponse> listar(StatusLote status, String cep) {
        List<Lote> lotes;
        if (status != null && cep != null) {
            lotes = loteRepository.findByStatusAndCep(status, cep);
        } else if (status != null) {
            lotes = loteRepository.findByStatus(status);
        } else {
            lotes = loteRepository.findAll();
        }
        return lotes.stream().map(LoteResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<LoteResponse> listarDisponiveis(String cep) {
        return listar(StatusLote.DISPONIVEL, cep);
    }

    @Transactional(readOnly = true)
    public LoteResponse buscarPorProtocolo(String protocolo) {
        return LoteResponse.from(obterLote(protocolo));
    }

    /**
     * Cria o lote. A consulta ao ViaCEP é feita fora de transação para não manter conexão
     * com o banco aberta durante a chamada HTTP externa.
     */
    public LoteCriadoResponse criar(CriarLoteRequest request) {
        Gerador gerador = geradorRepository.findById(request.cnpj())
                .orElseThrow(() -> new ResourceNotFoundException("Gerador não encontrado. CNPJ: " + request.cnpj()));
        if (gerador.getStatus() != StatusGerador.ATIVO) {
            throw new BusinessRuleException("Gerador inativo: agendamento não liberado");
        }
        validarPesoMinimo(request.pesoKg());
        ViaCepResponse endereco = consultarCep(request.cep());

        Lote lote = new Lote();
        lote.setProtocolo(gerarProtocolo());
        lote.setPinRetirada(pinGeneratorService.gerarPin());
        lote.setGerador(gerador);
        lote.setResponsavel(request.responsavel());
        lote.setTelefone(request.telefone());
        lote.setCep(request.cep());
        if (endereco != null) {
            lote.setLogradouro(endereco.logradouro());
            lote.setBairro(endereco.bairro());
        }
        lote.setTipoResiduo(request.tipoResiduo());
        lote.setPesoKg(request.pesoKg());
        lote.setJanelaHorario(request.janelaHorario());
        lote.setDtRegistro(LocalDateTime.now());
        lote.setStatus(StatusLote.DISPONIVEL);

        Lote salvo = loteRepository.save(lote);
        return new LoteCriadoResponse(salvo.getProtocolo(), salvo.getPinRetirada(), salvo.getDtRegistro(),
                salvo.getStatus());
    }

    @Transactional
    public LoteResponse atualizar(String protocolo, AtualizarLoteRequest request) {
        Lote lote = obterLoteParaAtualizacao(protocolo);
        if (lote.getStatus() != StatusLote.DISPONIVEL) {
            throw new ConflictException("Apenas lotes DISPONIVEL podem ser alterados. Status atual: " + lote.getStatus());
        }
        validarPesoMinimo(request.pesoKg());
        lote.setResponsavel(request.responsavel());
        lote.setTelefone(request.telefone());
        lote.setTipoResiduo(request.tipoResiduo());
        lote.setPesoKg(request.pesoKg());
        lote.setJanelaHorario(request.janelaHorario());
        return LoteResponse.from(loteRepository.save(lote));
    }

    @Transactional
    public void excluir(String protocolo) {
        Lote lote = obterLoteParaAtualizacao(protocolo);
        if (lote.getStatus() != StatusLote.DISPONIVEL) {
            throw new ConflictException("Apenas lotes DISPONIVEL podem ser excluídos. Status atual: " + lote.getStatus());
        }
        loteRepository.delete(lote);
    }

    /** Transição DISPONIVEL -> RESERVADO, acionada pelo ms-coleta-impacto via OpenFeign. */
    @Transactional
    public LoteResponse reservar(String protocolo, ReservarLoteRequest request) {
        Lote lote = obterLoteParaAtualizacao(protocolo);
        if (lote.getStatus() != StatusLote.DISPONIVEL) {
            throw new ConflictException("Lote não está disponível para reserva. Status atual: " + lote.getStatus());
        }
        lote.setStatus(StatusLote.RESERVADO);
        lote.setIdColetor(request.idColetor());
        return LoteResponse.from(loteRepository.save(lote));
    }

    /** Transição RESERVADO -> DISPONIVEL (cancelamento de reserva), acionada pelo ms-coleta-impacto. */
    @Transactional
    public LoteResponse liberar(String protocolo) {
        Lote lote = obterLoteParaAtualizacao(protocolo);
        if (lote.getStatus() != StatusLote.RESERVADO) {
            throw new ConflictException("Apenas lotes RESERVADO podem ser liberados. Status atual: " + lote.getStatus());
        }
        lote.setStatus(StatusLote.DISPONIVEL);
        lote.setIdColetor(null);
        return LoteResponse.from(loteRepository.save(lote));
    }

    /**
     * Transição (DISPONIVEL | RESERVADO) -> COLETADO, acionada pelo ms-coleta-impacto.
     * A baixa só é aceita se o PIN informado for igual ao PIN gravado no lote.
     */
    @Transactional
    public LoteResponse coletar(String protocolo, ColetarLoteRequest request) {
        Lote lote = obterLoteParaAtualizacao(protocolo);
        if (lote.getStatus() == StatusLote.COLETADO) {
            throw new ConflictException("Lote já foi coletado");
        }
        if (lote.getStatus() == StatusLote.RESERVADO && !request.idColetor().equals(lote.getIdColetor())) {
            throw new ConflictException("Lote reservado por outro coletor");
        }
        if (!pinCorreto(lote.getPinRetirada(), request.pinInformado())) {
            throw new BusinessRuleException(MSG_PIN_INVALIDO);
        }
        lote.setStatus(StatusLote.COLETADO);
        lote.setIdColetor(request.idColetor());
        lote.setDtColeta(LocalDateTime.now());
        return LoteResponse.from(loteRepository.save(lote));
    }

    private boolean pinCorreto(String pinGravado, String pinInformado) {
        return MessageDigest.isEqual(
                pinGravado.getBytes(StandardCharsets.UTF_8),
                pinInformado.getBytes(StandardCharsets.UTF_8));
    }

    private void validarPesoMinimo(BigDecimal pesoKg) {
        if (pesoKg == null || pesoKg.compareTo(PESO_MINIMO_KG) < 0) {
            throw new BusinessRuleException(MSG_PESO_MINIMO);
        }
    }

    /**
     * Recupera logradouro e bairro no ViaCEP. CEP inexistente invalida o cadastro;
     * indisponibilidade do ViaCEP não impede o agendamento (endereço fica em branco).
     */
    private ViaCepResponse consultarCep(String cep) {
        try {
            ViaCepResponse resposta = viaCepClient.buscarPorCep(cep);
            if (resposta == null || Boolean.TRUE.equals(resposta.erro())) {
                throw new BusinessRuleException("CEP não encontrado: " + cep);
            }
            return resposta;
        } catch (FeignException.BadRequest e) {
            throw new BusinessRuleException("CEP inválido: " + cep);
        } catch (FeignException e) {
            log.warn("ViaCEP indisponível para o CEP {}. Lote será criado sem logradouro/bairro. Erro: {}",
                    cep, e.getMessage());
            return null;
        }
    }

    private String gerarProtocolo() {
        String data = LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String protocolo;
        do {
            protocolo = "LT" + data + "-" + String.format("%06d", random.nextInt(1_000_000));
        } while (loteRepository.existsById(protocolo));
        return protocolo;
    }

    private Lote obterLote(String protocolo) {
        return loteRepository.findById(protocolo)
                .orElseThrow(() -> new ResourceNotFoundException("Lote não encontrado. Protocolo: " + protocolo));
    }

    private Lote obterLoteParaAtualizacao(String protocolo) {
        return loteRepository.buscarParaAtualizacao(protocolo)
                .orElseThrow(() -> new ResourceNotFoundException("Lote não encontrado. Protocolo: " + protocolo));
    }
}
