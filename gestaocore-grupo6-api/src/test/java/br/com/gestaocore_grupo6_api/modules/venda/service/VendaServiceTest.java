package br.com.gestaocore_grupo6_api.modules.venda.service;

import br.com.gestaocore_grupo6_api.modules.cliente.entity.ClienteEntity;
import br.com.gestaocore_grupo6_api.modules.cliente.entity.Endereco;
import br.com.gestaocore_grupo6_api.modules.cliente.repository.ClienteRepository;
import br.com.gestaocore_grupo6_api.modules.produto.entity.ProdutoEntity;
import br.com.gestaocore_grupo6_api.modules.produto.repository.ProdutoRepository;
import br.com.gestaocore_grupo6_api.modules.venda.dto.CriarItemVendaRequestDTO;
import br.com.gestaocore_grupo6_api.modules.venda.dto.CriarVendaRequestDTO;
import br.com.gestaocore_grupo6_api.modules.venda.dto.VendaDTO;
import br.com.gestaocore_grupo6_api.modules.venda.entity.ItemVendaEntity;
import br.com.gestaocore_grupo6_api.modules.venda.entity.VendaEntity;
import br.com.gestaocore_grupo6_api.modules.venda.enums.StatusVenda;
import br.com.gestaocore_grupo6_api.modules.venda.mapper.VendaMapper;
import br.com.gestaocore_grupo6_api.modules.venda.repository.VendaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - VendaService")
class VendaServiceTest {

    @Mock
    private VendaRepository vendaRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private VendaMapper vendaMapper;

    @InjectMocks
    private VendaService vendaService;

    private UUID vendaId;
    private UUID clienteId;
    private UUID produtoId;
    private VendaEntity vendaEntity;
    private VendaDTO vendaDTO;
    private ClienteEntity cliente;
    private ProdutoEntity produto;
    private CriarVendaRequestDTO criarVendaDTO;

    @BeforeEach
    void setUp() {
        vendaId = UUID.randomUUID();
        clienteId = UUID.randomUUID();
        produtoId = UUID.randomUUID();

        Endereco endereco = Endereco.builder()
                .rua("Rua Teste")
                .numero("123")
                .cidade("São Paulo")
                .estado("SP")
                .cep("01234-567")
                .complemento("Apto 1")
                .build();

        cliente = ClienteEntity.builder()
                .id(clienteId)
                .nome("Cliente Teste")
                .email("cliente@test.com")
                .telefone("1199999999")
                .endereco(endereco)
                .build();

        produto = ProdutoEntity.builder()
                .id(produtoId)
                .nome("Produto Teste")
                .categoria("Categoria")
                .preco(100.00)
                .qtdEstoque(50.0)
                .build();

        vendaEntity = VendaEntity.builder()
                .id(vendaId)
                .dataVenda(LocalDateTime.now())
                .cliente(cliente)
                .status(StatusVenda.ATIVA)
                .itens(new ArrayList<>())
                .build();

        vendaDTO = new VendaDTO(
                vendaId,
                LocalDateTime.now(),
                clienteId,
                new ArrayList<>()
        );

        CriarItemVendaRequestDTO item = new CriarItemVendaRequestDTO(produtoId, 5.0);
        criarVendaDTO = new CriarVendaRequestDTO(
                LocalDateTime.now(),
                clienteId,
                List.of(item)
        );
    }

