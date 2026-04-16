package br.com.gestaocore_grupo6_api.modules.usuario.dto;

public record UsuarioLoginResponseDTO(
        String accessToken,
        String expiresIn
) {}
