package br.com.gestaocore_grupo6_api.security;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import br.com.gestaocore_grupo6_api.dto.request.UsuarioLoginRequestDTO;
import br.com.gestaocore_grupo6_api.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import br.com.gestaocore_grupo6_api.dto.response.UsuarioLoginResponseDTO;
import br.com.gestaocore_grupo6_api.entity.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.naming.AuthenticationException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

  private final UsuarioRepository usuarioRepository;
  private final PasswordEncoder passwordEncoder;

  @Value("${jwt.secret}")
  private String secretKey;

  @Value("${jwt.expiration:3600000}")
  private Long tokenExpiration;

  public UsuarioLoginResponseDTO autenticar(UsuarioLoginRequestDTO dto) throws AuthenticationException {
    Usuario usuario = usuarioRepository
            .findByEmail(dto.email())
            .orElseThrow(() -> new EntityNotFoundException("Email/Senha incorreta"));

    if (!passwordEncoder.matches(dto.senha(), usuario.getSenha())) {
      throw new AuthenticationException();
    }

    Algorithm algorithm = Algorithm.HMAC256(secretKey);
    Instant expirationTime = Instant.now().plusMillis(tokenExpiration);

    String token = JWT.create()
        .withSubject(usuario.getId().toString())
        .withClaim("role", usuario.getRole().name())
        .withExpiresAt(Date.from(expirationTime))
        .sign(algorithm);

    String formattedExpiration = DateTimeFormatter
            .ofPattern("dd/MM/yyyy HH:mm:ss")
            .withZone(ZoneId.systemDefault())
            .format(expirationTime);

    return new UsuarioLoginResponseDTO(token, formattedExpiration);
  }

}
