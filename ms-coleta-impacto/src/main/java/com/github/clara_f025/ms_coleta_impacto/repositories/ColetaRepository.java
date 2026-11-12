package com.github.clara_f025.ms_coleta_impacto.repositories;

import com.github.clara_f025.ms_coleta_impacto.entities.Coleta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ColetaRepository extends JpaRepository<Coleta, Long> {

    Optional<Coleta> findByProtocoloLote(String protocoloLote);

    boolean existsByProtocoloLote(String protocoloLote);

    boolean existsByColetorCpfCnpj(String cpfCnpj);
}
