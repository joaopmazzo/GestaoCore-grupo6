package br.com.gestaocore_grupo6_api.modules.produto.service;

import br.com.gestaocore_grupo6_api.modules.produto.dto.AtualizarProdutoRequestDTO;
import br.com.gestaocore_grupo6_api.modules.produto.dto.CriarProdutoRequestDTO;
import br.com.gestaocore_grupo6_api.modules.produto.dto.ProdutoDTO;
import br.com.gestaocore_grupo6_api.modules.produto.entity.ProdutoEntity;
import br.com.gestaocore_grupo6_api.modules.produto.mapper.ProdutoMapper;
import br.com.gestaocore_grupo6_api.modules.produto.repository.ProdutoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - ProdutoService")
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private ProdutoMapper produtoMapper;

    @InjectMocks
    private ProdutoService produtoService;

    private UUID produtoId;
    private ProdutoEntity produtoEntity;
    private ProdutoDTO produtoDTO;
    private CriarProdutoRequestDTO criarProdutoDTO;
    private AtualizarProdutoRequestDTO atualizarProdutoDTO;

    @BeforeEach
    void setUp() {
        produtoId = UUID.randomUUID();

        produtoEntity = ProdutoEntity.builder()
                .id(produtoId)
                .nome("Notebook")
                .categoria("Eletrônicos")
                .preco(2500.00)
                .qtdEstoque(10.0)
                .build();

        produtoDTO = new ProdutoDTO(
                produtoId,
                "Notebook",
                "Eletrônicos",
                2500.00,
                10.0
        );

        criarProdutoDTO = new CriarProdutoRequestDTO(
                "Notebook",
                "Eletrônicos",
                2500.00,
                10.0
        );

        atualizarProdutoDTO = new AtualizarProdutoRequestDTO(
                "Notebook Dell",
                "Informática",
                3000.00,
                15.0
        );
    }

    @Test
    @DisplayName("Deve retornar lista de todos os produtos")
    void deveRetornarListaDeProdutos() {
        ProdutoEntity produto2 = ProdutoEntity.builder()
                .id(UUID.randomUUID())
                .nome("Mouse")
                .categoria("Periféricos")
                .preco(50.00)
                .qtdEstoque(100.0)
                .build();

        List<ProdutoEntity> produtos = Arrays.asList(produtoEntity, produto2);
        ProdutoDTO produtoDTO2 = new ProdutoDTO(produto2.getId(), "Mouse", "Periféricos", 50.00, 100.0);

        when(produtoRepository.findAll()).thenReturn(produtos);
        when(produtoMapper.toDTO(produtoEntity)).thenReturn(produtoDTO);
        when(produtoMapper.toDTO(produto2)).thenReturn(produtoDTO2);

        List<ProdutoDTO> resultado = produtoService.retornarProdutos();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Notebook", resultado.get(0).nome());
        verify(produtoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar lista vazia")
    void deveRetornarListaVazia() {
        when(produtoRepository.findAll()).thenReturn(List.of());

        List<ProdutoDTO> resultado = produtoService.retornarProdutos();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(produtoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar produto por ID")
    void deveRetornarProdutoPorId() {
        when(produtoRepository.findById(produtoId)).thenReturn(Optional.of(produtoEntity));
        when(produtoMapper.toDTO(produtoEntity)).thenReturn(produtoDTO);

        ProdutoDTO resultado = produtoService.retornarProdutoPorId(produtoId);

        assertNotNull(resultado);
        assertEquals(produtoId, resultado.id());
        assertEquals("Notebook", resultado.nome());
        verify(produtoRepository, times(1)).findById(produtoId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando produto não existe")
    void deveLancarExcecaoQuandoProdutoNaoExiste() {
        when(produtoRepository.findById(produtoId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> produtoService.retornarProdutoPorId(produtoId)
        );

        assertEquals("Produto não encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve cadastrar novo produto")
    void deveCadastrarProduto() {
        ProdutoEntity produtoNovo = ProdutoEntity.builder()
                .nome("Notebook")
                .categoria("Eletrônicos")
                .preco(2500.00)
                .qtdEstoque(10.0)
                .build();

        when(produtoMapper.toEntity(criarProdutoDTO)).thenReturn(produtoNovo);
        when(produtoRepository.save(any(ProdutoEntity.class))).thenReturn(produtoEntity);
        when(produtoMapper.toDTO(produtoEntity)).thenReturn(produtoDTO);

        ProdutoDTO resultado = produtoService.cadastrarProduto(criarProdutoDTO);

        assertNotNull(resultado);
        assertEquals("Notebook", resultado.nome());
        verify(produtoRepository, times(1)).save(any(ProdutoEntity.class));
    }

    @Test
    @DisplayName("Deve atualizar produto")
    void deveAtualizarProduto() {
        ProdutoEntity produtoAtualizado = ProdutoEntity.builder()
                .nome("Notebook Dell")
                .categoria("Informática")
                .preco(3000.00)
                .qtdEstoque(15.0)
                .build();

        ProdutoDTO produtoDTOAtualizado = new ProdutoDTO(
                produtoId,
                "Notebook Dell",
                "Informática",
                3000.00,
                15.0
        );

        when(produtoRepository.findById(produtoId)).thenReturn(Optional.of(produtoEntity));
        when(produtoMapper.toEntity(atualizarProdutoDTO)).thenReturn(produtoAtualizado);
        when(produtoRepository.save(produtoEntity)).thenReturn(produtoEntity);
        when(produtoMapper.toDTO(produtoEntity)).thenReturn(produtoDTOAtualizado);

        ProdutoDTO resultado = produtoService.atualizarProduto(produtoId, atualizarProdutoDTO);

        assertNotNull(resultado);
        assertEquals("Notebook Dell", resultado.nome());
        verify(produtoRepository, times(1)).findById(produtoId);
        verify(produtoRepository, times(1)).save(produtoEntity);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar produto inexistente")
    void deveLancarExcecaoAoAtualizarInexistente() {
        when(produtoRepository.findById(produtoId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> produtoService.atualizarProduto(produtoId, atualizarProdutoDTO)
        );

        assertEquals("Produto não encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve deletar produto")
    void deveDeletarProduto() {
        when(produtoRepository.findById(produtoId)).thenReturn(Optional.of(produtoEntity));
        doNothing().when(produtoRepository).deleteById(produtoId);

        produtoService.deletarProduto(produtoId);

        verify(produtoRepository, times(1)).findById(produtoId);
        verify(produtoRepository, times(1)).deleteById(produtoId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar inexistente")
    void deveLancarExcecaoAoDeletarInexistente() {
        when(produtoRepository.findById(produtoId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> produtoService.deletarProduto(produtoId)
        );

        assertEquals("Produto não encontrado", exception.getMessage());
    }
}

