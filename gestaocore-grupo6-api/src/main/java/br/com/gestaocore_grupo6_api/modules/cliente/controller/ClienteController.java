package br.com.gestaocore_grupo6_api.modules.cliente.controller;

import br.com.gestaocore_grupo6_api.modules.cliente.dto.AtualizarClienteRequestDTO;
import br.com.gestaocore_grupo6_api.modules.cliente.dto.ClienteDTO;
import br.com.gestaocore_grupo6_api.modules.cliente.dto.CriarClienteRequestDTO;
import br.com.gestaocore_grupo6_api.modules.cliente.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/cliente")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping(produces = "application/json")
    public ResponseEntity<List<ClienteDTO>> retornarClientes() {
        List<ClienteDTO> responseDTO = clienteService.retornarClientes();
        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<ClienteDTO> retornarClientePorId(@PathVariable UUID id) {
        ClienteDTO responseDTO = clienteService.retornarClientePorId(id);
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping(produces = "application/json")
    public ResponseEntity<ClienteDTO> cadastrarCliente(@RequestBody @Valid CriarClienteRequestDTO dto) {
        ClienteDTO responseDTO = clienteService.cadastrarCliente(dto);
        URI location = URI.create("/cliente/" + responseDTO.id());
        return ResponseEntity.created(location).body(responseDTO);
    }

    @PutMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<ClienteDTO> atualizarCliente(@PathVariable UUID id, @RequestBody @Valid AtualizarClienteRequestDTO dto) {
        ClienteDTO responseDTO = clienteService.atualizarCliente(id, dto);
        return ResponseEntity.ok().body(responseDTO);
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletarCliente(@PathVariable UUID id) {
        clienteService.deletarCliente(id);
        return ResponseEntity.noContent().build();
    }

}
