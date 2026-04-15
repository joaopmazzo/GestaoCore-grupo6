CREATE TABLE usuario (
    id UUID PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);

INSERT INTO usuario (id, nome, email, senha, role) VALUES
('11111111-1111-1111-1111-111111111111', 'Admin User', 'mazzojp@gmail.com', '$2a$12$2GLy/07xt8e2iWtks1PqG.OFqkUBOphs5UK9FaKi/TqZjmWeS5CeK', 'ADMIN');

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

CREATE TABLE produto (
    id UUID PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    categoria VARCHAR(255) NOT NULL,
    preco DOUBLE NOT NULL,
    qtd_estoque DOUBLE NOT NULL
);

CREATE TABLE venda (
    id UUID PRIMARY KEY,
    data_venda TIMESTAMP,
    cliente_id UUID NOT NULL,
    FOREIGN KEY (cliente_id) REFERENCES cliente(id)
);

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
