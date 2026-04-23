package br.com.gestaocore_grupo6_api.modules.cliente.service;

import br.com.gestaocore_grupo6_api.modules.cliente.dto.AtualizarClienteRequestDTO;
import br.com.gestaocore_grupo6_api.modules.cliente.dto.ClienteDTO;
import br.com.gestaocore_grupo6_api.modules.cliente.dto.CriarClienteRequestDTO;
import br.com.gestaocore_grupo6_api.modules.cliente.entity.ClienteEntity;
import br.com.gestaocore_grupo6_api.modules.cliente.entity.Endereco;
import br.com.gestaocore_grupo6_api.modules.cliente.mapper.ClienteMapper;
import br.com.gestaocore_grupo6_api.modules.cliente.repository.ClienteRepository;
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
@DisplayName("Testes Unitários - ClienteService")
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ClienteMapper clienteMapper;

    @InjectMocks
    private ClienteService clienteService;

    private UUID clienteId;
    private ClienteEntity clienteEntity;
    private ClienteDTO clienteDTO;
    private CriarClienteRequestDTO criarClienteDTO;
    private AtualizarClienteRequestDTO atualizarClienteDTO;
    private Endereco endereco;

    @BeforeEach
    void setUp() {
        clienteId = UUID.randomUUID();

        endereco = Endereco.builder()
                .rua("Rua das Flores")
                .numero("123")
                .cidade("São Paulo")
                .estado("SP")
                .cep("01234-567")
                .complemento("Apto 10")
                .build();

        clienteEntity = ClienteEntity.builder()
                .id(clienteId)
                .nome("João da Silva")
                .email("joao@email.com")
                .telefone("11999999999")
                .endereco(endereco)
                .build();

        clienteDTO = new ClienteDTO(
                clienteId,
                "João da Silva",
                "joao@email.com",
                "11999999999",
                endereco
        );

        criarClienteDTO = new CriarClienteRequestDTO(
                "João da Silva",
                "joao@email.com",
                "11999999999",
                endereco
        );

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
    @DisplayName("Deve retornar lista de todos os clientes")
    void deveRetornarListaDeClientes() {
        ClienteEntity cliente2 = ClienteEntity.builder()
                .id(UUID.randomUUID())
                .nome("Maria Santos")
                .email("maria@email.com")
                .telefone("11888888888")
                .endereco(endereco)
                .build();

        List<ClienteEntity> clientes = Arrays.asList(clienteEntity, cliente2);
        ClienteDTO clienteDTO2 = new ClienteDTO(cliente2.getId(), "Maria Santos", "maria@email.com", "11888888888", endereco);

        when(clienteRepository.findAll()).thenReturn(clientes);
        when(clienteMapper.toDTO(clienteEntity)).thenReturn(clienteDTO);
        when(clienteMapper.toDTO(cliente2)).thenReturn(clienteDTO2);

        List<ClienteDTO> resultado = clienteService.retornarClientes();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("João da Silva", resultado.get(0).nome());
        assertEquals("Maria Santos", resultado.get(1).nome());
        verify(clienteRepository, times(1)).findAll();
        verify(clienteMapper, times(2)).toDTO(any(ClienteEntity.class));
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há clientes")
    void deveRetornarListaVaziaQuandoNaoHaClientes() {
        when(clienteRepository.findAll()).thenReturn(List.of());

        List<ClienteDTO> resultado = clienteService.retornarClientes();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(clienteRepository, times(1)).findAll();
        verify(clienteMapper, never()).toDTO(any(ClienteEntity.class));
    }

    @Test
    @DisplayName("Deve retornar cliente por ID com sucesso")
    void deveRetornarClientePorId() {
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(clienteEntity));
        when(clienteMapper.toDTO(clienteEntity)).thenReturn(clienteDTO);

        ClienteDTO resultado = clienteService.retornarClientePorId(clienteId);

        assertNotNull(resultado);
        assertEquals(clienteId, resultado.id());
        assertEquals("João da Silva", resultado.nome());
        assertEquals("joao@email.com", resultado.email());
        verify(clienteRepository, times(1)).findById(clienteId);
        verify(clienteMapper, times(1)).toDTO(clienteEntity);
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException quando cliente não existe")
    void deveLancarExcecaoQuandoClienteNaoExiste() {
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> clienteService.retornarClientePorId(clienteId)
        );

        assertEquals("Cliente não encontrado", exception.getMessage());
        verify(clienteRepository, times(1)).findById(clienteId);
        verify(clienteMapper, never()).toDTO(any(ClienteEntity.class));
    }

    @Test
    @DisplayName("Deve cadastrar novo cliente com sucesso")
    void deveCadastrarClienteComSucesso() {
        ClienteEntity clienteNovo = ClienteEntity.builder()
                .nome("João da Silva")
                .email("joao@email.com")
                .telefone("11999999999")
                .endereco(endereco)
                .build();

        when(clienteMapper.toEntity(criarClienteDTO)).thenReturn(clienteNovo);
        when(clienteRepository.save(any(ClienteEntity.class))).thenReturn(clienteEntity);
        when(clienteMapper.toDTO(clienteEntity)).thenReturn(clienteDTO);

        ClienteDTO resultado = clienteService.cadastrarCliente(criarClienteDTO);

        assertNotNull(resultado);
        assertEquals("João da Silva", resultado.nome());
        assertEquals("joao@email.com", resultado.email());
        verify(clienteMapper, times(1)).toEntity(criarClienteDTO);
        verify(clienteRepository, times(1)).save(any(ClienteEntity.class));
        verify(clienteMapper, times(1)).toDTO(clienteEntity);
    }

    @Test
    @DisplayName("Deve atualizar cliente com sucesso")
    void deveAtualizarClienteComSucesso() {
        ClienteEntity clienteAtualizado = ClienteEntity.builder()
                .nome("João Silva Atualizado")
                .email("joao.novo@email.com")
                .telefone("11988888888")
                .endereco(Endereco.builder()
                        .rua("Avenida Paulista")
                        .numero("1000")
                        .cidade("São Paulo")
                        .estado("SP")
                        .cep("01310-100")
                        .complemento("Sala 100")
                        .build())
                .build();

        ClienteDTO clienteDTOAtualizado = new ClienteDTO(
                clienteId,
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

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(clienteEntity));
        when(clienteMapper.toEntity(atualizarClienteDTO)).thenReturn(clienteAtualizado);
        when(clienteRepository.save(clienteEntity)).thenReturn(clienteEntity);
        when(clienteMapper.toDTO(clienteEntity)).thenReturn(clienteDTOAtualizado);

        ClienteDTO resultado = clienteService.atualizarCliente(clienteId, atualizarClienteDTO);

        assertNotNull(resultado);
        assertEquals("João Silva Atualizado", resultado.nome());
        assertEquals("joao.novo@email.com", resultado.email());
        verify(clienteRepository, times(1)).findById(clienteId);
        verify(clienteMapper, times(1)).toEntity(atualizarClienteDTO);
        verify(clienteRepository, times(1)).save(clienteEntity);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar cliente inexistente")
    void deveLancarExcecaoAoAtualizarClienteInexistente() {
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> clienteService.atualizarCliente(clienteId, atualizarClienteDTO)
        );

        assertEquals("Cliente não encontrado", exception.getMessage());
        verify(clienteRepository, times(1)).findById(clienteId);
        verify(clienteRepository, never()).save(any(ClienteEntity.class));
    }

    @Test
    @DisplayName("Deve deletar cliente com sucesso")
    void deveDeletarClienteComSucesso() {
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(clienteEntity));
        doNothing().when(clienteRepository).deleteById(clienteId);

        clienteService.deletarCliente(clienteId);

        verify(clienteRepository, times(1)).findById(clienteId);
        verify(clienteRepository, times(1)).deleteById(clienteId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar cliente inexistente")
    void deveLancarExcecaoAoDeletarClienteInexistente() {
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> clienteService.deletarCliente(clienteId)
        );

        assertEquals("Cliente não encontrado", exception.getMessage());
        verify(clienteRepository, times(1)).findById(clienteId);
        verify(clienteRepository, never()).deleteById(any(UUID.class));
    }
}

