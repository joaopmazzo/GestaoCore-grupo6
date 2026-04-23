package br.com.gestaocore_grupo6_api.modules.usuario.controller;

import br.com.gestaocore_grupo6_api.modules.usuario.dto.AtualizarUsuarioRequestDTO;
import br.com.gestaocore_grupo6_api.modules.usuario.dto.UsuarioDTO;
import br.com.gestaocore_grupo6_api.modules.usuario.dto.CriarUsuarioRequestDTO;
import br.com.gestaocore_grupo6_api.modules.usuario.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping(produces = "application/json")
    public ResponseEntity<List<UsuarioDTO>> retornarUsuarios() {
        List<UsuarioDTO> responseDTO = usuarioService.retornarUsuarios();
        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<UsuarioDTO> retornarUsuarioPorId(@PathVariable UUID id) {
        UsuarioDTO responseDTO = usuarioService.retornarUsuarioPorId(id);
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping(produces = "application/json")
    public ResponseEntity<UsuarioDTO> cadastrarUsuario(@RequestBody @Valid CriarUsuarioRequestDTO dto) {
        UsuarioDTO responseDTO = usuarioService.cadastrarUsuario(dto);
        URI location = URI.create("/usuarios/" + responseDTO.id());
        return ResponseEntity.created(location).body(responseDTO);
    }

    @PutMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<UsuarioDTO> atualizarUsuario(@PathVariable UUID id, @RequestBody @Valid AtualizarUsuarioRequestDTO dto) {
        UsuarioDTO responseDTO = usuarioService.atualizarUsuario(id, dto);
        return ResponseEntity.ok().body(responseDTO);
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletarUsuario(@PathVariable UUID id) {
        usuarioService.deletarUsuario(id);
        return ResponseEntity.noContent().build();
    }

}