    @Test
    @DisplayName("Deve retornar lista de vendas")
    void deveRetornarListaDeVendas() {
        List<VendaEntity> vendas = List.of(vendaEntity);
        when(vendaRepository.findAll()).thenReturn(vendas);
        when(vendaMapper.toDTO(vendaEntity)).thenReturn(vendaDTO);

        List<VendaDTO> resultado = vendaService.retornarVendas();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(vendaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar lista vazia")
    void deveRetornarListaVazia() {
        when(vendaRepository.findAll()).thenReturn(List.of());

        List<VendaDTO> resultado = vendaService.retornarVendas();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Deve retornar venda por ID")
    void deveRetornarVendaPorId() {
        when(vendaRepository.findById(vendaId)).thenReturn(Optional.of(vendaEntity));
        when(vendaMapper.toDTO(vendaEntity)).thenReturn(vendaDTO);

        VendaDTO resultado = vendaService.retornarVendaPorId(vendaId);

        assertNotNull(resultado);
        assertEquals(vendaId, resultado.id());
        verify(vendaRepository, times(1)).findById(vendaId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando venda não existe")
    void deveLancarExcecaoQuandoVendaNaoExiste() {
        when(vendaRepository.findById(vendaId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> vendaService.retornarVendaPorId(vendaId)
        );

        assertEquals("Venda não encontrada", exception.getMessage());
    }

    @Test
    @DisplayName("Deve cadastrar venda com sucesso")
    void deveCadastrarVendaComSucesso() {
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(produtoRepository.findById(produtoId)).thenReturn(Optional.of(produto));
        when(produtoRepository.save(any(ProdutoEntity.class))).thenReturn(produto);
        when(vendaRepository.save(any(VendaEntity.class))).thenReturn(vendaEntity);
        when(vendaMapper.toDTO(vendaEntity)).thenReturn(vendaDTO);

        VendaDTO resultado = vendaService.cadastrarVenda(criarVendaDTO);

        assertNotNull(resultado);
        verify(clienteRepository, times(1)).findById(clienteId);
        verify(produtoRepository, times(1)).findById(produtoId);
        verify(vendaRepository, times(1)).save(any(VendaEntity.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando cliente não existe")
    void deveLancarExcecaoClienteNaoExiste() {
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> vendaService.cadastrarVenda(criarVendaDTO)
        );

        assertEquals("Cliente não encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando estoque insuficiente")
    void deveLancarExcecaoEstoqueInsuficiente() {
        produto.setQtdEstoque(2.0);
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(produtoRepository.findById(produtoId)).thenReturn(Optional.of(produto));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> vendaService.cadastrarVenda(criarVendaDTO)
        );

        assertTrue(exception.getMessage().contains("Estoque insuficiente"));
    }

    @Test
    @DisplayName("Deve cancelar venda ativa")
    void deveCancelarVendaAtiva() {
        ItemVendaEntity item = ItemVendaEntity.builder()
                .produto(produto)
                .quantidade(5.0)
                .valorNaVenda(100.00)
                .valorTotal(500.00)
                .venda(vendaEntity)
                .build();

        vendaEntity.setItens(List.of(item));

        when(vendaRepository.findById(vendaId)).thenReturn(Optional.of(vendaEntity));
        when(produtoRepository.save(any(ProdutoEntity.class))).thenReturn(produto);
        when(vendaRepository.save(vendaEntity)).thenReturn(vendaEntity);
        when(vendaMapper.toDTO(vendaEntity)).thenReturn(vendaDTO);

        VendaDTO resultado = vendaService.cancelarVenda(vendaId);

        assertNotNull(resultado);
        verify(vendaRepository, times(1)).findById(vendaId);
        verify(vendaRepository, times(1)).save(vendaEntity);
    }

    @Test
    @DisplayName("Deve lançar exceção ao cancelar venda já cancelada")
    void deveLancarExcecaoVendaJaCancelada() {
        vendaEntity.setStatus(StatusVenda.CANCELADA);

        when(vendaRepository.findById(vendaId)).thenReturn(Optional.of(vendaEntity));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> vendaService.cancelarVenda(vendaId)
        );

        assertTrue(exception.getMessage().contains("já está cancelada"));
    }

    @Test
    @DisplayName("Deve deletar venda cancelada")
    void deveDeletarVendaCancelada() {
        vendaEntity.setStatus(StatusVenda.CANCELADA);

        when(vendaRepository.findById(vendaId)).thenReturn(Optional.of(vendaEntity));
        doNothing().when(vendaRepository).deleteById(vendaId);

        vendaService.deletarVenda(vendaId);

        verify(vendaRepository, times(1)).findById(vendaId);
        verify(vendaRepository, times(1)).deleteById(vendaId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar venda ativa")
    void deveLancarExcecaoAoDeletarVendaAtiva() {
        when(vendaRepository.findById(vendaId)).thenReturn(Optional.of(vendaEntity));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> vendaService.deletarVenda(vendaId)
        );

        assertTrue(exception.getMessage().contains("Não é possível deletar uma venda ativa"));
    }
}

