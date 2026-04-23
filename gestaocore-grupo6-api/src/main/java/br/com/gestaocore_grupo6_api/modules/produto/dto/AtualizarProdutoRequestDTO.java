package br.com.gestaocore_grupo6_api.modules.produto.dto;

import jakarta.validation.constraints.DecimalMin;

public record AtualizarProdutoRequestDTO(
        String nome,

        String categoria,

        @DecimalMin(value = "0.1", message = "O preço deve ser maior que 0")
        Double preco,

        @DecimalMin(value = "0.1", message = "A quantidade em estoque deve ser maior que 0")
        Double qtdEstoque
) {}
