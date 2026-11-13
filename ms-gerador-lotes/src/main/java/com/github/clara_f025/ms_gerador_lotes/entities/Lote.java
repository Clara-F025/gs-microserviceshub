package com.github.clara_f025.ms_gerador_lotes.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_lote")
@Getter
@Setter
public class Lote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "protocolo", length = 30, nullable = false, unique = true)
    private String protocolo;

    @Column(name = "pin_retirada", length = 4, nullable = false)
    private String pinRetirada;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cnpj_gerador", referencedColumnName = "cnpj", nullable = false)
    private Gerador gerador;

    @Column(name = "responsavel", length = 100, nullable = false)
    private String responsavel;

    @Column(name = "telefone", length = 20, nullable = false)
    private String telefone;

    @Column(name = "cep", length = 8, nullable = false)
    private String cep;

    @Column(name = "logradouro", length = 150)
    private String logradouro;

    @Column(name = "bairro", length = 100)
    private String bairro;

    @Column(name = "tipo_residuo", length = 30, nullable = false)
    private String tipoResiduo;

    @Column(name = "peso_kg", precision = 10, scale = 2, nullable = false)
    private BigDecimal pesoKg;

    @Column(name = "janela_horario", length = 30, nullable = false)
    private String janelaHorario;

    @Column(name = "dt_registro", nullable = false)
    private LocalDateTime dtRegistro;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 15, nullable = false)
    private StatusLote status;

    @Column(name = "id_coletor", length = 14)
    private String idColetor;

    @Column(name = "dt_coleta")
    private LocalDateTime dtColeta;

}
