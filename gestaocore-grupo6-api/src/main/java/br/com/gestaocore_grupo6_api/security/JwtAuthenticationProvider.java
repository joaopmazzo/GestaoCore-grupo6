package br.com.gestaocore_grupo6_api.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider {

  @Value("${jwt.secret}")
  private String secretKey;

  public DecodedJWT validateToken(String token) {
    if (token == null || token.isEmpty()) {
      return null;
    }

    token = token.replace("Bearer ", "").trim();

    Algorithm algorithm = Algorithm.HMAC256(secretKey);

    try {
      return JWT.require(algorithm)
          .build()
          .verify(token);
    } catch (JWTVerificationException ex) {
      return null;
    }
  }

}
