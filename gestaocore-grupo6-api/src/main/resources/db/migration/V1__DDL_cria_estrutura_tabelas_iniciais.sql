CREATE TABLE usuario (
    id UUID PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);

INSERT INTO usuario (id, nome, email, senha, role) VALUES
('11111111-1111-1111-1111-111111111111', 'Admin User', 'admin@gmail.com', '$2a$12$2GLy/07xt8e2iWtks1PqG.OFqkUBOphs5UK9FaKi/TqZjmWeS5CeK', 'ADMIN'),
('22222222-2222-2222-2222-222222222222', 'João Silva', 'joao.silva@email.com', '$2a$12$2GLy/07xt8e2iWtks1PqG.OFqkUBOphs5UK9FaKi/TqZjmWeS5CeK', 'USER');

CREATE TABLE cliente (
    id UUID PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    telefone VARCHAR(20) NOT NULL,
    rua VARCHAR(255) NOT NULL,
    numero VARCHAR(20) NOT NULL,
    cidade VARCHAR(255) NOT NULL,
    estado VARCHAR(2) NOT NULL,
    cep VARCHAR(10) NOT NULL,
    complemento VARCHAR(255) NOT NULL
);

INSERT INTO cliente (id, nome, email, telefone, rua, numero, cidade, estado, cep, complemento) VALUES
('33333333-3333-3333-3333-333333333333', 'Maria Santos', 'maria.santos@email.com', '(11) 98765-4321', 'Rua das Flores', '123', 'São Paulo', 'SP', '01234-567', 'Apto 45'),
('44444444-4444-4444-4444-444444444444', 'Pedro Oliveira', 'pedro.oliveira@email.com', '(21) 99876-5432', 'Av. Principal', '456', 'Rio de Janeiro', 'RJ', '20123-456', 'Casa');

CREATE TABLE produto (
    id UUID PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    categoria VARCHAR(255) NOT NULL,
    preco DOUBLE NOT NULL,
    qtd_estoque DOUBLE NOT NULL
);

INSERT INTO produto (id, nome, categoria, preco, qtd_estoque) VALUES
('55555555-5555-5555-5555-555555555555', 'Notebook Dell Inspiron', 'Eletrônicos', 3500.00, 15),
('66666666-6666-6666-6666-666666666666', 'Mouse Logitech MX', 'Periféricos', 250.00, 50),
('77777777-7777-7777-7777-777777777777', 'Teclado Mecânico RGB', 'Periféricos', 450.00, 30);

CREATE TABLE venda (
    id UUID PRIMARY KEY,
    data_venda TIMESTAMP,
    cliente_id UUID NOT NULL,
    status VARCHAR(20) NOT NULL,
    FOREIGN KEY (cliente_id) REFERENCES cliente(id)
);

INSERT INTO venda (id, data_venda, cliente_id, status) VALUES
('88888888-8888-8888-8888-888888888888', '2024-04-20 10:30:00', '33333333-3333-3333-3333-333333333333', 'CONCLUIDA'),
('99999999-9999-9999-9999-999999999999', '2024-04-21 14:15:00', '44444444-4444-4444-4444-444444444444', 'PENDENTE');

CREATE TABLE item_venda (
    id UUID PRIMARY KEY,
    quantidade DOUBLE NOT NULL,
    valor_total DOUBLE NOT NULL,
    valor_na_venda DOUBLE NOT NULL,
    produto_id UUID NOT NULL,
    venda_id UUID NOT NULL,
    FOREIGN KEY (produto_id) REFERENCES produto(id),
    FOREIGN KEY (venda_id) REFERENCES venda(id)
);

INSERT INTO item_venda (id, quantidade, valor_total, valor_na_venda, produto_id, venda_id) VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 1, 3500.00, 3500.00, '55555555-5555-5555-5555-555555555555', '88888888-8888-8888-8888-888888888888'),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 2, 500.00, 250.00, '66666666-6666-6666-6666-666666666666', '88888888-8888-8888-8888-888888888888'),
('cccccccc-cccc-cccc-cccc-cccccccccccc', 1, 450.00, 450.00, '77777777-7777-7777-7777-777777777777', '99999999-9999-9999-9999-999999999999');
