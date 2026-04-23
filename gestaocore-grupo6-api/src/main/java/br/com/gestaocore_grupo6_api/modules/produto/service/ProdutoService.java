package br.com.gestaocore_grupo6_api.modules.produto.service;

import br.com.gestaocore_grupo6_api.modules.produto.dto.AtualizarProdutoRequestDTO;
import br.com.gestaocore_grupo6_api.modules.produto.dto.ProdutoDTO;
import br.com.gestaocore_grupo6_api.modules.produto.dto.CriarProdutoRequestDTO;
import br.com.gestaocore_grupo6_api.modules.produto.entity.ProdutoEntity;
import br.com.gestaocore_grupo6_api.modules.produto.mapper.ProdutoMapper;
import br.com.gestaocore_grupo6_api.modules.produto.repository.ProdutoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final ProdutoMapper produtoMapper;

    public List<ProdutoDTO> retornarProdutos() {
        List<ProdutoEntity> produtos = produtoRepository.findAll();
        return produtos
                .stream()
                .map(produtoMapper::toDTO)
                .toList();
    }

    public ProdutoDTO retornarProdutoPorId(UUID id) {
        return produtoMapper.toDTO(buscarProdutoPorId(id));
    }

    public ProdutoDTO cadastrarProduto(CriarProdutoRequestDTO dto) {
        ProdutoEntity produto = produtoMapper.toEntity(dto);
        return produtoMapper.toDTO(produtoRepository.save(produto));
    }

    public ProdutoDTO atualizarProduto(UUID id, AtualizarProdutoRequestDTO dto) {
        ProdutoEntity produtoASerAtualizado = buscarProdutoPorId(id);
        ProdutoEntity produtoAtualizado = produtoMapper.toEntity(dto);
        produtoASerAtualizado.merge(produtoAtualizado);
        return produtoMapper.toDTO(produtoRepository.save(produtoASerAtualizado));
    }

    public void deletarProduto(UUID id) {
        buscarProdutoPorId(id);
        produtoRepository.deleteById(id);
    }

    private ProdutoEntity buscarProdutoPorId(UUID id) {
        return produtoRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));
    }

}
