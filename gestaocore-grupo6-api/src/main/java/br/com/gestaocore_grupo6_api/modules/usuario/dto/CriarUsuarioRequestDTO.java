package br.com.gestaocore_grupo6_api.modules.usuario.dto;

import br.com.gestaocore_grupo6_api.modules.usuario.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CriarUsuarioRequestDTO(
        @NotBlank(message = "O nome é obrigatório")
        @Size(min = 3, max = 255, message = "O nome deve conter no mínimo 3 caracteres")
        String nome,

        @NotBlank(message = "O email é obrigatório")
        @Email(message = "O email deve ser válido")
        String email,

        @NotBlank(message = "A senha é obrigatória")
        @Size(min = 6, max = 12, message = "Senha deve ter entre 6 e 12 caracteres")
        String senha,

        Role role
) {
    public CriarUsuarioRequestDTO {
        if (role == null) {
            role = Role.USER;
        }
    }
}
