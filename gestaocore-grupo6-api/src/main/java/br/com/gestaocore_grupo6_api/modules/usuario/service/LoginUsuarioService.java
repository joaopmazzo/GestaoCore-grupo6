package br.com.gestaocore_grupo6_api.modules.usuario.service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import br.com.gestaocore_grupo6_api.modules.usuario.dto.UsuarioLoginRequestDTO;
import br.com.gestaocore_grupo6_api.modules.usuario.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import br.com.gestaocore_grupo6_api.modules.usuario.dto.UsuarioLoginResponseDTO;
import br.com.gestaocore_grupo6_api.modules.usuario.entity.UsuarioEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.naming.AuthenticationException;

@Service
@RequiredArgsConstructor
public class LoginUsuarioService {

  private final UsuarioRepository usuarioRepository;
  private final PasswordEncoder passwordEncoder;

  @Value("${jwt.secret}")
  private String secretKey;

  @Value("${jwt.expiration:3600000}")
  private Long tokenExpiration;

  public UsuarioLoginResponseDTO autenticar(UsuarioLoginRequestDTO dto) throws AuthenticationException {
    UsuarioEntity usuarioEntity = usuarioRepository
            .findByEmail(dto.email())
            .orElseThrow(() -> new EntityNotFoundException("Email/Senha incorreta"));

    if (!passwordEncoder.matches(dto.senha(), usuarioEntity.getSenha())) {
      throw new AuthenticationException("Email/Senha incorreta");
    }

    Algorithm algorithm = Algorithm.HMAC256(secretKey);
    Instant expirationTime = Instant.now().plusMillis(tokenExpiration);

    String token = JWT.create()
        .withSubject(usuarioEntity.getId().toString())
        .withClaim("role", usuarioEntity.getRole().name())
        .withExpiresAt(Date.from(expirationTime))
        .sign(algorithm);

    String formattedExpiration = DateTimeFormatter
            .ofPattern("dd/MM/yyyy HH:mm:ss")
            .withZone(ZoneId.systemDefault())
            .format(expirationTime);

    return new UsuarioLoginResponseDTO(token, formattedExpiration);
  }

}
