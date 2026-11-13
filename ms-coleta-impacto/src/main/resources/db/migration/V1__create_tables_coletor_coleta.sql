CREATE TABLE tb_coletor (
    id                 BIGINT         NOT NULL AUTO_INCREMENT,
    cpf_cnpj           VARCHAR(14)    NOT NULL,
    nome               VARCHAR(100)   NOT NULL,
    telefone           VARCHAR(20)    NOT NULL,
    cep_atuacao        CHAR(8)        NOT NULL,
    saldo_kg           DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    co2_evitado_kg     DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    pontos_acumulados  BIGINT         NOT NULL DEFAULT 0,
    CONSTRAINT pk_tb_coletor PRIMARY KEY (id),
    CONSTRAINT uk_tb_coletor_cpf_cnpj UNIQUE (cpf_cnpj)
);

CREATE TABLE tb_coleta (
    id              BIGINT         NOT NULL AUTO_INCREMENT,
    protocolo_lote  VARCHAR(30)    NOT NULL,
    id_coletor      VARCHAR(14)    NOT NULL,
    peso_kg         DECIMAL(10, 2) NOT NULL,
    co2_evitado_kg  DECIMAL(12, 2) NULL,
    pontos_ganhos   BIGINT         NULL,
    status          VARCHAR(15)    NOT NULL,
    dt_reserva      DATETIME       NOT NULL,
    dt_conclusao    DATETIME       NULL,
    observacao      VARCHAR(255)   NULL,
    CONSTRAINT pk_tb_coleta PRIMARY KEY (id),
    CONSTRAINT uk_coleta_protocolo UNIQUE (protocolo_lote),
    CONSTRAINT fk_coleta_coletor FOREIGN KEY (id_coletor) REFERENCES tb_coletor (cpf_cnpj)
);

CREATE INDEX idx_coleta_status ON tb_coleta (status);
