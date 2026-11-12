-- O lote LT20260101-000004 (ms-gerador-lotes) esta COLETADO; esta coleta registra o impacto correspondente
-- (30 kg x 1,5 = 45 kg de CO2 evitado; 30 kg x 10 = 300 pontos).
INSERT INTO tb_coletor (cpf_cnpj, nome, telefone, cep_atuacao, saldo_kg, co2_evitado_kg, pontos_acumulados) VALUES
('11122233344',    'Joao da Silva Reciclagem',      '11988887777', '01310100', 30.00, 45.00, 300),
('22233344455',    'Ana Pereira Catadora Autonoma', '21977776666', '20040020',  0.00,  0.00,   0),
('98765432000110', 'Cooperativa Verde Futuro',      '1133334444',  '04538133',  0.00,  0.00,   0);

INSERT INTO tb_coleta (protocolo_lote, id_coletor, peso_kg, co2_evitado_kg, pontos_ganhos, status,
                       dt_reserva, dt_conclusao, observacao) VALUES
('LT20260101-000004', '11122233344', 30.00, 45.00, 300, 'CONCLUIDA',
 '2026-01-22 18:00:00', '2026-01-23 14:30:00', 'Coleta de metal concluida com PIN validado');
