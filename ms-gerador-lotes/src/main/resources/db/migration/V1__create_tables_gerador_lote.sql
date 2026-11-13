CREATE TABLE tb_gerador (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    cnpj          CHAR(14)     NOT NULL,
    razao_social  VARCHAR(150) NOT NULL,
    cep           CHAR(8)      NOT NULL,
    telefone      VARCHAR(20)  NOT NULL,
    status        VARCHAR(10)  NOT NULL,
    dt_cadastro   DATETIME     NOT NULL,
    CONSTRAINT pk_tb_gerador PRIMARY KEY (id),
    CONSTRAINT uk_tb_gerador_cnpj UNIQUE (cnpj)
);

CREATE TABLE tb_lote (
    id              BIGINT         NOT NULL AUTO_INCREMENT,
    protocolo       VARCHAR(30)    NOT NULL,
    pin_retirada    CHAR(4)        NOT NULL,
    cnpj_gerador    CHAR(14)       NOT NULL,
    responsavel     VARCHAR(100)   NOT NULL,
    telefone        VARCHAR(20)    NOT NULL,
    cep             CHAR(8)        NOT NULL,
    logradouro      VARCHAR(150)   NULL,
    bairro          VARCHAR(100)   NULL,
    tipo_residuo    VARCHAR(30)    NOT NULL,
    peso_kg         DECIMAL(10, 2) NOT NULL,
    janela_horario  VARCHAR(30)    NOT NULL,
    dt_registro     DATETIME       NOT NULL,
    status          VARCHAR(15)    NOT NULL,
    id_coletor      VARCHAR(14)    NULL,
    dt_coleta       DATETIME       NULL,
    CONSTRAINT pk_tb_lote PRIMARY KEY (id),
    CONSTRAINT uk_tb_lote_protocolo UNIQUE (protocolo),
    CONSTRAINT fk_lote_gerador FOREIGN KEY (cnpj_gerador) REFERENCES tb_gerador (cnpj),
    CONSTRAINT ck_lote_peso_minimo CHECK (peso_kg >= 10.00)
);

CREATE INDEX idx_lote_status_cep ON tb_lote (status, cep);
