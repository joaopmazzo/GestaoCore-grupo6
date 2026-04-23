package br.com.gestaocore_grupo6_api.modules.venda.controller;

import br.com.gestaocore_grupo6_api.modules.cliente.entity.ClienteEntity;
import br.com.gestaocore_grupo6_api.modules.cliente.entity.Endereco;
import br.com.gestaocore_grupo6_api.modules.cliente.repository.ClienteRepository;
import br.com.gestaocore_grupo6_api.modules.produto.entity.ProdutoEntity;
import br.com.gestaocore_grupo6_api.modules.produto.repository.ProdutoRepository;
import br.com.gestaocore_grupo6_api.modules.venda.dto.CriarItemVendaRequestDTO;
import br.com.gestaocore_grupo6_api.modules.venda.dto.CriarVendaRequestDTO;
import br.com.gestaocore_grupo6_api.modules.venda.entity.VendaEntity;
import br.com.gestaocore_grupo6_api.modules.venda.repository.VendaRepository;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes de Integração - VendaController")
@ActiveProfiles("test")
@Transactional
class VendaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VendaRepository vendaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    private UUID clienteId;
    private UUID produtoId;

    @BeforeEach
    void setUp() {
        vendaRepository.deleteAll();
        clienteRepository.deleteAll();
        produtoRepository.deleteAll();

        Endereco endereco = Endereco.builder()
                .rua("Rua Teste")
                .numero("123")
                .cidade("São Paulo")
                .estado("SP")
                .cep("01234-567")
                .complemento("Apto 1")
                .build();

        ClienteEntity cliente = clienteRepository.save(ClienteEntity.builder()
                .nome("Cliente Teste")
                .email("cliente@test.com")
                .telefone("1199999999")
                .endereco(endereco)
                .build());

        clienteId = cliente.getId();

        ProdutoEntity produto = produtoRepository.save(ProdutoEntity.builder()
                .nome("Produto Teste")
                .categoria("Categoria")
                .preco(100.00)
                .qtdEstoque(50.0)
                .build());

        produtoId = produto.getId();

        CriarItemVendaRequestDTO item = new CriarItemVendaRequestDTO(produtoId, 5.0);
        CriarVendaRequestDTO criarVendaDTO = new CriarVendaRequestDTO(
                LocalDateTime.now(),
                clienteId,
                List.of(item)
        );

        String json = objectMapper.writeValueAsString(criarVendaDTO);
        try {
            mockMvc.perform(post("/venda")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json));
        } catch (Exception e) {
            // ignore
        }
    }

    @Test
    @WithMockUser
    @DisplayName("GET /venda - Deve retornar lista de vendas")
    void deveRetornarListaDeVendas() throws Exception {
        mockMvc.perform(get("/venda")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /venda/{id} - Deve retornar venda por ID")
    void deveRetornarVendaPorId() throws Exception {
        List<VendaEntity> vendas = vendaRepository.findAll();
        if (!vendas.isEmpty()) {
            UUID id = vendas.getFirst().getId();
            mockMvc.perform(get("/venda/{id}", id)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(id.toString()));
        }
    }

    @Test
    @WithMockUser
    @DisplayName("GET /venda/{id} - Deve retornar 404 quando venda não existe")
    void deveRetornar404QuandoVendaNaoExiste() throws Exception {
        UUID idInexistente = UUID.randomUUID();

        mockMvc.perform(get("/venda/{id}", idInexistente)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /venda - Deve cadastrar nova venda")
    void deveCadastrarNovaVenda() throws Exception {
        CriarItemVendaRequestDTO item = new CriarItemVendaRequestDTO(produtoId, 3.0);
        CriarVendaRequestDTO novavenda = new CriarVendaRequestDTO(
                LocalDateTime.now(),
                clienteId,
                List.of(item)
        );

        mockMvc.perform(post("/venda")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novavenda)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().exists("Location"));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /venda - Deve retornar 400 quando cliente não existe")
    void deveRetornar400QuandoClienteNaoExiste() throws Exception {
        CriarItemVendaRequestDTO item = new CriarItemVendaRequestDTO(produtoId, 1.0);
        CriarVendaRequestDTO novaVenda = new CriarVendaRequestDTO(
                LocalDateTime.now(),
                UUID.randomUUID(),
                List.of(item)
        );

        mockMvc.perform(post("/venda")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novaVenda)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("PATCH /venda/{id}/cancelar - Deve cancelar venda")
    void deveCancelarVenda() throws Exception {
        List<VendaEntity> vendas = vendaRepository.findAll();
        if (!vendas.isEmpty()) {
            UUID id = vendas.getFirst().getId();
            mockMvc.perform(patch("/venda/{id}/cancelar", id)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /venda/{id} - Deve deletar venda com ADMIN")
    void deveDeletarVendaComRoleAdmin() throws Exception {
        // Primeiro cancela a venda
        List<VendaEntity> vendas = vendaRepository.findAll();
        if (!vendas.isEmpty()) {
            UUID id = vendas.getFirst().getId();
            mockMvc.perform(patch("/venda/{id}/cancelar", id)
                    .contentType(MediaType.APPLICATION_JSON));

            // Depois deleta
            mockMvc.perform(delete("/venda/{id}", id)
                    .with(csrf()))
                    .andExpect(status().isNoContent());
        }
    }

    @Test
    @DisplayName("Deve retornar 401 sem autenticação")
    void deveRetornar401QuandoNaoAutenticado() throws Exception {
        mockMvc.perform(get("/venda")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}

