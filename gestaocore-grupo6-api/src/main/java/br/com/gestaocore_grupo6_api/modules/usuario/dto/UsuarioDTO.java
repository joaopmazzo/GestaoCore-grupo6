package br.com.gestaocore_grupo6_api.modules.usuario.dto;

import java.util.UUID;

public record UsuarioDTO(
        UUID id,
        String nome,
        String email,
        String role
) {}
