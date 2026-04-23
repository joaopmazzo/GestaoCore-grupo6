package br.com.gestaocore_grupo6_api.modules.usuario.service;

import br.com.gestaocore_grupo6_api.modules.usuario.dto.UsuarioLoginRequestDTO;
import br.com.gestaocore_grupo6_api.modules.usuario.dto.UsuarioLoginResponseDTO;
import br.com.gestaocore_grupo6_api.modules.usuario.entity.UsuarioEntity;
import br.com.gestaocore_grupo6_api.modules.usuario.enums.Role;
import br.com.gestaocore_grupo6_api.modules.usuario.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import javax.naming.AuthenticationException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - LoginUsuarioService")
class LoginUsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private LoginUsuarioService loginUsuarioService;

    private UsuarioEntity usuarioEntity;
    private UsuarioLoginRequestDTO loginRequestDTO;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(loginUsuarioService, "secretKey", "test-secret-key-with-minimum-256-bits-length");
        ReflectionTestUtils.setField(loginUsuarioService, "tokenExpiration", 3600000L);

        usuarioEntity = UsuarioEntity.builder()
                .id(UUID.randomUUID())
                .nome("João Silva")
                .email("joao@email.com")
                .senha("$2a$10$encodedPassword")
                .role(Role.USER)
                .build();

        loginRequestDTO = new UsuarioLoginRequestDTO(
                "joao@email.com",
                "senha123"
        );
    }

    @Test
    @DisplayName("Deve autenticar usuário com credenciais válidas")
    void deveAutenticarUsuarioComCredenciaisValidas() throws AuthenticationException {
        // Arrange
        when(usuarioRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(usuarioEntity));
        when(passwordEncoder.matches("senha123", "$2a$10$encodedPassword")).thenReturn(true);

        // Act
        UsuarioLoginResponseDTO resultado = loginUsuarioService.autenticar(loginRequestDTO);

        // Assert
        assertNotNull(resultado);
        assertNotNull(resultado.accessToken());
        assertNotNull(resultado.expiresIn());
        assertFalse(resultado.accessToken().isEmpty());
        assertTrue(resultado.expiresIn().matches("\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}:\\d{2}"));
        verify(usuarioRepository, times(1)).findByEmail("joao@email.com");
        verify(passwordEncoder, times(1)).matches("senha123", "$2a$10$encodedPassword");
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException quando email não existe")
    void deveLancarExcecaoQuandoEmailNaoExiste() {
        // Arrange
        when(usuarioRepository.findByEmail("inexistente@email.com")).thenReturn(Optional.empty());

        UsuarioLoginRequestDTO requestInvalido = new UsuarioLoginRequestDTO(
                "inexistente@email.com",
                "senha123"
        );

        // Act & Assert
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> loginUsuarioService.autenticar(requestInvalido)
        );

        assertEquals("Email/Senha incorreta", exception.getMessage());
        verify(usuarioRepository, times(1)).findByEmail("inexistente@email.com");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("Deve lançar AuthenticationException quando senha está incorreta")
    void deveLancarExcecaoQuandoSenhaIncorreta() {
        // Arrange
        when(usuarioRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(usuarioEntity));
        when(passwordEncoder.matches("senhaErrada", "$2a$10$encodedPassword")).thenReturn(false);

        UsuarioLoginRequestDTO requestInvalido = new UsuarioLoginRequestDTO(
                "joao@email.com",
                "senhaErrada"
        );

        // Act & Assert
        AuthenticationException exception = assertThrows(
                AuthenticationException.class,
                () -> loginUsuarioService.autenticar(requestInvalido)
        );

        assertEquals("Email/Senha incorreta", exception.getMessage());
        verify(usuarioRepository, times(1)).findByEmail("joao@email.com");
        verify(passwordEncoder, times(1)).matches("senhaErrada", "$2a$10$encodedPassword");
    }

    @Test
    @DisplayName("Deve gerar token JWT com role correto para usuário ADMIN")
    void deveGerarTokenComRoleAdmin() throws AuthenticationException {
        // Arrange
        UsuarioEntity adminEntity = UsuarioEntity.builder()
                .id(UUID.randomUUID())
                .nome("Admin User")
                .email("admin@email.com")
                .senha("$2a$10$encodedPassword")
                .role(Role.ADMIN)
                .build();

        UsuarioLoginRequestDTO adminLogin = new UsuarioLoginRequestDTO(
                "admin@email.com",
                "adminPass123"
        );

        when(usuarioRepository.findByEmail("admin@email.com")).thenReturn(Optional.of(adminEntity));
        when(passwordEncoder.matches("adminPass123", "$2a$10$encodedPassword")).thenReturn(true);

        // Act
        UsuarioLoginResponseDTO resultado = loginUsuarioService.autenticar(adminLogin);

        // Assert
        assertNotNull(resultado);
        assertNotNull(resultado.accessToken());
        verify(usuarioRepository, times(1)).findByEmail("admin@email.com");
    }

    @Test
    @DisplayName("Deve gerar token JWT com role correto para usuário GERENTE")
    void deveGerarTokenComRoleGerente() throws AuthenticationException {
        // Arrange
        UsuarioEntity gerenteEntity = UsuarioEntity.builder()
                .id(UUID.randomUUID())
                .nome("Gerente User")
                .email("gerente@email.com")
                .senha("$2a$10$encodedPassword")
                .role(Role.GERENTE)
                .build();

        UsuarioLoginRequestDTO gerenteLogin = new UsuarioLoginRequestDTO(
                "gerente@email.com",
                "gerentePass123"
        );

        when(usuarioRepository.findByEmail("gerente@email.com")).thenReturn(Optional.of(gerenteEntity));
        when(passwordEncoder.matches("gerentePass123", "$2a$10$encodedPassword")).thenReturn(true);

        // Act
        UsuarioLoginResponseDTO resultado = loginUsuarioService.autenticar(gerenteLogin);

        // Assert
        assertNotNull(resultado);
        assertNotNull(resultado.accessToken());
        assertNotNull(resultado.expiresIn());
        verify(usuarioRepository, times(1)).findByEmail("gerente@email.com");
    }

    @Test
    @DisplayName("Deve retornar data de expiração formatada corretamente")
    void deveRetornarDataExpiracaoFormatada() throws AuthenticationException {
        // Arrange
        when(usuarioRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(usuarioEntity));
        when(passwordEncoder.matches("senha123", "$2a$10$encodedPassword")).thenReturn(true);

        // Act
        UsuarioLoginResponseDTO resultado = loginUsuarioService.autenticar(loginRequestDTO);

        // Assert
        assertNotNull(resultado.accessToken());
        // Verifica formato: dd/MM/yyyy HH:mm:ss
        assertTrue(resultado.expiresIn().matches("\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}:\\d{2}"),
                "Data deve estar no formato dd/MM/yyyy HH:mm:ss");
    }

    @Test
    @DisplayName("Deve autenticar diferentes usuários com emails diferentes")
    void deveAutenticarDiferentesUsuarios() throws AuthenticationException {
        // Arrange - Primeiro usuário
        UsuarioEntity usuario1 = UsuarioEntity.builder()
                .id(UUID.randomUUID())
                .nome("Usuario 1")
                .email("usuario1@email.com")
                .senha("$2a$10$senha1")
                .role(Role.USER)
                .build();

        UsuarioLoginRequestDTO login1 = new UsuarioLoginRequestDTO("usuario1@email.com", "pass1");

        when(usuarioRepository.findByEmail("usuario1@email.com")).thenReturn(Optional.of(usuario1));
        when(passwordEncoder.matches("pass1", "$2a$10$senha1")).thenReturn(true);

        // Act
        UsuarioLoginResponseDTO resultado1 = loginUsuarioService.autenticar(login1);

        // Arrange - Segundo usuário
        UsuarioEntity usuario2 = UsuarioEntity.builder()
                .id(UUID.randomUUID())
                .nome("Usuario 2")
                .email("usuario2@email.com")
                .senha("$2a$10$senha2")
                .role(Role.ADMIN)
                .build();

        UsuarioLoginRequestDTO login2 = new UsuarioLoginRequestDTO("usuario2@email.com", "pass2");

        when(usuarioRepository.findByEmail("usuario2@email.com")).thenReturn(Optional.of(usuario2));
        when(passwordEncoder.matches("pass2", "$2a$10$senha2")).thenReturn(true);

        // Act
        UsuarioLoginResponseDTO resultado2 = loginUsuarioService.autenticar(login2);

        // Assert
        assertNotNull(resultado1);
        assertNotNull(resultado2);
        assertNotEquals(resultado1.accessToken(), resultado2.accessToken());
        verify(usuarioRepository, times(1)).findByEmail("usuario1@email.com");
        verify(usuarioRepository, times(1)).findByEmail("usuario2@email.com");
    }
}
