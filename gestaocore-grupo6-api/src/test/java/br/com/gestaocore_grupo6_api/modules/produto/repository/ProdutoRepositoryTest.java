package br.com.gestaocore_grupo6_api.modules.produto.repository;

import br.com.gestaocore_grupo6_api.modules.produto.entity.ProdutoEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("Testes de Repositório - ProdutoRepository")
@ActiveProfiles("test")
class ProdutoRepositoryTest {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private EntityManager entityManager;

    private ProdutoEntity produtoEntity;

    @BeforeEach
    void setUp() {
        produtoEntity = ProdutoEntity.builder()
                .nome("Notebook")
                .categoria("Eletrônicos")
                .preco(2500.00)
                .qtdEstoque(10.0)
                .build();
    }

    @Test
    @DisplayName("Deve salvar produto no banco de dados")
    void deveSalvarProduto() {
        ProdutoEntity produtoSalvo = produtoRepository.save(produtoEntity);
        assertNotNull(produtoSalvo.getId());
        assertEquals("Notebook", produtoSalvo.getNome());
        assertEquals("Eletrônicos", produtoSalvo.getCategoria());
        assertEquals(2500.00, produtoSalvo.getPreco());
    }

    @Test
    @DisplayName("Deve encontrar produto por ID")
    void deveEncontrarProdutoPorId() {
        entityManager.persist(produtoEntity);
        entityManager.flush();
        Optional<ProdutoEntity> resultado = produtoRepository.findById(produtoEntity.getId());
        assertTrue(resultado.isPresent());
        assertEquals("Notebook", resultado.get().getNome());
    }

    @Test
    @DisplayName("Deve retornar todos os produtos")
    void deveRetornarTodosProdutos() {
        ProdutoEntity produto2 = ProdutoEntity.builder()
                .nome("Mouse")
                .categoria("Periféricos")
                .preco(50.00)
                .qtdEstoque(100.0)
                .build();

        entityManager.persist(produtoEntity);
        entityManager.persist(produto2);
        entityManager.flush();

        List<ProdutoEntity> produtos = produtoRepository.findAll();
        assertThat(produtos).hasSize(2);
        assertThat(produtos).extracting(ProdutoEntity::getNome)
                .contains("Notebook", "Mouse");
    }

    @Test
    @DisplayName("Deve deletar produto por ID")
    void deveDeletarProdutoPorId() {
        entityManager.persist(produtoEntity);
        entityManager.flush();
        UUID produtoId = produtoEntity.getId();

        produtoRepository.deleteById(produtoId);
        entityManager.flush();

        Optional<ProdutoEntity> resultado = produtoRepository.findById(produtoId);
        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("Deve atualizar dados do produto")
    void deveAtualizarDadosDoProduto() {
        entityManager.persist(produtoEntity);
        entityManager.flush();

        produtoEntity.setNome("Notebook Dell");
        produtoEntity.setPreco(3000.00);
        ProdutoEntity produtoAtualizado = produtoRepository.save(produtoEntity);
        entityManager.flush();

        ProdutoEntity produtoRecuperado = entityManager.find(ProdutoEntity.class, produtoAtualizado.getId());
        assertEquals("Notebook Dell", produtoRecuperado.getNome());
        assertEquals(3000.00, produtoRecuperado.getPreco());
    }

    @Test
    @DisplayName("Deve gerar ID automaticamente")
    void deveGerarIdAutomaticamente() {
        ProdutoEntity produtoSalvo = produtoRepository.save(produtoEntity);
        assertNotNull(produtoSalvo.getId());
        assertInstanceOf(UUID.class, produtoSalvo.getId());
    }

    @Test
    @DisplayName("Deve retornar lista vazia")
    void deveRetornarListaVazia() {
        List<ProdutoEntity> produtos = produtoRepository.findAll();
        assertThat(produtos).isEmpty();
    }

    @Test
    @DisplayName("Deve contar número de produtos")
    void deveContarNumeroDeProdutos() {
        entityManager.persist(produtoEntity);
        entityManager.persist(ProdutoEntity.builder()
                .nome("Teclado")
                .categoria("Periféricos")
                .preco(150.00)
                .qtdEstoque(50.0)
                .build());
        entityManager.flush();

        long count = produtoRepository.count();
        assertEquals(2, count);
    }

    @Test
    @DisplayName("Deve verificar existência por ID")
    void deveVerificarExistenciaPorId() {
        entityManager.persist(produtoEntity);
        entityManager.flush();

        boolean existe = produtoRepository.existsById(produtoEntity.getId());
        boolean naoExiste = produtoRepository.existsById(UUID.randomUUID());

        assertTrue(existe);
        assertFalse(naoExiste);
    }

    @Test
    @DisplayName("Deve atualizar quantidade em estoque")
    void deveAtualizarQtdEstoque() {
        produtoEntity.setQtdEstoque(10.0);
        ProdutoEntity produtoSalvo = produtoRepository.save(produtoEntity);
        entityManager.flush();

        produtoSalvo.setQtdEstoque(5.0);
        produtoRepository.save(produtoSalvo);
        entityManager.flush();

        ProdutoEntity produtoRecuperado = produtoRepository.findById(produtoSalvo.getId()).get();
        assertEquals(5.0, produtoRecuperado.getQtdEstoque());
    }
}

