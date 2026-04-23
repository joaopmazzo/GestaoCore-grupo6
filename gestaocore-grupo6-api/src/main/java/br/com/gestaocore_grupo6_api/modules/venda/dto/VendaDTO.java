package br.com.gestaocore_grupo6_api.modules.venda.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record VendaDTO(
        UUID id,
        LocalDateTime dataVenda,
        UUID clienteId,
        List<ItemVendaDTO> itens
) {}
