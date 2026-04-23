package br.com.gestaocore_grupo6_api.modules.venda.dto;

import java.util.UUID;

public record ItemVendaDTO(
        UUID id,
        UUID produtoId,
        Double quantidade,
        Double valorTotal,
        Double valorNaVenda
) {}
