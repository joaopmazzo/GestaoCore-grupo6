package br.com.gestaocore_grupo6_api.controller;

import br.com.gestaocore_grupo6_api.dto.request.UsuarioLoginRequestDTO;
import br.com.gestaocore_grupo6_api.dto.response.UsuarioLoginResponseDTO;
import br.com.gestaocore_grupo6_api.security.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {

    private final AuthenticationService authenticationService;

    @PostMapping
    public ResponseEntity<Object> login(@RequestBody @Valid UsuarioLoginRequestDTO dto) {
        try {
            UsuarioLoginResponseDTO responseDTO = authenticationService.autenticar(dto);
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public void requestProtegida() {}

}
