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

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_coleta")
public class Coleta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "protocolo_lote", length = 30, nullable = false, unique = true)
    private String protocoloLote;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_coletor", nullable = false)
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProtocoloLote() {
        return protocoloLote;
    }

    public void setProtocoloLote(String protocoloLote) {
        this.protocoloLote = protocoloLote;
    }

    public Coletor getColetor() {
        return coletor;
    }

    public void setColetor(Coletor coletor) {
        this.coletor = coletor;
    }

    public BigDecimal getPesoKg() {
        return pesoKg;
    }

    public void setPesoKg(BigDecimal pesoKg) {
        this.pesoKg = pesoKg;
    }

    public BigDecimal getCo2EvitadoKg() {
        return co2EvitadoKg;
    }

    public void setCo2EvitadoKg(BigDecimal co2EvitadoKg) {
        this.co2EvitadoKg = co2EvitadoKg;
    }

    public Long getPontosGanhos() {
        return pontosGanhos;
    }

    public void setPontosGanhos(Long pontosGanhos) {
        this.pontosGanhos = pontosGanhos;
    }

    public StatusColeta getStatus() {
        return status;
    }

    public void setStatus(StatusColeta status) {
        this.status = status;
    }

    public LocalDateTime getDtReserva() {
        return dtReserva;
    }

    public void setDtReserva(LocalDateTime dtReserva) {
        this.dtReserva = dtReserva;
    }

    public LocalDateTime getDtConclusao() {
        return dtConclusao;
    }

    public void setDtConclusao(LocalDateTime dtConclusao) {
        this.dtConclusao = dtConclusao;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
