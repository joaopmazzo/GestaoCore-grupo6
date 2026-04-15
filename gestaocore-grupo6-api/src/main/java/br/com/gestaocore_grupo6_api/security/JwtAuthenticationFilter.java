package br.com.gestaocore_grupo6_api.security;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.interfaces.DecodedJWT;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    private static final String[] PERMIT_ALL_LIST = {
            "/swagger-ui",
            "/v3/api-docs",
            "/swagger-resources",
            "/actuator",
            "/h2-console",
            "/login"
    };

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            String token = extractTokenFromRequest(request);
            DecodedJWT subjectToken = jwtAuthenticationProvider.validateToken(token);

            request.setAttribute("userID", subjectToken.getSubject());

            String role = subjectToken.getClaim("role").asString();
            SimpleGrantedAuthority authoritie = new SimpleGrantedAuthority("ROLE_" + role.toUpperCase());

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    subjectToken.getSubject(),
                    null,
                    Collections.singleton(authoritie));

            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (Exception ex) {
            logger.error("Falha ao definir autenticação do usuário no contexto de segurança", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        return authHeader.substring(7);
    }

}
