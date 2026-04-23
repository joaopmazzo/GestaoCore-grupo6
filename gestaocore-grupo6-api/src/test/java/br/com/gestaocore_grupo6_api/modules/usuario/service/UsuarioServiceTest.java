package br.com.gestaocore_grupo6_api.modules.usuario.service;

import br.com.gestaocore_grupo6_api.modules.usuario.dto.AtualizarUsuarioRequestDTO;
import br.com.gestaocore_grupo6_api.modules.usuario.dto.CriarUsuarioRequestDTO;
import br.com.gestaocore_grupo6_api.modules.usuario.dto.UsuarioDTO;
import br.com.gestaocore_grupo6_api.modules.usuario.entity.UsuarioEntity;
import br.com.gestaocore_grupo6_api.modules.usuario.enums.Role;
import br.com.gestaocore_grupo6_api.modules.usuario.mapper.UsuarioMapper;
import br.com.gestaocore_grupo6_api.modules.usuario.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - UsuarioService")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioMapper usuarioMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private UUID usuarioId;
    private UsuarioEntity usuarioEntity;
    private UsuarioDTO usuarioDTO;
    private CriarUsuarioRequestDTO criarUsuarioDTO;
    private AtualizarUsuarioRequestDTO atualizarUsuarioDTO;

    @BeforeEach
    void setUp() {
        usuarioId = UUID.randomUUID();

        usuarioEntity = UsuarioEntity.builder()
                .id(usuarioId)
                .nome("João Silva")
                .email("joao@email.com")
                .senha("senhaEncriptada")
                .role(Role.USER)
                .build();

        usuarioDTO = new UsuarioDTO(
                usuarioId,
                "João Silva",
                "joao@email.com",
                "USER"
        );

        criarUsuarioDTO = new CriarUsuarioRequestDTO(
                "João Silva",
                "joao@email.com",
                "senha123",
                Role.USER
        );

        atualizarUsuarioDTO = new AtualizarUsuarioRequestDTO(
                "João Silva Atualizado",
                "joao.novo@email.com",
                "novaSenha123",
                Role.ADMIN
        );
    }

    @Test
    @DisplayName("Deve retornar lista de todos os usuários")
    void deveRetornarListaDeUsuarios() {
        // Arrange
        UsuarioEntity usuario2 = UsuarioEntity.builder()
                .id(UUID.randomUUID())
                .nome("Maria Santos")
                .email("maria@email.com")
                .senha("senha")
                .role(Role.ADMIN)
                .build();

        List<UsuarioEntity> usuarios = Arrays.asList(usuarioEntity, usuario2);
        UsuarioDTO usuarioDTO2 = new UsuarioDTO(usuario2.getId(), "Maria Santos", "maria@email.com", "ADMIN");

        when(usuarioRepository.findAll()).thenReturn(usuarios);
        when(usuarioMapper.toDTO(usuarioEntity)).thenReturn(usuarioDTO);
        when(usuarioMapper.toDTO(usuario2)).thenReturn(usuarioDTO2);

        // Act
        List<UsuarioDTO> resultado = usuarioService.retornarUsuarios();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("João Silva", resultado.get(0).nome());
        assertEquals("Maria Santos", resultado.get(1).nome());
        verify(usuarioRepository, times(1)).findAll();
        verify(usuarioMapper, times(2)).toDTO(any(UsuarioEntity.class));
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há usuários")
    void deveRetornarListaVaziaQuandoNaoHaUsuarios() {
        // Arrange
        when(usuarioRepository.findAll()).thenReturn(List.of());

        // Act
        List<UsuarioDTO> resultado = usuarioService.retornarUsuarios();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(usuarioRepository, times(1)).findAll();
        verify(usuarioMapper, never()).toDTO(any(UsuarioEntity.class));
    }

    @Test
    @DisplayName("Deve retornar usuário por ID com sucesso")
    void deveRetornarUsuarioPorId() {
        // Arrange
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuarioEntity));
        when(usuarioMapper.toDTO(usuarioEntity)).thenReturn(usuarioDTO);

        // Act
        UsuarioDTO resultado = usuarioService.retornarUsuarioPorId(usuarioId);

        // Assert
        assertNotNull(resultado);
        assertEquals(usuarioId, resultado.id());
        assertEquals("João Silva", resultado.nome());
        assertEquals("joao@email.com", resultado.email());
        verify(usuarioRepository, times(1)).findById(usuarioId);
        verify(usuarioMapper, times(1)).toDTO(usuarioEntity);
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException quando usuário não existe")
    void deveLancarExcecaoQuandoUsuarioNaoExiste() {
        // Arrange
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> usuarioService.retornarUsuarioPorId(usuarioId)
        );

        assertEquals("Usuário não encontrado", exception.getMessage());
        verify(usuarioRepository, times(1)).findById(usuarioId);
        verify(usuarioMapper, never()).toDTO(any(UsuarioEntity.class));
    }

    @Test
    @DisplayName("Deve cadastrar novo usuário com sucesso")
    void deveCadastrarUsuarioComSucesso() {
        // Arrange
        UsuarioEntity usuarioNovo = UsuarioEntity.builder()
                .nome("João Silva")
                .email("joao@email.com")
                .senha("senha123")
                .role(Role.USER)
                .build();

        when(usuarioMapper.toEntity(criarUsuarioDTO)).thenReturn(usuarioNovo);
        when(passwordEncoder.encode("senha123")).thenReturn("senhaEncriptada");
        when(usuarioRepository.save(any(UsuarioEntity.class))).thenReturn(usuarioEntity);
        when(usuarioMapper.toDTO(usuarioEntity)).thenReturn(usuarioDTO);

        // Act
        UsuarioDTO resultado = usuarioService.cadastrarUsuario(criarUsuarioDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals("João Silva", resultado.nome());
        assertEquals("joao@email.com", resultado.email());
        verify(usuarioMapper, times(1)).toEntity(criarUsuarioDTO);
        verify(passwordEncoder, times(1)).encode("senha123");
        verify(usuarioRepository, times(1)).save(any(UsuarioEntity.class));
        verify(usuarioMapper, times(1)).toDTO(usuarioEntity);
    }

    @Test
    @DisplayName("Deve criptografar senha ao cadastrar usuário")
    void deveCriptografarSenhaAoCadastrarUsuario() {
        // Arrange
        UsuarioEntity usuarioNovo = UsuarioEntity.builder()
                .nome("João Silva")
                .email("joao@email.com")
                .senha("senha123")
                .role(Role.USER)
                .build();

        when(usuarioMapper.toEntity(criarUsuarioDTO)).thenReturn(usuarioNovo);
        when(passwordEncoder.encode("senha123")).thenReturn("senhaEncriptada");
        when(usuarioRepository.save(any(UsuarioEntity.class))).thenAnswer(invocation -> {
            UsuarioEntity saved = invocation.getArgument(0);
            assertEquals("senhaEncriptada", saved.getSenha());
            return saved;
        });
        when(usuarioMapper.toDTO(any(UsuarioEntity.class))).thenReturn(usuarioDTO);

        // Act
        usuarioService.cadastrarUsuario(criarUsuarioDTO);

        // Assert
        verify(passwordEncoder, times(1)).encode("senha123");
    }

    @Test
    @DisplayName("Deve atualizar usuário com sucesso")
    void deveAtualizarUsuarioComSucesso() {
        // Arrange
        UsuarioEntity usuarioAtualizado = UsuarioEntity.builder()
                .nome("João Silva Atualizado")
                .email("joao.novo@email.com")
                .senha("novaSenha123")
                .role(Role.ADMIN)
                .build();

        UsuarioDTO usuarioDTOAtualizado = new UsuarioDTO(
                usuarioId,
                "João Silva Atualizado",
                "joao.novo@email.com",
                "ADMIN"
        );

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuarioEntity));
        when(usuarioMapper.toEntity(atualizarUsuarioDTO)).thenReturn(usuarioAtualizado);
        when(passwordEncoder.encode("novaSenha123")).thenReturn("novaSenhaEncriptada");
        when(usuarioRepository.save(usuarioEntity)).thenReturn(usuarioEntity);
        when(usuarioMapper.toDTO(usuarioEntity)).thenReturn(usuarioDTOAtualizado);

        // Act
        UsuarioDTO resultado = usuarioService.atualizarUsuario(usuarioId, atualizarUsuarioDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals("João Silva Atualizado", resultado.nome());
        assertEquals("joao.novo@email.com", resultado.email());
        assertEquals("ADMIN", resultado.role());
        verify(usuarioRepository, times(1)).findById(usuarioId);
        verify(usuarioMapper, times(1)).toEntity(atualizarUsuarioDTO);
        verify(passwordEncoder, times(1)).encode("novaSenha123");
        verify(usuarioRepository, times(1)).save(usuarioEntity);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar usuário inexistente")
    void deveLancarExcecaoAoAtualizarUsuarioInexistente() {
        // Arrange
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> usuarioService.atualizarUsuario(usuarioId, atualizarUsuarioDTO)
        );

        assertEquals("Usuário não encontrado", exception.getMessage());
        verify(usuarioRepository, times(1)).findById(usuarioId);
        verify(usuarioRepository, never()).save(any(UsuarioEntity.class));
    }

    @Test
    @DisplayName("Deve deletar usuário com sucesso")
    void deveDeletarUsuarioComSucesso() {
        // Arrange
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuarioEntity));
        doNothing().when(usuarioRepository).deleteById(usuarioId);

        // Act
        usuarioService.deletarUsuario(usuarioId);

        // Assert
        verify(usuarioRepository, times(1)).findById(usuarioId);
        verify(usuarioRepository, times(1)).deleteById(usuarioId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar usuário inexistente")
    void deveLancarExcecaoAoDeletarUsuarioInexistente() {
        // Arrange
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> usuarioService.deletarUsuario(usuarioId)
        );

        assertEquals("Usuário não encontrado", exception.getMessage());
        verify(usuarioRepository, times(1)).findById(usuarioId);
        verify(usuarioRepository, never()).deleteById(any(UUID.class));
    }
}
