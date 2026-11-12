package com.github.clara_f025.ms_gerador_lotes.repositories;

import com.github.clara_f025.ms_gerador_lotes.entities.Lote;
import com.github.clara_f025.ms_gerador_lotes.entities.StatusLote;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LoteRepository extends JpaRepository<Lote, String> {

    List<Lote> findByStatus(StatusLote status);

    List<Lote> findByStatusAndCep(StatusLote status, String cep);

    boolean existsByGeradorCnpj(String cnpj);

    @Query("select l from Lote l where l.protocolo = :protocolo")
    Optional<Lote> buscarParaAtualizacao(@Param("protocolo") String protocolo);
}
