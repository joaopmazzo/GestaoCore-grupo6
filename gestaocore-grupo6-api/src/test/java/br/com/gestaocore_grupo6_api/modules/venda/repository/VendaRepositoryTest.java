package br.com.gestaocore_grupo6_api.modules.venda.repository;

import br.com.gestaocore_grupo6_api.modules.cliente.entity.ClienteEntity;
import br.com.gestaocore_grupo6_api.modules.cliente.entity.Endereco;
import br.com.gestaocore_grupo6_api.modules.cliente.repository.ClienteRepository;
import br.com.gestaocore_grupo6_api.modules.venda.entity.VendaEntity;
import br.com.gestaocore_grupo6_api.modules.venda.enums.StatusVenda;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("Testes de Repositório - VendaRepository")
@ActiveProfiles("test")
class VendaRepositoryTest {

    @Autowired
    private VendaRepository vendaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private EntityManager entityManager;

    private VendaEntity vendaEntity;
    private ClienteEntity cliente;

    @BeforeEach
    void setUp() {
        Endereco endereco = Endereco.builder()
                .rua("Rua Teste")
                .numero("123")
                .cidade("São Paulo")
                .estado("SP")
                .cep("01234-567")
                .complemento("Apto 1")
                .build();

        cliente = clienteRepository.save(ClienteEntity.builder()
                .nome("Cliente Teste")
                .email("cliente@test.com")
                .telefone("1199999999")
                .endereco(endereco)
                .build());

        vendaEntity = VendaEntity.builder()
                .dataVenda(LocalDateTime.now())
                .cliente(cliente)
                .status(StatusVenda.ATIVA)
                .itens(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("Deve salvar venda no banco")
    void deveSalvarVenda() {
        VendaEntity vendaSalva = vendaRepository.save(vendaEntity);
        assertNotNull(vendaSalva.getId());
        assertEquals(StatusVenda.ATIVA, vendaSalva.getStatus());
        assertEquals(cliente.getId(), vendaSalva.getCliente().getId());
    }

    @Test
    @DisplayName("Deve encontrar venda por ID")
    void deveEncontrarVendaPorId() {
        entityManager.persist(vendaEntity);
        entityManager.flush();
        Optional<VendaEntity> resultado = vendaRepository.findById(vendaEntity.getId());
        assertTrue(resultado.isPresent());
        assertEquals(StatusVenda.ATIVA, resultado.get().getStatus());
    }

    @Test
    @DisplayName("Deve retornar todas as vendas")
    void deveRetornarTodasVendas() {
        VendaEntity venda2 = VendaEntity.builder()
                .dataVenda(LocalDateTime.now())
                .cliente(cliente)
                .status(StatusVenda.CANCELADA)
                .itens(new ArrayList<>())
                .build();

        entityManager.persist(vendaEntity);
        entityManager.persist(venda2);
        entityManager.flush();

        List<VendaEntity> vendas = vendaRepository.findAll();
        assertThat(vendas).hasSize(2);
    }

    @Test
    @DisplayName("Deve deletar venda por ID")
    void deveDeletarVendaPorId() {
        entityManager.persist(vendaEntity);
        entityManager.flush();
        UUID vendaId = vendaEntity.getId();

        vendaRepository.deleteById(vendaId);
        entityManager.flush();

        Optional<VendaEntity> resultado = vendaRepository.findById(vendaId);
        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("Deve atualizar status da venda")
    void deveAtualizarStatusDaVenda() {
        entityManager.persist(vendaEntity);
        entityManager.flush();

        vendaEntity.setStatus(StatusVenda.CANCELADA);
        VendaEntity vendaAtualizada = vendaRepository.save(vendaEntity);
        entityManager.flush();

        VendaEntity vendaRecuperada = entityManager.find(VendaEntity.class, vendaAtualizada.getId());
        assertEquals(StatusVenda.CANCELADA, vendaRecuperada.getStatus());
    }

    @Test
    @DisplayName("Deve gerar ID automaticamente")
    void deveGerarIdAutomaticamente() {
        VendaEntity vendaSalva = vendaRepository.save(vendaEntity);
        assertNotNull(vendaSalva.getId());
        assertInstanceOf(UUID.class, vendaSalva.getId());
    }

    @Test
    @DisplayName("Deve retornar lista vazia")
    void deveRetornarListaVazia() {
        List<VendaEntity> vendas = vendaRepository.findAll();
        assertThat(vendas).isEmpty();
    }

    @Test
    @DisplayName("Deve contar vendas")
    void deveContarVendas() {
        entityManager.persist(vendaEntity);
        entityManager.flush();
        long count = vendaRepository.count();
        assertEquals(1, count);
    }

    @Test
    @DisplayName("Deve verificar existência por ID")
    void deveVerificarExistencia() {
        entityManager.persist(vendaEntity);
        entityManager.flush();
        boolean existe = vendaRepository.existsById(vendaEntity.getId());
        assertTrue(existe);
    }
}

