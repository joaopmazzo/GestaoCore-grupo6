package br.com.gestaocore_grupo6_api.modules.usuario.service;

import br.com.gestaocore_grupo6_api.modules.usuario.dto.AtualizarUsuarioRequestDTO;
import br.com.gestaocore_grupo6_api.modules.usuario.dto.UsuarioDTO;
import br.com.gestaocore_grupo6_api.modules.usuario.dto.CriarUsuarioRequestDTO;
import br.com.gestaocore_grupo6_api.modules.usuario.entity.UsuarioEntity;
import br.com.gestaocore_grupo6_api.modules.usuario.mapper.UsuarioMapper;
import br.com.gestaocore_grupo6_api.modules.usuario.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    public List<UsuarioDTO> retornarUsuarios() {
        List<UsuarioEntity> usuarios = usuarioRepository.findAll();
        return usuarios
                .stream()
                .map(usuarioMapper::toDTO)
                .toList();
    }

    public UsuarioDTO retornarUsuarioPorId(UUID id) {
        return usuarioMapper.toDTO(buscarUsuarioPorId(id));
    }

    public UsuarioDTO cadastrarUsuario(CriarUsuarioRequestDTO dto) {
        UsuarioEntity usuario = usuarioMapper.toEntity(dto);
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return usuarioMapper.toDTO(usuarioRepository.save(usuario));
    }

    public UsuarioDTO atualizarUsuario(UUID id, AtualizarUsuarioRequestDTO dto) {
        UsuarioEntity usuarioASerAtualizado = buscarUsuarioPorId(id);
        UsuarioEntity usuarioAtualizado = usuarioMapper.toEntity(dto);
        usuarioAtualizado.setSenha(passwordEncoder.encode(usuarioAtualizado.getSenha()));
        usuarioASerAtualizado.merge(usuarioAtualizado);
        return usuarioMapper.toDTO(usuarioRepository.save(usuarioASerAtualizado));
    }

    public void deletarUsuario(UUID id) {
        buscarUsuarioPorId(id);
        usuarioRepository.deleteById(id);
    }

    private UsuarioEntity buscarUsuarioPorId(UUID id) {
        return usuarioRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
    }

}
