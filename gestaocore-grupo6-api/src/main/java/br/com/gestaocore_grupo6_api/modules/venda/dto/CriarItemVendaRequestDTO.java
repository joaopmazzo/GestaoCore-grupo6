package br.com.gestaocore_grupo6_api.modules.venda.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record CriarItemVendaRequestDTO(
        @NotNull(message = "O ID do produto é obrigatório")
        UUID produtoId,

        @NotNull(message = "A quantidade é obrigatória")
        @Positive(message = "A quantidade deve ser maior que zero")
        Double quantidade
) {}
