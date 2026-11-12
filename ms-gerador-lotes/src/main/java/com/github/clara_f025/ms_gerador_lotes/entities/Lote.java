package com.github.clara_f025.ms_gerador_lotes.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_lote")
public class Lote {

    @Id
    @Column(name = "protocolo", length = 30, nullable = false)
    private String protocolo;

    @Column(name = "pin_retirada", length = 4, nullable = false)
    private String pinRetirada;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cnpj_gerador", nullable = false)
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

    public String getProtocolo() {
        return protocolo;
    }

    public void setProtocolo(String protocolo) {
        this.protocolo = protocolo;
    }

    public String getPinRetirada() {
        return pinRetirada;
    }

    public void setPinRetirada(String pinRetirada) {
        this.pinRetirada = pinRetirada;
    }

    public Gerador getGerador() {
        return gerador;
    }

    public void setGerador(Gerador gerador) {
        this.gerador = gerador;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getTipoResiduo() {
        return tipoResiduo;
    }

    public void setTipoResiduo(String tipoResiduo) {
        this.tipoResiduo = tipoResiduo;
    }

    public BigDecimal getPesoKg() {
        return pesoKg;
    }

    public void setPesoKg(BigDecimal pesoKg) {
        this.pesoKg = pesoKg;
    }

    public String getJanelaHorario() {
        return janelaHorario;
    }

    public void setJanelaHorario(String janelaHorario) {
        this.janelaHorario = janelaHorario;
    }

    public LocalDateTime getDtRegistro() {
        return dtRegistro;
    }

    public void setDtRegistro(LocalDateTime dtRegistro) {
        this.dtRegistro = dtRegistro;
    }

    public StatusLote getStatus() {
        return status;
    }

    public void setStatus(StatusLote status) {
        this.status = status;
    }

    public String getIdColetor() {
        return idColetor;
    }

    public void setIdColetor(String idColetor) {
        this.idColetor = idColetor;
    }

    public LocalDateTime getDtColeta() {
        return dtColeta;
    }

    public void setDtColeta(LocalDateTime dtColeta) {
        this.dtColeta = dtColeta;
    }
}
