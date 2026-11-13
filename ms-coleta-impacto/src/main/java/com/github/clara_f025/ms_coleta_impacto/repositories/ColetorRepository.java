package com.github.clara_f025.ms_coleta_impacto.repositories;

import com.github.clara_f025.ms_coleta_impacto.entities.Coletor;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ColetorRepository extends JpaRepository<Coletor, Long> {

    Optional<Coletor> findByCpfCnpj(String cpfCnpj);

    boolean existsByCpfCnpj(String cpfCnpj);

    @Query("select c from Coletor c where c.cpfCnpj = :cpfCnpj")
    Optional<Coletor> buscarParaAtualizacao(@Param("cpfCnpj") String cpfCnpj);
}
