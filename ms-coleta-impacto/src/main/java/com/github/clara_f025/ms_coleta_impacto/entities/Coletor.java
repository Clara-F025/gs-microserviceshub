package com.github.clara_f025.ms_coleta_impacto.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "tb_coletor")
public class Coletor {

    @Id
    @Column(name = "cpf_cnpj", length = 14, nullable = false)
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

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getCepAtuacao() {
        return cepAtuacao;
    }

    public void setCepAtuacao(String cepAtuacao) {
        this.cepAtuacao = cepAtuacao;
    }

    public BigDecimal getSaldoKg() {
        return saldoKg;
    }

    public void setSaldoKg(BigDecimal saldoKg) {
        this.saldoKg = saldoKg;
    }

    public BigDecimal getCo2EvitadoKg() {
        return co2EvitadoKg;
    }

    public void setCo2EvitadoKg(BigDecimal co2EvitadoKg) {
        this.co2EvitadoKg = co2EvitadoKg;
    }

    public Long getPontosAcumulados() {
        return pontosAcumulados;
    }

    public void setPontosAcumulados(Long pontosAcumulados) {
        this.pontosAcumulados = pontosAcumulados;
    }
}
