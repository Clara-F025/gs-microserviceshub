package com.github.clara_f025.ms_coleta_impacto.services;

import com.github.clara_f025.ms_coleta_impacto.dtos.ColetorRequest;
import com.github.clara_f025.ms_coleta_impacto.dtos.ColetorResponse;
import com.github.clara_f025.ms_coleta_impacto.dtos.ColetorUpdateRequest;
import com.github.clara_f025.ms_coleta_impacto.entities.Coletor;
import com.github.clara_f025.ms_coleta_impacto.exceptions.ConflictException;
import com.github.clara_f025.ms_coleta_impacto.exceptions.ResourceNotFoundException;
import com.github.clara_f025.ms_coleta_impacto.repositories.ColetaRepository;
import com.github.clara_f025.ms_coleta_impacto.repositories.ColetorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ColetorService {

    private final ColetorRepository coletorRepository;
    private final ColetaRepository coletaRepository;

    public ColetorService(ColetorRepository coletorRepository, ColetaRepository coletaRepository) {
        this.coletorRepository = coletorRepository;
        this.coletaRepository = coletaRepository;
    }

    @Transactional(readOnly = true)
    public List<ColetorResponse> listar() {
        return coletorRepository.findAll().stream().map(ColetorResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ColetorResponse buscarPorCpfCnpj(String cpfCnpj) {
        return ColetorResponse.from(obterColetor(cpfCnpj));
    }

    @Transactional
    public ColetorResponse criar(ColetorRequest request) {
        if (coletorRepository.existsByCpfCnpj(request.cpfCnpj())) {
            throw new ConflictException("Já existe um coletor cadastrado com o documento " + request.cpfCnpj());
        }
        Coletor coletor = new Coletor();
        coletor.setCpfCnpj(request.cpfCnpj());
        coletor.setNome(request.nome());
        coletor.setTelefone(request.telefone());
        coletor.setCepAtuacao(request.cepAtuacao());
        return ColetorResponse.from(coletorRepository.save(coletor));
    }

    @Transactional
    public ColetorResponse atualizar(String cpfCnpj, ColetorUpdateRequest request) {
        Coletor coletor = obterColetor(cpfCnpj);
        coletor.setNome(request.nome());
        coletor.setTelefone(request.telefone());
        coletor.setCepAtuacao(request.cepAtuacao());
        return ColetorResponse.from(coletorRepository.save(coletor));
    }

    @Transactional
    public void excluir(String cpfCnpj) {
        Coletor coletor = obterColetor(cpfCnpj);
        if (coletaRepository.existsByColetorCpfCnpj(cpfCnpj)) {
            throw new ConflictException("Coletor possui coletas registradas e não pode ser excluído");
        }
        coletorRepository.delete(coletor);
    }

    private Coletor obterColetor(String cpfCnpj) {
        return coletorRepository.findByCpfCnpj(cpfCnpj)
                .orElseThrow(() -> new ResourceNotFoundException("Coletor não encontrado. Documento: " + cpfCnpj));
    }
}
