package com.github.clara_f025.ms_coleta_impacto.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "tb_coleta")
@Getter
@Setter
public class Coleta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "protocolo_lote", length = 30, nullable = false, unique = true)
    private String protocoloLote;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_coletor", referencedColumnName = "cpf_cnpj", nullable = false)
    private Coletor coletor;

    @Column(name = "peso_kg", precision = 10, scale = 2, nullable = false)
    private BigDecimal pesoKg;

    @Column(name = "co2_evitado_kg", precision = 12, scale = 2)
    private BigDecimal co2EvitadoKg;

    @Column(name = "pontos_ganhos")
    private Long pontosGanhos;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 15, nullable = false)
    private StatusColeta status;

    @Column(name = "dt_reserva", nullable = false)
    private LocalDateTime dtReserva;

    @Column(name = "dt_conclusao")
    private LocalDateTime dtConclusao;

    @Column(name = "observacao", length = 255)
    private String observacao;

}
