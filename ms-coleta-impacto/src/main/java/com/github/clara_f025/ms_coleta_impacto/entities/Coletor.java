package com.github.clara_f025.ms_coleta_impacto.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "tb_coletor")
@Getter
@Setter
public class Coletor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "cpf_cnpj", length = 14, nullable = false, unique = true)
    private String cpfCnpj;

    @Column(name = "nome", length = 100, nullable = false)
    private String nome;

    @Column(name = "telefone", length = 20, nullable = false)
    private String telefone;

    @Column(name = "cep_atuacao", length = 8, nullable = false)
    private String cepAtuacao;

    @Column(name = "saldo_kg", precision = 12, scale = 2, nullable = false)
    private BigDecimal saldoKg = BigDecimal.ZERO;

    @Column(name = "co2_evitado_kg", precision = 12, scale = 2, nullable = false)
    private BigDecimal co2EvitadoKg = BigDecimal.ZERO;

    @Column(name = "pontos_acumulados", nullable = false)
    private Long pontosAcumulados = 0L;

}
