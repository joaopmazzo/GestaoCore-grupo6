package br.com.gestaocore_grupo6_api.modules.produto.controller;

import br.com.gestaocore_grupo6_api.modules.produto.dto.AtualizarProdutoRequestDTO;
import br.com.gestaocore_grupo6_api.modules.produto.dto.ProdutoDTO;
import br.com.gestaocore_grupo6_api.modules.produto.dto.CriarProdutoRequestDTO;
import br.com.gestaocore_grupo6_api.modules.produto.service.ProdutoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/produto")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService produtoService;

    @GetMapping(produces = "application/json")
    public ResponseEntity<List<ProdutoDTO>> retornarProdutos() {
        List<ProdutoDTO> responseDTO = produtoService.retornarProdutos();
        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<ProdutoDTO> retornarProdutoPorId(@PathVariable UUID id) {
        ProdutoDTO responseDTO = produtoService.retornarProdutoPorId(id);
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping(produces = "application/json")
    public ResponseEntity<ProdutoDTO> cadastrarProduto(@RequestBody @Valid CriarProdutoRequestDTO dto) {
        ProdutoDTO responseDTO = produtoService.cadastrarProduto(dto);
        URI location = URI.create("/produto/" + responseDTO.id());
        return ResponseEntity.created(location).body(responseDTO);
    }

    @PutMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<ProdutoDTO> atualizarProduto(@PathVariable UUID id, @RequestBody @Valid AtualizarProdutoRequestDTO dto) {
        ProdutoDTO responseDTO = produtoService.atualizarProduto(id, dto);
        return ResponseEntity.ok().body(responseDTO);
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletarProduto(@PathVariable UUID id) {
        produtoService.deletarProduto(id);
        return ResponseEntity.noContent().build();
    }

}
