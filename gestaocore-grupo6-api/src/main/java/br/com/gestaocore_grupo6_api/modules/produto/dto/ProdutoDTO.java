package br.com.gestaocore_grupo6_api.modules.produto.dto;

import java.util.UUID;

public record ProdutoDTO(
        UUID id,
        String nome,
        String categoria,
        Double preco,
        Double qtdEstoque
) {}
