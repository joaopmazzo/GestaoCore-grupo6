package br.com.gestaocore_grupo6_api.dto.request;

public record UsuarioLoginRequestDTO(
        String email,
        String senha
) {}
