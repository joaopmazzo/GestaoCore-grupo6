package br.com.gestaocore_grupo6_api.modules.usuario.controller;

import br.com.gestaocore_grupo6_api.modules.usuario.dto.AtualizarUsuarioRequestDTO;
import br.com.gestaocore_grupo6_api.modules.usuario.dto.CriarUsuarioRequestDTO;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes de Integração - UsuarioController")
@ActiveProfiles("test")
@Transactional
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private UUID usuarioId;
    private UsuarioEntity usuario;
    private AtualizarUsuarioRequestDTO atualizarUsuarioDTO;

    @BeforeEach
    void setUp() {
        // Limpar dados anteriores
        usuarioRepository.deleteAll();

        // Criar e salvar usuário de teste
        usuario = usuarioRepository.save(UsuarioEntity.builder()
                .nome("João Silva")
                .email("joao@email.com")
                .senha("$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy")
                .role(Role.USER)
                .build());

        usuarioId = usuario.getId();

        atualizarUsuarioDTO = new AtualizarUsuarioRequestDTO(
                "João Silva Atualizado",
                "joao.novo@email.com",
                "novaSenha123",
                Role.ADMIN
        );
    }

    @Test
    @WithMockUser
    @DisplayName("GET /usuario - Deve retornar lista de usuários")
    void deveRetornarListaDeUsuarios() throws Exception {
        // Arrange - Criar segundo usuário
        usuarioRepository.save(UsuarioEntity.builder()
                .nome("Maria Santos")
                .email("maria@email.com")
                .senha("senha123")
                .role(Role.ADMIN)
                .build());

        // Act & Assert
        mockMvc.perform(get("/usuario")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nome").value("João Silva"))
                .andExpect(jsonPath("$[0].email").value("joao@email.com"))
                .andExpect(jsonPath("$[0].role").value("USER"))
                .andExpect(jsonPath("$[1].nome").value("Maria Santos"))
                .andExpect(jsonPath("$[1].email").value("maria@email.com"))
                .andExpect(jsonPath("$[1].role").value("ADMIN"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /usuario/{id} - Deve retornar usuário por ID")
    void deveRetornarUsuarioPorId() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/usuario/{id}", usuarioId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(usuarioId.toString()))
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.email").value("joao@email.com"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /usuario/{id} - Deve retornar 404 quando usuário não existe")
    void deveRetornar404QuandoUsuarioNaoExiste() throws Exception {
        // Arrange
        UUID idInexistente = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(get("/usuario/{id}", idInexistente)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /usuario - Deve cadastrar novo usuário")
    void deveCadastrarNovoUsuario() throws Exception {
        // Arrange
        CriarUsuarioRequestDTO novoUsuarioDTO = new CriarUsuarioRequestDTO(
                "Pedro Costa",
                "pedro@email.com",
                "senha123",
                Role.USER
        );

        // Act & Assert
        mockMvc.perform(post("/usuario")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novoUsuarioDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.nome").value("Pedro Costa"))
                .andExpect(jsonPath("$.email").value("pedro@email.com"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /usuario - Deve retornar 400 quando dados inválidos (nome vazio)")
    void deveRetornar400QuandoNomeVazio() throws Exception {
        // Arrange
        CriarUsuarioRequestDTO dtoInvalido = new CriarUsuarioRequestDTO(
                "",  // Nome vazio
                "joao@email.com",
                "senha123",
                Role.USER
        );

        // Act & Assert
        mockMvc.perform(post("/usuario")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /usuario - Deve retornar 400 quando email inválido")
    void deveRetornar400QuandoEmailInvalido() throws Exception {
        // Arrange
        CriarUsuarioRequestDTO dtoInvalido = new CriarUsuarioRequestDTO(
                "João Silva",
                "emailinvalido",  // Email sem formato válido
                "senha123",
                Role.USER
        );

        // Act & Assert
        mockMvc.perform(post("/usuario")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /usuario - Deve retornar 400 quando senha muito curta")
    void deveRetornar400QuandoSenhaMuitoCurta() throws Exception {
        // Arrange
        CriarUsuarioRequestDTO dtoInvalido = new CriarUsuarioRequestDTO(
                "João Silva",
                "joao@email.com",
                "12345",  // Senha com menos de 6 caracteres
                Role.USER
        );

        // Act & Assert
        mockMvc.perform(post("/usuario")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("PUT /usuario/{id} - Deve atualizar usuário")
    void deveAtualizarUsuario() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/usuario/{id}", usuarioId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizarUsuarioDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nome").value("João Silva Atualizado"))
                .andExpect(jsonPath("$.email").value("joao.novo@email.com"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    @WithMockUser
    @DisplayName("PUT /usuario/{id} - Deve retornar 404 ao atualizar usuário inexistente")
    void deveRetornar404AoAtualizarUsuarioInexistente() throws Exception {
        // Arrange
        UUID idInexistente = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(put("/usuario/{id}", idInexistente)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizarUsuarioDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /usuario/{id} - Deve deletar usuário com role ADMIN")
    void deveDeletarUsuarioComRoleAdmin() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/usuario/{id}", usuarioId)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        // Verify que foi deletado
        var resultado = usuarioRepository.findById(usuarioId);
        assert resultado.isEmpty();
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("DELETE /usuario/{id} - Deve retornar 403 quando usuário não é ADMIN")
    void deveRetornar403QuandoUsuarioNaoEhAdmin() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/usuario/{id}", usuarioId)
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /usuario/{id} - Deve retornar 404 ao deletar usuário inexistente")
    void deveRetornar404AoDeletarUsuarioInexistente() throws Exception {
        // Arrange
        UUID idInexistente = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(delete("/usuario/{id}", idInexistente)
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar 401 quando usuário não está autenticado")
    void deveRetornar401QuandoNaoAutenticado() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/usuario")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
