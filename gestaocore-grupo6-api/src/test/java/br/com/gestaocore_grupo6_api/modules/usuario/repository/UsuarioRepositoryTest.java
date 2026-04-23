package br.com.gestaocore_grupo6_api.modules.usuario.repository;

import br.com.gestaocore_grupo6_api.modules.usuario.entity.UsuarioEntity;
import br.com.gestaocore_grupo6_api.modules.usuario.enums.Role;
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
@DisplayName("Testes de Repositório - UsuarioRepository")
@ActiveProfiles("test")
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EntityManager entityManager;

    private UsuarioEntity usuarioEntity;

    @BeforeEach
    void setUp() {
        usuarioEntity = UsuarioEntity.builder()
                .nome("João Silva")
                .email("joao@email.com")
                .senha("senhaEncriptada")
                .role(Role.USER)
                .build();
    }

    @Test
    @DisplayName("Deve salvar usuário no banco de dados")
    void deveSalvarUsuario() {
        // Act
        UsuarioEntity usuarioSalvo = usuarioRepository.save(usuarioEntity);

        // Assert
        assertNotNull(usuarioSalvo.getId());
        assertEquals("João Silva", usuarioSalvo.getNome());
        assertEquals("joao@email.com", usuarioSalvo.getEmail());
        assertEquals("senhaEncriptada", usuarioSalvo.getSenha());
        assertEquals(Role.USER, usuarioSalvo.getRole());
    }

    @Test
    @DisplayName("Deve encontrar usuário por email")
    void deveEncontrarUsuarioPorEmail() {
        // Arrange
        entityManager.persist(usuarioEntity);
        entityManager.flush();

        // Act
        Optional<UsuarioEntity> resultado = usuarioRepository.findByEmail("joao@email.com");

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("João Silva", resultado.get().getNome());
        assertEquals("joao@email.com", resultado.get().getEmail());
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando email não existe")
    void deveRetornarVazioQuandoEmailNaoExiste() {
        // Act
        Optional<UsuarioEntity> resultado = usuarioRepository.findByEmail("inexistente@email.com");

        // Assert
        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("Deve encontrar usuário por ID")
    void deveEncontrarUsuarioPorId() {
        // Arrange
        entityManager.persist(usuarioEntity);
        entityManager.flush();

        // Act
        Optional<UsuarioEntity> resultado = usuarioRepository.findById(usuarioEntity.getId());

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(usuarioEntity.getId(), resultado.get().getId());
        assertEquals("João Silva", resultado.get().getNome());
    }

    @Test
    @DisplayName("Deve retornar todos os usuários")
    void deveRetornarTodosUsuarios() {
        // Arrange
        UsuarioEntity usuario2 = UsuarioEntity.builder()
                .nome("Maria Santos")
                .email("maria@email.com")
                .senha("senha123")
                .role(Role.ADMIN)
                .build();

        entityManager.persist(usuarioEntity);
        entityManager.persist(usuario2);
        entityManager.flush();

        // Act
        List<UsuarioEntity> usuarios = usuarioRepository.findAll();

        // Assert
        assertThat(usuarios).hasSize(2);
        assertThat(usuarios).extracting(UsuarioEntity::getEmail)
                .contains("joao@email.com", "maria@email.com");
    }

    @Test
    @DisplayName("Deve deletar usuário por ID")
    void deveDeletarUsuarioPorId() {
        // Arrange
        entityManager.persist(usuarioEntity);
        entityManager.flush();
        UUID usuarioId = usuarioEntity.getId();

        // Act
        usuarioRepository.deleteById(usuarioId);
        entityManager.flush();

        // Assert
        Optional<UsuarioEntity> resultado = usuarioRepository.findById(usuarioId);
        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("Deve atualizar dados do usuário")
    void deveAtualizarDadosDoUsuario() {
        // Arrange
        entityManager.persist(usuarioEntity);
        entityManager.flush();

        // Act
        usuarioEntity.setNome("João Silva Atualizado");
        usuarioEntity.setEmail("joao.novo@email.com");
        usuarioEntity.setRole(Role.ADMIN);
        UsuarioEntity usuarioAtualizado = usuarioRepository.save(usuarioEntity);
        entityManager.flush();

        // Assert
        UsuarioEntity usuarioRecuperado = entityManager.find(UsuarioEntity.class, usuarioAtualizado.getId());
        assertEquals("João Silva Atualizado", usuarioRecuperado.getNome());
        assertEquals("joao.novo@email.com", usuarioRecuperado.getEmail());
        assertEquals(Role.ADMIN, usuarioRecuperado.getRole());
    }

    @Test
    @DisplayName("Deve gerar ID automaticamente ao salvar usuário")
    void deveGerarIdAutomaticamente() {
        // Act
        UsuarioEntity usuarioSalvo = usuarioRepository.save(usuarioEntity);

        // Assert
        assertNotNull(usuarioSalvo.getId());
        assertInstanceOf(UUID.class, usuarioSalvo.getId());
    }

    @Test
    @DisplayName("Deve salvar usuário com role padrão USER")
    void deveSalvarUsuarioComRolePadrao() {
        // Arrange
        UsuarioEntity usuario = UsuarioEntity.builder()
                .nome("Teste Usuario")
                .email("teste@email.com")
                .senha("senha")
                .build();

        // Act
        UsuarioEntity usuarioSalvo = usuarioRepository.save(usuario);

        // Assert
        assertEquals(Role.USER, usuarioSalvo.getRole());
    }

    @Test
    @DisplayName("Deve salvar usuário com role ADMIN")
    void deveSalvarUsuarioComRoleAdmin() {
        // Arrange
        usuarioEntity.setRole(Role.ADMIN);

        // Act
        UsuarioEntity usuarioSalvo = usuarioRepository.save(usuarioEntity);

        // Assert
        assertEquals(Role.ADMIN, usuarioSalvo.getRole());
    }

    @Test
    @DisplayName("Deve salvar usuário com role GERENTE")
    void deveSalvarUsuarioComRoleGerente() {
        // Arrange
        usuarioEntity.setRole(Role.GERENTE);

        // Act
        UsuarioEntity usuarioSalvo = usuarioRepository.save(usuarioEntity);

        // Assert
        assertEquals(Role.GERENTE, usuarioSalvo.getRole());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há usuários")
    void deveRetornarListaVaziaQuandoNaoHaUsuarios() {
        // Act
        List<UsuarioEntity> usuarios = usuarioRepository.findAll();

        // Assert
        assertThat(usuarios).isEmpty();
    }

    @Test
    @DisplayName("Deve buscar usuário por email case-sensitive")
    void deveBuscarUsuarioPorEmailCaseSensitive() {
        // Arrange
        entityManager.persist(usuarioEntity);
        entityManager.flush();

        // Act
        Optional<UsuarioEntity> resultadoMinusculo = usuarioRepository.findByEmail("joao@email.com");
        Optional<UsuarioEntity> resultadoMaiusculo = usuarioRepository.findByEmail("JOAO@EMAIL.COM");

        // Assert
        assertTrue(resultadoMinusculo.isPresent());
        assertFalse(resultadoMaiusculo.isPresent());
    }

    @Test
    @DisplayName("Deve persistir senha encriptada")
    void devePersistirSenhaEncriptada() {
        // Arrange
        usuarioEntity.setSenha("$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy");

        // Act
        UsuarioEntity usuarioSalvo = usuarioRepository.save(usuarioEntity);
        entityManager.flush();

        // Assert
        UsuarioEntity usuarioRecuperado = entityManager.find(UsuarioEntity.class, usuarioSalvo.getId());
        assertEquals("$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy",
                usuarioRecuperado.getSenha());
    }

    @Test
    @DisplayName("Deve contar número de usuários no banco")
    void deveContarNumeroDeUsuarios() {
        // Arrange
        entityManager.persist(usuarioEntity);
        entityManager.persist(UsuarioEntity.builder()
                .nome("Outro Usuario")
                .email("outro@email.com")
                .senha("senha")
                .role(Role.USER)
                .build());
        entityManager.flush();

        // Act
        long count = usuarioRepository.count();

        // Assert
        assertEquals(2, count);
    }

    @Test
    @DisplayName("Deve verificar se usuário existe por ID")
    void deveVerificarSeUsuarioExistePorId() {
        // Arrange
        entityManager.persist(usuarioEntity);
        entityManager.flush();

        // Act
        boolean existe = usuarioRepository.existsById(usuarioEntity.getId());
        boolean naoExiste = usuarioRepository.existsById(UUID.randomUUID());

        // Assert
        assertTrue(existe);
        assertFalse(naoExiste);
    }
}
