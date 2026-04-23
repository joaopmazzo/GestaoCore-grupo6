package br.com.gestaocore_grupo6_api.modules.venda.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CriarVendaRequestDTO(
        @NotNull(message = "A data da venda é obrigatória")
        LocalDateTime dataVenda,

        @NotNull(message = "O ID do cliente é obrigatório")
        UUID clienteId,

        @NotNull(message = "A lista de itens é obrigatória")
        @NotEmpty(message = "A venda deve ter pelo menos um item")
        @Valid
        List<CriarItemVendaRequestDTO> itens
) {}
