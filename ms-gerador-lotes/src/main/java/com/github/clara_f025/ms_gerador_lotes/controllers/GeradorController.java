package com.github.clara_f025.ms_gerador_lotes.controllers;

import com.github.clara_f025.ms_gerador_lotes.dtos.GeradorRequest;
import com.github.clara_f025.ms_gerador_lotes.dtos.GeradorResponse;
import com.github.clara_f025.ms_gerador_lotes.dtos.GeradorStatusResponse;
import com.github.clara_f025.ms_gerador_lotes.dtos.GeradorUpdateRequest;
import com.github.clara_f025.ms_gerador_lotes.services.GeradorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/geradores")
@Tag(name = "Geradores", description = "Cadastro e consulta de pontos geradores de recicláveis")
public class GeradorController {

    private final GeradorService geradorService;

    public GeradorController(GeradorService geradorService) {
        this.geradorService = geradorService;
    }

    @GetMapping
    @Operation(summary = "Lista todos os geradores")
    public ResponseEntity<List<GeradorResponse>> listar() {
        return ResponseEntity.ok(geradorService.listar());
    }

    @GetMapping("/{cnpj}")
    @Operation(summary = "Busca um gerador pelo CNPJ")
    public ResponseEntity<GeradorResponse> buscarPorCnpj(@PathVariable String cnpj) {
        return ResponseEntity.ok(geradorService.buscarPorCnpj(cnpj));
    }

    @GetMapping("/{cnpj}/status")
    @Operation(summary = "Verifica se o gerador possui cadastro ativo e agendamento liberado")
    public ResponseEntity<GeradorStatusResponse> consultarStatus(@PathVariable String cnpj) {
        return ResponseEntity.ok(geradorService.consultarStatus(cnpj));
    }

    @PostMapping
    @Operation(summary = "Cadastra um novo gerador (status inicial ATIVO)")
    public ResponseEntity<GeradorResponse> criar(@Valid @RequestBody GeradorRequest request) {
        GeradorResponse response = geradorService.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{cnpj}").buildAndExpand(response.cnpj()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{cnpj}")
    @Operation(summary = "Atualiza os dados cadastrais de um gerador")
    public ResponseEntity<GeradorResponse> atualizar(@PathVariable String cnpj,
                                                     @Valid @RequestBody GeradorUpdateRequest request) {
        return ResponseEntity.ok(geradorService.atualizar(cnpj, request));
    }

    @PatchMapping("/{cnpj}/ativar")
    @Operation(summary = "Ativa o gerador (libera agendamentos)")
    public ResponseEntity<GeradorResponse> ativar(@PathVariable String cnpj) {
        return ResponseEntity.ok(geradorService.ativar(cnpj));
    }

    @PatchMapping("/{cnpj}/inativar")
    @Operation(summary = "Inativa o gerador (bloqueia novos agendamentos)")
    public ResponseEntity<GeradorResponse> inativar(@PathVariable String cnpj) {
        return ResponseEntity.ok(geradorService.inativar(cnpj));
    }

    @DeleteMapping("/{cnpj}")
    @Operation(summary = "Exclui um gerador sem lotes cadastrados")
    public ResponseEntity<Void> excluir(@PathVariable String cnpj) {
        geradorService.excluir(cnpj);
        return ResponseEntity.noContent().build();
    }
}
