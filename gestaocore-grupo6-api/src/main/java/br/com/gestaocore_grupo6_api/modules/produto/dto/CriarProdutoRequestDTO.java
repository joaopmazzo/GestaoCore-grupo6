package br.com.gestaocore_grupo6_api.modules.produto.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CriarProdutoRequestDTO(
        @NotBlank(message = "O nome é obrigatório")
        String nome,

        @NotBlank(message = "A categoria é obrigatória")
        String categoria,

        @NotNull(message = "O preço é obrigatório")
        @DecimalMin(value = "0.1", message = "O preço deve ser maior que 0")
        Double preco,

        @NotNull(message = "A quantidade em estoque é obrigatória")
        @DecimalMin(value = "0.1", message = "A quantidade em estoque deve ser maior que 0")
        Double qtdEstoque
) {}
