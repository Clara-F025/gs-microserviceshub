package com.github.clara_f025.ms_gerador_lotes.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_gerador")
@Getter
@Setter
public class Gerador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "cnpj", length = 14, nullable = false, unique = true)
    private String cnpj;

    @Column(name = "razao_social", length = 150, nullable = false)
    private String razaoSocial;

    @Column(name = "cep", length = 8, nullable = false)
    private String cep;

    @Column(name = "telefone", length = 20, nullable = false)
    private String telefone;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 10, nullable = false)
    private StatusGerador status;

    @Column(name = "dt_cadastro", nullable = false)
    private LocalDateTime dtCadastro;

}
