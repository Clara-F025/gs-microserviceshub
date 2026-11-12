INSERT INTO tb_gerador (cnpj, razao_social, cep, telefone, status, dt_cadastro) VALUES
('11222333000181', 'Supermercado Boa Compra Ltda', '01310100', '1131234567', 'ATIVO',   '2026-01-10 09:00:00'),
('44555666000199', 'Restaurante Sabor Verde ME',    '04538133', '11987654321', 'INATIVO', '2026-01-12 10:30:00'),
('12345678000195', 'Industria Plasticos Alfa S/A',  '20040020', '2132109876', 'ATIVO',   '2026-01-15 14:15:00');

INSERT INTO tb_lote (protocolo, pin_retirada, cnpj_gerador, responsavel, telefone, cep, logradouro, bairro,
                     tipo_residuo, peso_kg, janela_horario, dt_registro, status, id_coletor, dt_coleta) VALUES
('LT20260101-000001', '1234', '11222333000181', 'Maria Souza',  '11987651234', '01310100',
 'Avenida Paulista', 'Bela Vista', 'PLASTICO', 25.50, '08:00-12:00', '2026-01-20 08:00:00', 'DISPONIVEL', NULL, NULL),
('LT20260101-000002', '4321', '11222333000181', 'Maria Souza',  '11987651234', '01310100',
 'Avenida Paulista', 'Bela Vista', 'PAPEL', 40.00, '13:00-17:00', '2026-01-20 08:30:00', 'DISPONIVEL', NULL, NULL),
('LT20260101-000003', '1111', '12345678000195', 'Carlos Lima',  '21999998888', '20040020',
 'Rua da Assembleia', 'Centro', 'VIDRO', 15.00, '09:00-11:00', '2026-01-21 09:00:00', 'DISPONIVEL', NULL, NULL),
('LT20260101-000004', '9999', '12345678000195', 'Carlos Lima',  '21999998888', '20040020',
 'Rua da Assembleia', 'Centro', 'METAL', 30.00, '14:00-16:00', '2026-01-22 10:00:00', 'COLETADO',
 '11122233344', '2026-01-23 14:30:00');
