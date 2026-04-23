package br.com.gestaocore_grupo6_api.modules.venda.controller;

import br.com.gestaocore_grupo6_api.modules.venda.dto.CriarVendaRequestDTO;
import br.com.gestaocore_grupo6_api.modules.venda.dto.VendaDTO;
import br.com.gestaocore_grupo6_api.modules.venda.service.VendaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/venda")
@RequiredArgsConstructor
public class VendaController {

    private final VendaService vendaService;

    @GetMapping(produces = "application/json")
    public ResponseEntity<List<VendaDTO>> retornarVendas() {
        List<VendaDTO> responseDTO = vendaService.retornarVendas();
        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<VendaDTO> retornarVendaPorId(@PathVariable UUID id) {
        VendaDTO responseDTO = vendaService.retornarVendaPorId(id);
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping(produces = "application/json")
    public ResponseEntity<VendaDTO> cadastrarVenda(@RequestBody @Valid CriarVendaRequestDTO dto) {
        VendaDTO responseDTO = vendaService.cadastrarVenda(dto);
        URI location = URI.create("/venda/" + responseDTO.id());
        return ResponseEntity.created(location).body(responseDTO);
    }

    @PatchMapping(value = "/{id}/cancelar", produces = "application/json")
    public ResponseEntity<VendaDTO> cancelarVenda(@PathVariable UUID id) {
        VendaDTO responseDTO = vendaService.cancelarVenda(id);
        return ResponseEntity.ok().body(responseDTO);
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletarVenda(@PathVariable UUID id) {
        vendaService.deletarVenda(id);
        return ResponseEntity.noContent().build();
    }
}
