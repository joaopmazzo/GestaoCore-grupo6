package br.com.gestaocore_grupo6_api.modules.cliente.dto;

import br.com.gestaocore_grupo6_api.modules.cliente.entity.Endereco;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;

public record AtualizarClienteRequestDTO(
        String nome,

        @Email(message = "O email deve ser válido")
        String email,

        String telefone,

        @Valid
        @JsonUnwrapped
        Endereco endereco
) {}
