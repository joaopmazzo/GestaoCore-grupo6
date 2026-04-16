package br.com.gestaocore_grupo6_api.modules.usuario.controller;

import br.com.gestaocore_grupo6_api.modules.usuario.dto.UsuarioLoginRequestDTO;
import br.com.gestaocore_grupo6_api.modules.usuario.dto.UsuarioLoginResponseDTO;
import br.com.gestaocore_grupo6_api.modules.usuario.service.LoginUsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.AuthenticationException;

@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor
public class LoginUsuarioController {

    private final LoginUsuarioService loginUsuarioService;

    @PostMapping("/login")
    public ResponseEntity<UsuarioLoginResponseDTO> login(@RequestBody @Valid UsuarioLoginRequestDTO dto) throws AuthenticationException {
        UsuarioLoginResponseDTO responseDTO = loginUsuarioService.autenticar(dto);
        return ResponseEntity.ok().body(responseDTO);
    }

}

