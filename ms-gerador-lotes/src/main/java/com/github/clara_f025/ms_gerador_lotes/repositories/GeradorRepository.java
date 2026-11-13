package com.github.clara_f025.ms_gerador_lotes.repositories;

import com.github.clara_f025.ms_gerador_lotes.entities.Gerador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GeradorRepository extends JpaRepository<Gerador, Long> {
	Optional<Gerador> findByCnpj(String cnpj);

	boolean existsByCnpj(String cnpj);
}
