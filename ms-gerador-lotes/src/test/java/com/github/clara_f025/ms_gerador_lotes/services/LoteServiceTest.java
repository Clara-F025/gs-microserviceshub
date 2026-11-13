package com.github.clara_f025.ms_gerador_lotes.services;

import com.github.clara_f025.ms_gerador_lotes.clients.ViaCepClient;
import com.github.clara_f025.ms_gerador_lotes.clients.ViaCepResponse;
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
import com.github.clara_f025.ms_gerador_lotes.repositories.GeradorRepository;
import com.github.clara_f025.ms_gerador_lotes.repositories.LoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoteServiceTest {

    private static final String CNPJ = "11222333000181";
    private static final String COLETOR = "11122233344";

    @Mock
    private LoteRepository loteRepository;
    @Mock
    private GeradorRepository geradorRepository;
    @Mock
    private ViaCepClient viaCepClient;

    private LoteService loteService;
    private Gerador gerador;

    @BeforeEach
    void setUp() {
        loteService = new LoteService(loteRepository, geradorRepository, new PinGeneratorService(), viaCepClient);
        gerador = new Gerador();
        gerador.setCnpj(CNPJ);
        gerador.setStatus(StatusGerador.ATIVO);
    }

    private CriarLoteRequest request(String peso) {
        return new CriarLoteRequest(CNPJ, "Maria Souza", "11987651234", "01310100", "PLASTICO",
                new BigDecimal(peso), "08:00-12:00");
    }

    private Lote lote(StatusLote status, String pin) {
        Lote lote = new Lote();
        lote.setProtocolo("LT20260101-000001");
        lote.setPinRetirada(pin);
        lote.setGerador(gerador);
        lote.setStatus(status);
        lote.setPesoKg(new BigDecimal("25.00"));
        lote.setDtRegistro(LocalDateTime.now());
        return lote;
    }

    @Test
    void criaLoteDisponivelComPinDeQuatroDigitos() {
        when(geradorRepository.findByCnpj(CNPJ)).thenReturn(Optional.of(gerador));
        when(viaCepClient.buscarPorCep("01310100"))
                .thenReturn(new ViaCepResponse("01310-100", "Avenida Paulista", "Bela Vista", "Sao Paulo", "SP", null));
        when(loteRepository.save(any(Lote.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LoteCriadoResponse response = loteService.criar(request("10.0"));

        assertThat(response.status()).isEqualTo(StatusLote.DISPONIVEL);
        assertThat(response.pin()).matches("\\d{4}");
        assertThat(response.protocolo()).startsWith("LT");
    }

    @Test
    void recusaPesoMenorQueDezQuilos() {
        when(geradorRepository.findByCnpj(CNPJ)).thenReturn(Optional.of(gerador));

        assertThatThrownBy(() -> loteService.criar(request("9.99")))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Peso mínimo de 10kg");
        verify(loteRepository, never()).save(any());
    }

    @Test
    void recusaGeradorInativo() {
        gerador.setStatus(StatusGerador.INATIVO);
        when(geradorRepository.findByCnpj(CNPJ)).thenReturn(Optional.of(gerador));

        assertThatThrownBy(() -> loteService.criar(request("20")))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void reservaLoteDisponivel() {
        Lote lote = lote(StatusLote.DISPONIVEL, "1234");
        when(loteRepository.buscarParaAtualizacao(lote.getProtocolo())).thenReturn(Optional.of(lote));
        when(loteRepository.save(lote)).thenReturn(lote);

        LoteResponse response = loteService.reservar(lote.getProtocolo(), new ReservarLoteRequest(COLETOR));

        assertThat(response.status()).isEqualTo(StatusLote.RESERVADO);
        assertThat(response.idColetor()).isEqualTo(COLETOR);
    }

    @Test
    void naoReservaLoteJaReservado() {
        Lote lote = lote(StatusLote.RESERVADO, "1234");
        when(loteRepository.buscarParaAtualizacao(lote.getProtocolo())).thenReturn(Optional.of(lote));

        assertThatThrownBy(() -> loteService.reservar(lote.getProtocolo(), new ReservarLoteRequest(COLETOR)))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void coletaComPinCorretoMarcaLoteComoColetado() {
        Lote lote = lote(StatusLote.DISPONIVEL, "1234");
        when(loteRepository.buscarParaAtualizacao(lote.getProtocolo())).thenReturn(Optional.of(lote));
        when(loteRepository.save(lote)).thenReturn(lote);

        LoteResponse response = loteService.coletar(lote.getProtocolo(), new ColetarLoteRequest(COLETOR, "1234"));

        assertThat(response.status()).isEqualTo(StatusLote.COLETADO);
        assertThat(response.dtColeta()).isNotNull();
    }

    @Test
    void coletaComPinIncorretoEhRecusada() {
        Lote lote = lote(StatusLote.DISPONIVEL, "1234");
        when(loteRepository.buscarParaAtualizacao(lote.getProtocolo())).thenReturn(Optional.of(lote));

        assertThatThrownBy(() -> loteService.coletar(lote.getProtocolo(), new ColetarLoteRequest(COLETOR, "0000")))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Código PIN inválido");
        assertThat(lote.getStatus()).isEqualTo(StatusLote.DISPONIVEL);
    }

    @Test
    void naoColetaLoteReservadoPorOutroColetor() {
        Lote lote = lote(StatusLote.RESERVADO, "1234");
        lote.setIdColetor("99988877766");
        when(loteRepository.buscarParaAtualizacao(lote.getProtocolo())).thenReturn(Optional.of(lote));

        assertThatThrownBy(() -> loteService.coletar(lote.getProtocolo(), new ColetarLoteRequest(COLETOR, "1234")))
                .isInstanceOf(ConflictException.class);
    }
}
