package br.com.gestaocore_grupo6_api.modules.cliente.dto;

import br.com.gestaocore_grupo6_api.modules.cliente.entity.Endereco;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CriarClienteRequestDTO(
        @NotBlank(message = "O nome é obrigatório")
        String nome,

        @NotBlank(message = "O email é obrigatório")
        @Email(message = "O email deve ser válido")
        String email,

        @NotBlank(message = "O telefone é obrigatório")
        String telefone,

        @NotNull(message = "O endereço é obrigatório")
        @Valid
        @JsonUnwrapped
        Endereco endereco
) {}
