package com.github.clara_f025.ms_coleta_impacto.services;

import com.github.clara_f025.ms_coleta_impacto.clients.ColetarLoteRemoteRequest;
import com.github.clara_f025.ms_coleta_impacto.clients.GeradorLotesClient;
import com.github.clara_f025.ms_coleta_impacto.clients.LoteRemotoResponse;
import com.github.clara_f025.ms_coleta_impacto.dtos.BaixaColetaRequest;
import com.github.clara_f025.ms_coleta_impacto.dtos.ReciboImpactoResponse;
import com.github.clara_f025.ms_coleta_impacto.entities.Coleta;
import com.github.clara_f025.ms_coleta_impacto.entities.Coletor;
import com.github.clara_f025.ms_coleta_impacto.entities.StatusColeta;
import com.github.clara_f025.ms_coleta_impacto.exceptions.BusinessRuleException;
import com.github.clara_f025.ms_coleta_impacto.exceptions.ServiceUnavailableException;
import com.github.clara_f025.ms_coleta_impacto.repositories.ColetaRepository;
import com.github.clara_f025.ms_coleta_impacto.repositories.ColetorRepository;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ColetaServiceTest {

    private static final String PROTOCOLO = "LT20260101-000001";
    private static final String COLETOR_ID = "11122233344";

    @Mock
    private ColetaRepository coletaRepository;
    @Mock
    private ColetorRepository coletorRepository;
    @Mock
    private GeradorLotesClient geradorLotesClient;

    private ColetaService coletaService;
    private Coletor coletor;

    @BeforeEach
    void setUp() {
        TransactionTemplate transactionTemplate = new TransactionTemplate(new NoOpTransactionManager());
        coletaService = new ColetaService(coletaRepository, coletorRepository, geradorLotesClient,
                new ImpactoAmbientalService(), transactionTemplate);
        coletor = new Coletor();
        coletor.setCpfCnpj(COLETOR_ID);
        coletor.setNome("Joao");
    }

    private LoteRemotoResponse loteColetado(String peso) {
        return new LoteRemotoResponse(PROTOCOLO, "01310100", "Bela Vista", "PLASTICO", new BigDecimal(peso),
                "08:00-12:00", "COLETADO", COLETOR_ID);
    }

    private FeignException feignException(int status) {
        Request request = Request.create(Request.HttpMethod.PUT, "http://ms-gerador-lotes/api/v1/lotes",
                Collections.emptyMap(), null, StandardCharsets.UTF_8, new RequestTemplate());
        return FeignException.errorStatus("coletar",
                feign.Response.builder().status(status).reason("erro").request(request)
                        .headers(Collections.emptyMap()).build());
    }

    @Test
    void baixaComPinCorretoCalculaImpactoEAtualizaColetor() {
        when(coletorRepository.findByCpfCnpj(COLETOR_ID)).thenReturn(Optional.of(coletor));
        when(coletorRepository.buscarParaAtualizacao(COLETOR_ID)).thenReturn(Optional.of(coletor));
        when(coletaRepository.findByProtocoloLote(PROTOCOLO)).thenReturn(Optional.empty());
        when(geradorLotesClient.coletar(any(), any(ColetarLoteRemoteRequest.class))).thenReturn(loteColetado("25.50"));

        ReciboImpactoResponse recibo = coletaService.baixar(PROTOCOLO, new BaixaColetaRequest(COLETOR_ID, "1234"));

        assertThat(recibo.sucesso()).isTrue();
        assertThat(recibo.co2EvitadoKg()).isEqualByComparingTo("38.25");
        assertThat(recibo.pontosGanhos()).isEqualTo(255L);
        assertThat(coletor.getSaldoKg()).isEqualByComparingTo("25.50");
        assertThat(coletor.getCo2EvitadoKg()).isEqualByComparingTo("38.25");
        assertThat(coletor.getPontosAcumulados()).isEqualTo(255L);

        ArgumentCaptor<Coleta> captor = ArgumentCaptor.forClass(Coleta.class);
        verify(coletaRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(StatusColeta.CONCLUIDA);
    }

    @Test
    void baixaComPinInvalidoRetorna400SemAlterarColetor() {
        when(coletorRepository.findByCpfCnpj(COLETOR_ID)).thenReturn(Optional.of(coletor));
        when(coletaRepository.findByProtocoloLote(PROTOCOLO)).thenReturn(Optional.empty());
        when(geradorLotesClient.coletar(any(), any(ColetarLoteRemoteRequest.class))).thenThrow(feignException(400));

        assertThatThrownBy(() -> coletaService.baixar(PROTOCOLO, new BaixaColetaRequest(COLETOR_ID, "0000")))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Código PIN inválido");
        verify(coletaRepository, never()).save(any());
        assertThat(coletor.getPontosAcumulados()).isZero();
    }

    @Test
    void falhaDoGeradorViraServiceUnavailable() {
        when(coletorRepository.findByCpfCnpj(COLETOR_ID)).thenReturn(Optional.of(coletor));
        when(coletaRepository.findByProtocoloLote(PROTOCOLO)).thenReturn(Optional.empty());
        when(geradorLotesClient.coletar(any(), any(ColetarLoteRemoteRequest.class))).thenThrow(feignException(500));

        assertThatThrownBy(() -> coletaService.baixar(PROTOCOLO, new BaixaColetaRequest(COLETOR_ID, "1234")))
                .isInstanceOf(ServiceUnavailableException.class);
    }

    @Test
    void conversaoEsgSegueAsFormulas() {
        ImpactoAmbientalService impacto = new ImpactoAmbientalService();

        assertThat(impacto.calcularCo2EvitadoKg(new BigDecimal("10"))).isEqualByComparingTo("15.00");
        assertThat(impacto.calcularPontos(new BigDecimal("10"))).isEqualTo(100L);
    }
}
