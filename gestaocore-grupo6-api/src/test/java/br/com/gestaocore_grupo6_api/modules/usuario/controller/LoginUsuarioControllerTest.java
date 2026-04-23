package br.com.gestaocore_grupo6_api.modules.usuario.controller;

import br.com.gestaocore_grupo6_api.modules.usuario.dto.UsuarioLoginRequestDTO;
import br.com.gestaocore_grupo6_api.modules.usuario.entity.UsuarioEntity;
import br.com.gestaocore_grupo6_api.modules.usuario.enums.Role;
import br.com.gestaocore_grupo6_api.modules.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes de Integração - LoginUsuarioController")
@ActiveProfiles("test")
@Transactional
class LoginUsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private UsuarioLoginRequestDTO loginRequestDTO;

    @BeforeEach
    void setUp() {
        // Limpar dados anteriores
        usuarioRepository.deleteAll();

        // Criar usuário de teste com senha encriptada
        String senhaEncriptada = passwordEncoder.encode("senha123");
        usuarioRepository.save(UsuarioEntity.builder()
                .nome("João Silva")
                .email("joao@email.com")
                .senha(senhaEncriptada)
                .role(Role.USER)
                .build());

        loginRequestDTO = new UsuarioLoginRequestDTO(
                "joao@email.com",
                "senha123"
        );
    }

    @Test
    @DisplayName("POST /usuario/login - Deve autenticar usuário com credenciais válidas")
    void deveAutenticarUsuarioComCredenciaisValidas() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/usuario/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.expiresIn").exists())
                .andExpect(jsonPath("$.expiresIn").isNotEmpty());
    }

    @Test
    @DisplayName("POST /usuario/login - Deve retornar 404 quando email não existe")
    void deveRetornar404QuandoEmailNaoExiste() throws Exception {
        // Arrange
        UsuarioLoginRequestDTO loginInvalido = new UsuarioLoginRequestDTO(
                "naoexiste@email.com",
                "senha123"
        );

        // Act & Assert
        mockMvc.perform(post("/usuario/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginInvalido)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /usuario/login - Deve retornar erro quando senha incorreta")
    void deveRetornarErroQuandoSenhaIncorreta() throws Exception {
        // Arrange
        UsuarioLoginRequestDTO loginComSenhaErrada = new UsuarioLoginRequestDTO(
                "joao@email.com",
                "senhaerrada"
        );

        // Act & Assert
        mockMvc.perform(post("/usuario/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginComSenhaErrada)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /usuario/login - Deve retornar 400 quando email está vazio")
    void deveRetornar400QuandoEmailVazio() throws Exception {
        // Arrange
        UsuarioLoginRequestDTO dtoInvalido = new UsuarioLoginRequestDTO(
                "",  // Email vazio
                "senha123"
        );

        // Act & Assert
        mockMvc.perform(post("/usuario/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /usuario/login - Deve retornar 400 quando senha está vazia")
    void deveRetornar400QuandoSenhaVazia() throws Exception {
        // Arrange
        UsuarioLoginRequestDTO dtoInvalido = new UsuarioLoginRequestDTO(
                "joao@email.com",
                ""  // Senha vazia
        );

        // Act & Assert
        mockMvc.perform(post("/usuario/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /usuario/login - Deve retornar 400 quando email tem formato inválido")
    void deveRetornar400QuandoEmailFormatoInvalido() throws Exception {
        // Arrange
        UsuarioLoginRequestDTO dtoInvalido = new UsuarioLoginRequestDTO(
                "emailinvalido",  // Email sem formato válido
                "senha123"
        );

        // Act & Assert
        mockMvc.perform(post("/usuario/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /usuario/login - Deve aceitar diferentes emails válidos")
    void deveAceitarDiferentesEmailsValidos() throws Exception {
        // Arrange
        String senhaEncriptada = passwordEncoder.encode("pass123");
        usuarioRepository.save(UsuarioEntity.builder()
                .nome("User Admin")
                .email("admin@domain.org")
                .senha(senhaEncriptada)
                .role(Role.ADMIN)
                .build());

        UsuarioLoginRequestDTO dto1 = new UsuarioLoginRequestDTO("joao@email.com", "senha123");
        UsuarioLoginRequestDTO dto2 = new UsuarioLoginRequestDTO("admin@domain.org", "pass123");

        // Act & Assert - Primeiro email
        mockMvc.perform(post("/usuario/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto1)))
                .andExpect(status().isOk());

        // Act & Assert - Segundo email
        mockMvc.perform(post("/usuario/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto2)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /usuario/login - Deve retornar token JWT e data de expiração")
    void deveRetornarTokenEDataExpiracao() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/usuario/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isString())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.expiresIn").isString())
                .andExpect(jsonPath("$.expiresIn").isNotEmpty());
    }

    @Test
    @DisplayName("POST /usuario/login - Deve retornar 400 quando body está vazio")
    void deveRetornar400QuandoBodyVazio() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/usuario/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /usuario/login - Deve processar request com content-type correto")
    void deveProcessarRequestComContentTypeCorreto() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/usuario/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
