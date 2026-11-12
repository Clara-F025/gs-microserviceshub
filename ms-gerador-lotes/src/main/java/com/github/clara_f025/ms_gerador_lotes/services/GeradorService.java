package com.github.clara_f025.ms_gerador_lotes.services;

import com.github.clara_f025.ms_gerador_lotes.dtos.GeradorRequest;
import com.github.clara_f025.ms_gerador_lotes.dtos.GeradorResponse;
import com.github.clara_f025.ms_gerador_lotes.dtos.GeradorStatusResponse;
import com.github.clara_f025.ms_gerador_lotes.dtos.GeradorUpdateRequest;
import com.github.clara_f025.ms_gerador_lotes.entities.Gerador;
import com.github.clara_f025.ms_gerador_lotes.entities.StatusGerador;
import com.github.clara_f025.ms_gerador_lotes.exceptions.ConflictException;
import com.github.clara_f025.ms_gerador_lotes.exceptions.ResourceNotFoundException;
import com.github.clara_f025.ms_gerador_lotes.repositories.GeradorRepository;
import com.github.clara_f025.ms_gerador_lotes.repositories.LoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GeradorService {

    private final GeradorRepository geradorRepository;
    private final LoteRepository loteRepository;

    public GeradorService(GeradorRepository geradorRepository, LoteRepository loteRepository) {
        this.geradorRepository = geradorRepository;
        this.loteRepository = loteRepository;
    }

    @Transactional(readOnly = true)
    public List<GeradorResponse> listar() {
        return geradorRepository.findAll().stream().map(GeradorResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public GeradorResponse buscarPorCnpj(String cnpj) {
        return GeradorResponse.from(obterGerador(cnpj));
    }

    @Transactional(readOnly = true)
    public GeradorStatusResponse consultarStatus(String cnpj) {
        Gerador gerador = obterGerador(cnpj);
        return new GeradorStatusResponse(gerador.getCnpj(), gerador.getStatus(),
                gerador.getStatus() == StatusGerador.ATIVO);
    }

    @Transactional
    public GeradorResponse criar(GeradorRequest request) {
        if (geradorRepository.existsById(request.cnpj())) {
            throw new ConflictException("Já existe um gerador cadastrado com o CNPJ " + request.cnpj());
        }
        Gerador gerador = new Gerador();
        gerador.setCnpj(request.cnpj());
        gerador.setRazaoSocial(request.razaoSocial());
        gerador.setCep(request.cep());
        gerador.setTelefone(request.telefone());
        gerador.setStatus(StatusGerador.ATIVO);
        gerador.setDtCadastro(LocalDateTime.now());
        return GeradorResponse.from(geradorRepository.save(gerador));
    }

    @Transactional
    public GeradorResponse atualizar(String cnpj, GeradorUpdateRequest request) {
        Gerador gerador = obterGerador(cnpj);
        gerador.setRazaoSocial(request.razaoSocial());
        gerador.setCep(request.cep());
        gerador.setTelefone(request.telefone());
        return GeradorResponse.from(geradorRepository.save(gerador));
    }

    @Transactional
    public void excluir(String cnpj) {
        Gerador gerador = obterGerador(cnpj);
        if (loteRepository.existsByGeradorCnpj(cnpj)) {
            throw new ConflictException("Gerador possui lotes cadastrados e não pode ser excluído");
        }
        geradorRepository.delete(gerador);
    }

    @Transactional
    public GeradorResponse ativar(String cnpj) {
        return alterarStatus(cnpj, StatusGerador.ATIVO);
    }

    @Transactional
    public GeradorResponse inativar(String cnpj) {
        return alterarStatus(cnpj, StatusGerador.INATIVO);
    }

    private GeradorResponse alterarStatus(String cnpj, StatusGerador novoStatus) {
        Gerador gerador = obterGerador(cnpj);
        gerador.setStatus(novoStatus);
        return GeradorResponse.from(geradorRepository.save(gerador));
    }

    private Gerador obterGerador(String cnpj) {
        return geradorRepository.findById(cnpj)
                .orElseThrow(() -> new ResourceNotFoundException("Gerador não encontrado. CNPJ: " + cnpj));
    }
}
