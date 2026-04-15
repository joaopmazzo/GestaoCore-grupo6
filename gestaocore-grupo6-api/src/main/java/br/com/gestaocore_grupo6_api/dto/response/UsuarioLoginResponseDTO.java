package br.com.gestaocore_grupo6_api.dto.response;

public record UsuarioLoginResponseDTO(
        String accessToken,
        String expiresIn
) {}
