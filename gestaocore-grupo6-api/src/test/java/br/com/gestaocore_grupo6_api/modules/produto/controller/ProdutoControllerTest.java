package br.com.gestaocore_grupo6_api.modules.produto.controller;

import br.com.gestaocore_grupo6_api.modules.produto.dto.AtualizarProdutoRequestDTO;
import br.com.gestaocore_grupo6_api.modules.produto.dto.CriarProdutoRequestDTO;
import br.com.gestaocore_grupo6_api.modules.produto.entity.ProdutoEntity;
import br.com.gestaocore_grupo6_api.modules.produto.repository.ProdutoRepository;
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
@DisplayName("Testes de Integração - ProdutoController")
@ActiveProfiles("test")
@Transactional
class ProdutoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProdutoRepository produtoRepository;

    private UUID produtoId;
    private ProdutoEntity produto;
    private AtualizarProdutoRequestDTO atualizarProdutoDTO;

    @BeforeEach
    void setUp() {
        produtoRepository.deleteAll();

        produto = produtoRepository.save(ProdutoEntity.builder()
                .nome("Notebook")
                .categoria("Eletrônicos")
                .preco(2500.00)
                .qtdEstoque(10.0)
                .build());

        produtoId = produto.getId();

        atualizarProdutoDTO = new AtualizarProdutoRequestDTO(
                "Notebook Dell",
                "Informática",
                3000.00,
                15.0
        );
    }

    @Test
    @WithMockUser
    @DisplayName("GET /produto - Deve retornar lista de produtos")
    void deveRetornarListaDeProdutos() throws Exception {
        produtoRepository.save(ProdutoEntity.builder()
                .nome("Mouse")
                .categoria("Periféricos")
                .preco(50.00)
                .qtdEstoque(100.0)
                .build());

        mockMvc.perform(get("/produto")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nome").value("Notebook"))
                .andExpect(jsonPath("$[1].nome").value("Mouse"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /produto/{id} - Deve retornar produto por ID")
    void deveRetornarProdutoPorId() throws Exception {
        mockMvc.perform(get("/produto/{id}", produtoId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(produtoId.toString()))
                .andExpect(jsonPath("$.nome").value("Notebook"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /produto/{id} - Deve retornar 404 quando não existe")
    void deveRetornar404QuandoProdutoNaoExiste() throws Exception {
        UUID idInexistente = UUID.randomUUID();
        mockMvc.perform(get("/produto/{id}", idInexistente)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /produto - Deve cadastrar novo produto")
    void deveCadastrarNovoProduto() throws Exception {
        CriarProdutoRequestDTO novoProdutoDTO = new CriarProdutoRequestDTO(
                "Teclado",
                "Periféricos",
                150.00,
                50.0
        );

        mockMvc.perform(post("/produto")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novoProdutoDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.nome").value("Teclado"));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /produto - Deve retornar 400 quando nome vazio")
    void deveRetornar400QuandoNomeVazio() throws Exception {
        CriarProdutoRequestDTO dtoInvalido = new CriarProdutoRequestDTO(
                "",
                "Eletrônicos",
                2500.00,
                10.0
        );

        mockMvc.perform(post("/produto")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("PUT /produto/{id} - Deve atualizar produto")
    void deveAtualizarProduto() throws Exception {
        mockMvc.perform(put("/produto/{id}", produtoId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizarProdutoDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nome").value("Notebook Dell"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /produto/{id} - Deve deletar com ADMIN")
    void deveDeletarProdutoComRoleAdmin() throws Exception {
        mockMvc.perform(delete("/produto/{id}", produtoId)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        var resultado = produtoRepository.findById(produtoId);
        assert resultado.isEmpty();
    }

    @Test
    @DisplayName("Deve retornar 401 sem autenticação")
    void deveRetornar401QuandoNaoAutenticado() throws Exception {
        mockMvc.perform(get("/produto")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}

