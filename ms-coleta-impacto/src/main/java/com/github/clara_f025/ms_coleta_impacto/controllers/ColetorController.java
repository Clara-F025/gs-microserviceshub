package com.github.clara_f025.ms_coleta_impacto.controllers;

import com.github.clara_f025.ms_coleta_impacto.dtos.ColetorRequest;
import com.github.clara_f025.ms_coleta_impacto.dtos.ColetorResponse;
import com.github.clara_f025.ms_coleta_impacto.dtos.ColetorUpdateRequest;
import com.github.clara_f025.ms_coleta_impacto.services.ColetorService;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/coletores")
@Tag(name = "Coletores", description = "Cadastro de coletores e acompanhamento do impacto ESG acumulado")
public class ColetorController {

    private final ColetorService coletorService;

    public ColetorController(ColetorService coletorService) {
        this.coletorService = coletorService;
    }

    @GetMapping
    @Operation(summary = "Lista todos os coletores")
    public ResponseEntity<List<ColetorResponse>> listar() {
        return ResponseEntity.ok(coletorService.listar());
    }

    @GetMapping("/{cpfCnpj}")
    @Operation(summary = "Busca um coletor pelo CPF/CNPJ (inclui saldo, CO2 evitado e pontos)")
    public ResponseEntity<ColetorResponse> buscarPorCpfCnpj(@PathVariable String cpfCnpj) {
        return ResponseEntity.ok(coletorService.buscarPorCpfCnpj(cpfCnpj));
    }

    @PostMapping
    @Operation(summary = "Cadastra um coletor (saldo, CO2 e pontos iniciam zerados)")
    public ResponseEntity<ColetorResponse> criar(@Valid @RequestBody ColetorRequest request) {
        ColetorResponse response = coletorService.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{cpfCnpj}").buildAndExpand(response.cpfCnpj()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{cpfCnpj}")
    @Operation(summary = "Atualiza os dados cadastrais de um coletor")
    public ResponseEntity<ColetorResponse> atualizar(@PathVariable String cpfCnpj,
                                                     @Valid @RequestBody ColetorUpdateRequest request) {
        return ResponseEntity.ok(coletorService.atualizar(cpfCnpj, request));
    }

    @DeleteMapping("/{cpfCnpj}")
    @Operation(summary = "Exclui um coletor sem coletas registradas")
    public ResponseEntity<Void> excluir(@PathVariable String cpfCnpj) {
        coletorService.excluir(cpfCnpj);
        return ResponseEntity.noContent().build();
    }
}
