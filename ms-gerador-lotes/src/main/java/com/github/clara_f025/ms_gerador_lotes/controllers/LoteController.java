package com.github.clara_f025.ms_gerador_lotes.controllers;

import com.github.clara_f025.ms_gerador_lotes.dtos.AtualizarLoteRequest;
import com.github.clara_f025.ms_gerador_lotes.dtos.ColetarLoteRequest;
import com.github.clara_f025.ms_gerador_lotes.dtos.CriarLoteRequest;
import com.github.clara_f025.ms_gerador_lotes.dtos.LoteCriadoResponse;
import com.github.clara_f025.ms_gerador_lotes.dtos.LoteResponse;
import com.github.clara_f025.ms_gerador_lotes.dtos.ReservarLoteRequest;
import com.github.clara_f025.ms_gerador_lotes.entities.StatusLote;
import com.github.clara_f025.ms_gerador_lotes.services.LoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/lotes")
@Tag(name = "Lotes", description = "Gestão de lotes de recicláveis e transições de status")
public class LoteController {

    private final LoteService loteService;

    public LoteController(LoteService loteService) {
        this.loteService = loteService;
    }

    @GetMapping
    @Operation(summary = "Lista lotes, com filtros opcionais por status e CEP")
    public ResponseEntity<List<LoteResponse>> listar(@RequestParam(required = false) StatusLote status,
                                                     @RequestParam(required = false) String cep) {
        return ResponseEntity.ok(loteService.listar(status, cep));
    }

    @GetMapping("/disponiveis")
    @Operation(summary = "Lista lotes com status DISPONIVEL (opcionalmente por CEP)")
    public ResponseEntity<List<LoteResponse>> listarDisponiveis(@RequestParam(required = false) String cep) {
        return ResponseEntity.ok(loteService.listarDisponiveis(cep));
    }

    @GetMapping("/{protocolo}")
    @Operation(summary = "Busca um lote pelo protocolo")
    public ResponseEntity<LoteResponse> buscarPorProtocolo(@PathVariable String protocolo) {
        return ResponseEntity.ok(loteService.buscarPorProtocolo(protocolo));
    }

    @PostMapping
    @Operation(summary = "Agenda um novo lote (status inicial DISPONIVEL) e retorna protocolo e PIN")
    public ResponseEntity<LoteCriadoResponse> criar(@Valid @RequestBody CriarLoteRequest request) {
        LoteCriadoResponse response = loteService.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{protocolo}").buildAndExpand(response.protocolo()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{protocolo}")
    @Operation(summary = "Atualiza um lote ainda DISPONIVEL")
    public ResponseEntity<LoteResponse> atualizar(@PathVariable String protocolo,
                                                  @Valid @RequestBody AtualizarLoteRequest request) {
        return ResponseEntity.ok(loteService.atualizar(protocolo, request));
    }

    @DeleteMapping("/{protocolo}")
    @Operation(summary = "Exclui um lote ainda DISPONIVEL")
    public ResponseEntity<Void> excluir(@PathVariable String protocolo) {
        loteService.excluir(protocolo);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{protocolo}/reservar")
    @Operation(summary = "Reserva o lote para um coletor (DISPONIVEL -> RESERVADO). Consumido pelo ms-coleta-impacto")
    public ResponseEntity<LoteResponse> reservar(@PathVariable String protocolo,
                                                 @Valid @RequestBody ReservarLoteRequest request) {
        return ResponseEntity.ok(loteService.reservar(protocolo, request));
    }

    @PutMapping("/{protocolo}/liberar")
    @Operation(summary = "Cancela a reserva do lote (RESERVADO -> DISPONIVEL). Consumido pelo ms-coleta-impacto")
    public ResponseEntity<LoteResponse> liberar(@PathVariable String protocolo) {
        return ResponseEntity.ok(loteService.liberar(protocolo));
    }

    @PutMapping("/{protocolo}/coletar")
    @Operation(summary = "Confirma a coleta validando o PIN (-> COLETADO). Consumido pelo ms-coleta-impacto")
    public ResponseEntity<LoteResponse> coletar(@PathVariable String protocolo,
                                                @Valid @RequestBody ColetarLoteRequest request) {
        return ResponseEntity.ok(loteService.coletar(protocolo, request));
    }
}
