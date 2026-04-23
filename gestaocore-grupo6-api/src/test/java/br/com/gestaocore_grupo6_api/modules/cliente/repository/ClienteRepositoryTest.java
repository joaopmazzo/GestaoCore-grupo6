package br.com.gestaocore_grupo6_api.modules.cliente.repository;

import br.com.gestaocore_grupo6_api.modules.cliente.entity.ClienteEntity;
import br.com.gestaocore_grupo6_api.modules.cliente.entity.Endereco;
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
@DisplayName("Testes de Repositório - ClienteRepository")
@ActiveProfiles("test")
class ClienteRepositoryTest {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private EntityManager entityManager;

    private ClienteEntity clienteEntity;
    private Endereco endereco;

    @BeforeEach
    void setUp() {
        endereco = Endereco.builder()
                .rua("Rua das Flores")
                .numero("123")
                .cidade("São Paulo")
                .estado("SP")
                .cep("01234-567")
                .complemento("Apto 10")
                .build();

        clienteEntity = ClienteEntity.builder()
                .nome("João da Silva")
                .email("joao@email.com")
                .telefone("11999999999")
                .endereco(endereco)
                .build();
    }

    @Test
    @DisplayName("Deve salvar cliente no banco de dados")
    void deveSalvarCliente() {
        ClienteEntity clienteSalvo = clienteRepository.save(clienteEntity);
        assertNotNull(clienteSalvo.getId());
        assertEquals("João da Silva", clienteSalvo.getNome());
        assertEquals("joao@email.com", clienteSalvo.getEmail());
    }

    @Test
    @DisplayName("Deve encontrar cliente por ID")
    void deveEncontrarClientePorId() {
        entityManager.persist(clienteEntity);
        entityManager.flush();
        Optional<ClienteEntity> resultado = clienteRepository.findById(clienteEntity.getId());
        assertTrue(resultado.isPresent());
        assertEquals(clienteEntity.getId(), resultado.get().getId());
        assertEquals("João da Silva", resultado.get().getNome());
    }

    @Test
    @DisplayName("Deve retornar todos os clientes")
    void deveRetornarTodosOsClientes() {
        ClienteEntity cliente2 = ClienteEntity.builder()
                .nome("Maria Santos")
                .email("maria@email.com")
                .telefone("11888888888")
                .endereco(endereco)
                .build();

        entityManager.persist(clienteEntity);
        entityManager.persist(cliente2);
        entityManager.flush();

        List<ClienteEntity> clientes = clienteRepository.findAll();
        assertThat(clientes).hasSize(2);
        assertThat(clientes).extracting(ClienteEntity::getEmail)
                .contains("joao@email.com", "maria@email.com");
    }

    @Test
    @DisplayName("Deve deletar cliente por ID")
    void deveDeletarClientePorId() {
        entityManager.persist(clienteEntity);
        entityManager.flush();
        UUID clienteId = clienteEntity.getId();

        clienteRepository.deleteById(clienteId);
        entityManager.flush();

        Optional<ClienteEntity> resultado = clienteRepository.findById(clienteId);
        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("Deve atualizar dados do cliente")
    void deveAtualizarDadosDoCliente() {
        entityManager.persist(clienteEntity);
        entityManager.flush();

        clienteEntity.setNome("João Silva Atualizado");
        clienteEntity.setEmail("joao.novo@email.com");
        ClienteEntity clienteAtualizado = clienteRepository.save(clienteEntity);
        entityManager.flush();

        ClienteEntity clienteRecuperado = entityManager.find(ClienteEntity.class, clienteAtualizado.getId());
        assertEquals("João Silva Atualizado", clienteRecuperado.getNome());
        assertEquals("joao.novo@email.com", clienteRecuperado.getEmail());
    }

    @Test
    @DisplayName("Deve gerar ID automaticamente ao salvar cliente")
    void deveGerarIdAutomaticamente() {
        ClienteEntity clienteSalvo = clienteRepository.save(clienteEntity);
        assertNotNull(clienteSalvo.getId());
        assertInstanceOf(UUID.class, clienteSalvo.getId());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há clientes")
    void deveRetornarListaVaziaQuandoNaoHaClientes() {
        List<ClienteEntity> clientes = clienteRepository.findAll();
        assertThat(clientes).isEmpty();
    }

    @Test
    @DisplayName("Deve contar número de clientes no banco")
    void deveContarNumeroDeClientes() {
        entityManager.persist(clienteEntity);
        entityManager.persist(ClienteEntity.builder()
                .nome("Outro Cliente")
                .email("outro@email.com")
                .telefone("11777777777")
                .endereco(endereco)
                .build());
        entityManager.flush();

        long count = clienteRepository.count();
        assertEquals(2, count);
    }

    @Test
    @DisplayName("Deve verificar se cliente existe por ID")
    void deveVerificarSeClienteExistePorId() {
        entityManager.persist(clienteEntity);
        entityManager.flush();

        boolean existe = clienteRepository.existsById(clienteEntity.getId());
        boolean naoExiste = clienteRepository.existsById(UUID.randomUUID());

        assertTrue(existe);
        assertFalse(naoExiste);
    }

    @Test
    @DisplayName("Deve persistir endereço embarcado do cliente")
    void devePersistirEnderecoEmbarcado() {
        Endereco novoEndereco = Endereco.builder()
                .rua("Avenida Paulista")
                .numero("1000")
                .cidade("São Paulo")
                .estado("SP")
                .cep("01310-100")
                .complemento("Sala 100")
                .build();

        clienteEntity.setEndereco(novoEndereco);
        ClienteEntity clienteSalvo = clienteRepository.save(clienteEntity);
        entityManager.flush();

        ClienteEntity clienteRecuperado = entityManager.find(ClienteEntity.class, clienteSalvo.getId());
        assertEquals("Avenida Paulista", clienteRecuperado.getEndereco().getRua());
        assertEquals("1000", clienteRecuperado.getEndereco().getNumero());
        assertEquals("01310-100", clienteRecuperado.getEndereco().getCep());
    }
}

