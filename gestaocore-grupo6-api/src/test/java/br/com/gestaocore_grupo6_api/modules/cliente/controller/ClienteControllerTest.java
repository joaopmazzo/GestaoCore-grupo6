package br.com.gestaocore_grupo6_api.modules.cliente.controller;

import br.com.gestaocore_grupo6_api.modules.cliente.dto.AtualizarClienteRequestDTO;
import br.com.gestaocore_grupo6_api.modules.cliente.dto.CriarClienteRequestDTO;
import br.com.gestaocore_grupo6_api.modules.cliente.entity.ClienteEntity;
import br.com.gestaocore_grupo6_api.modules.cliente.entity.Endereco;
import br.com.gestaocore_grupo6_api.modules.cliente.repository.ClienteRepository;
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
@DisplayName("Testes de Integração - ClienteController")
@ActiveProfiles("test")
@Transactional
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClienteRepository clienteRepository;

    private UUID clienteId;
    private ClienteEntity cliente;
    private AtualizarClienteRequestDTO atualizarClienteDTO;
    private Endereco endereco;

    @BeforeEach
    void setUp() {
        clienteRepository.deleteAll();

        endereco = Endereco.builder()
                .rua("Rua das Flores")
                .numero("123")
                .cidade("São Paulo")
                .estado("SP")
                .cep("01234-567")
                .complemento("Apto 10")
                .build();

        cliente = clienteRepository.save(ClienteEntity.builder()
                .nome("João da Silva")
                .email("joao@email.com")
                .telefone("11999999999")
                .endereco(endereco)
                .build());

        clienteId = cliente.getId();

        atualizarClienteDTO = new AtualizarClienteRequestDTO(
                "João Silva Atualizado",
                "joao.novo@email.com",
                "11988888888",
                Endereco.builder()
                        .rua("Avenida Paulista")
                        .numero("1000")
                        .cidade("São Paulo")
                        .estado("SP")
                        .cep("01310-100")
                        .complemento("Sala 100")
                        .build()
        );
    }

    @Test
    @WithMockUser
    @DisplayName("GET /cliente - Deve retornar lista de clientes")
    void deveRetornarListaDeClientes() throws Exception {
        clienteRepository.save(ClienteEntity.builder()
                .nome("Maria Santos")
                .email("maria@email.com")
                .telefone("11888888888")
                .endereco(endereco)
                .build());

        mockMvc.perform(get("/cliente")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nome").value("João da Silva"))
                .andExpect(jsonPath("$[0].email").value("joao@email.com"))
                .andExpect(jsonPath("$[1].nome").value("Maria Santos"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /cliente/{id} - Deve retornar cliente por ID")
    void deveRetornarClientePorId() throws Exception {
        mockMvc.perform(get("/cliente/{id}", clienteId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(clienteId.toString()))
                .andExpect(jsonPath("$.nome").value("João da Silva"))
                .andExpect(jsonPath("$.email").value("joao@email.com"))
                .andExpect(jsonPath("$.telefone").value("11999999999"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /cliente/{id} - Deve retornar 404 quando cliente não existe")
    void deveRetornar404QuandoClienteNaoExiste() throws Exception {
        UUID idInexistente = UUID.randomUUID();

        mockMvc.perform(get("/cliente/{id}", idInexistente)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /cliente - Deve cadastrar novo cliente")
    void deveCadastrarNovoCliente() throws Exception {
        CriarClienteRequestDTO novoClienteDTO = new CriarClienteRequestDTO(
                "Pedro Costa",
                "pedro@email.com",
                "11977777777",
                Endereco.builder()
                        .rua("Rua Principal")
                        .numero("456")
                        .cidade("Rio de Janeiro")
                        .estado("RJ")
                        .cep("20000-000")
                        .complemento("Loja")
                        .build()
        );

        mockMvc.perform(post("/cliente")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novoClienteDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.nome").value("Pedro Costa"))
                .andExpect(jsonPath("$.email").value("pedro@email.com"));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /cliente - Deve retornar 400 quando nome vazio")
    void deveRetornar400QuandoNomeVazio() throws Exception {
        CriarClienteRequestDTO dtoInvalido = new CriarClienteRequestDTO(
                "",
                "joao@email.com",
                "11999999999",
                endereco
        );

        mockMvc.perform(post("/cliente")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /cliente - Deve retornar 400 quando email inválido")
    void deveRetornar400QuandoEmailInvalido() throws Exception {
        CriarClienteRequestDTO dtoInvalido = new CriarClienteRequestDTO(
                "João Silva",
                "emailinvalido",
                "11999999999",
                endereco
        );

        mockMvc.perform(post("/cliente")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("PUT /cliente/{id} - Deve atualizar cliente")
    void deveAtualizarCliente() throws Exception {
        mockMvc.perform(put("/cliente/{id}", clienteId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizarClienteDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nome").value("João Silva Atualizado"))
                .andExpect(jsonPath("$.email").value("joao.novo@email.com"));
    }

    @Test
    @WithMockUser
    @DisplayName("PUT /cliente/{id} - Deve retornar 404 ao atualizar cliente inexistente")
    void deveRetornar404AoAtualizarClienteInexistente() throws Exception {
        UUID idInexistente = UUID.randomUUID();

        mockMvc.perform(put("/cliente/{id}", idInexistente)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizarClienteDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /cliente/{id} - Deve deletar cliente com role ADMIN")
    void deveDeletarClienteComRoleAdmin() throws Exception {
        mockMvc.perform(delete("/cliente/{id}", clienteId)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        var resultado = clienteRepository.findById(clienteId);
        assert resultado.isEmpty();
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("DELETE /cliente/{id} - Deve retornar 403 quando usuário não é ADMIN")
    void deveRetornar403QuandoUsuarioNaoEhAdmin() throws Exception {
        mockMvc.perform(delete("/cliente/{id}", clienteId)
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /cliente/{id} - Deve retornar 404 ao deletar cliente inexistente")
    void deveRetornar404AoDeletarClienteInexistente() throws Exception {
        UUID idInexistente = UUID.randomUUID();

        mockMvc.perform(delete("/cliente/{id}", idInexistente)
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar 401 quando usuário não está autenticado")
    void deveRetornar401QuandoNaoAutenticado() throws Exception {
        mockMvc.perform(get("/cliente")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}

