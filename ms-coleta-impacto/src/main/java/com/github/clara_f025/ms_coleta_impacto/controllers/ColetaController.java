package com.github.clara_f025.ms_coleta_impacto.controllers;

import com.github.clara_f025.ms_coleta_impacto.dtos.AtualizarColetaRequest;
import com.github.clara_f025.ms_coleta_impacto.dtos.BaixaColetaRequest;
import com.github.clara_f025.ms_coleta_impacto.dtos.ColetaResponse;
import com.github.clara_f025.ms_coleta_impacto.dtos.LoteDisponivelDTO;
import com.github.clara_f025.ms_coleta_impacto.dtos.ReciboImpactoResponse;
import com.github.clara_f025.ms_coleta_impacto.dtos.ReservarColetaRequest;
import com.github.clara_f025.ms_coleta_impacto.exceptions.ServiceUnavailableException;
import com.github.clara_f025.ms_coleta_impacto.services.ColetaService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/coletas")
@Tag(name = "Coletas", description = "Operação de coleta, custódia (PIN) e impacto ESG")
public class ColetaController {

    private static final Logger log = LoggerFactory.getLogger(ColetaController.class);
    private static final String MSG_INDISPONIVEL =
            "O serviço de lotes (ms-gerador-lotes) está temporariamente indisponível. Tente novamente em instantes.";

    private final ColetaService coletaService;

    public ColetaController(ColetaService coletaService) {
        this.coletaService = coletaService;
    }

    @GetMapping("/disponiveis")
    @CircuitBreaker(name = "geradorLotes", fallbackMethod = "fallbackListarDisponiveis")
    @Operation(summary = "Lista lotes DISPONIVEL (consulta o ms-gerador-lotes), opcionalmente filtrando por CEP")
    public ResponseEntity<List<LoteDisponivelDTO>> listarDisponiveis(@RequestParam(required = false) String cep) {
        return ResponseEntity.ok(coletaService.listarLotesDisponiveis(cep));
    }

    @GetMapping
    @Operation(summary = "Lista todas as coletas")
    public ResponseEntity<List<ColetaResponse>> listar() {
        return ResponseEntity.ok(coletaService.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma coleta pelo ID")
    public ResponseEntity<ColetaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(coletaService.buscarPorId(id));
    }

    @PostMapping
    @CircuitBreaker(name = "geradorLotes", fallbackMethod = "fallbackReservar")
    @Operation(summary = "Reserva um lote para o coletor (Lote: DISPONIVEL -> RESERVADO via OpenFeign)")
    public ResponseEntity<ColetaResponse> reservar(@Valid @RequestBody ReservarColetaRequest request) {
        ColetaResponse response = coletaService.reservar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza a observação de uma coleta")
    public ResponseEntity<ColetaResponse> atualizar(@PathVariable Long id,
                                                    @Valid @RequestBody AtualizarColetaRequest request) {
        return ResponseEntity.ok(coletaService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @CircuitBreaker(name = "geradorLotes", fallbackMethod = "fallbackCancelar")
    @Operation(summary = "Cancela a reserva (Lote: RESERVADO -> DISPONIVEL via OpenFeign) e exclui a coleta")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        coletaService.cancelar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{protocolo}/baixa")
    @CircuitBreaker(name = "geradorLotes", fallbackMethod = "fallbackBaixar")
    @Operation(summary = "Baixa da coleta: valida o PIN no ms-gerador-lotes (Lote -> COLETADO) e retorna o recibo de impacto ESG")
    public ResponseEntity<ReciboImpactoResponse> baixar(@PathVariable String protocolo,
                                                        @Valid @RequestBody BaixaColetaRequest request) {
        return ResponseEntity.ok(coletaService.baixar(protocolo, request));
    }

    public ResponseEntity<List<LoteDisponivelDTO>> fallbackListarDisponiveis(String cep, Throwable e) {
        log.error("Falha ao listar lotes disponíveis (cep={}). Ativando fallback. Erro: {}", cep, e.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(List.of());
    }

    public ResponseEntity<ColetaResponse> fallbackReservar(ReservarColetaRequest request, Throwable e) {
        log.error("Falha ao reservar o lote {}. Ativando fallback. Erro: {}", request.protocolo(), e.getMessage());
        throw new ServiceUnavailableException(MSG_INDISPONIVEL, e);
    }

    public ResponseEntity<Void> fallbackCancelar(Long id, Throwable e) {
        log.error("Falha ao cancelar a coleta {}. Ativando fallback. Erro: {}", id, e.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

    public ResponseEntity<ReciboImpactoResponse> fallbackBaixar(String protocolo, BaixaColetaRequest request, Throwable e) {
        log.error("Falha na baixa do lote {}. Ativando fallback. Erro: {}", protocolo, e.getMessage());
        ReciboImpactoResponse recibo = new ReciboImpactoResponse(false, protocolo, BigDecimal.ZERO, BigDecimal.ZERO,
                0L, MSG_INDISPONIVEL);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(recibo);
    }
}
