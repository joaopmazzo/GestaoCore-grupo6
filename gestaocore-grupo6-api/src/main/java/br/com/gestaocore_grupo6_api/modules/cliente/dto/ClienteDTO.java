package br.com.gestaocore_grupo6_api.modules.cliente.dto;

import br.com.gestaocore_grupo6_api.modules.cliente.entity.Endereco;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.UUID;

public record ClienteDTO(
        UUID id,
        String nome,
        String email,
        String telefone,

        @JsonUnwrapped
        Endereco endereco
) {}
