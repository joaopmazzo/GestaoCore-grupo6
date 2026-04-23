package br.com.gestaocore_grupo6_api.modules.venda.service;

import br.com.gestaocore_grupo6_api.modules.cliente.entity.ClienteEntity;
import br.com.gestaocore_grupo6_api.modules.cliente.repository.ClienteRepository;
import br.com.gestaocore_grupo6_api.modules.produto.entity.ProdutoEntity;
import br.com.gestaocore_grupo6_api.modules.produto.repository.ProdutoRepository;
import br.com.gestaocore_grupo6_api.modules.venda.dto.CriarItemVendaRequestDTO;
import br.com.gestaocore_grupo6_api.modules.venda.dto.CriarVendaRequestDTO;
import br.com.gestaocore_grupo6_api.modules.venda.dto.VendaDTO;
import br.com.gestaocore_grupo6_api.modules.venda.entity.ItemVendaEntity;
import br.com.gestaocore_grupo6_api.modules.venda.entity.VendaEntity;
import br.com.gestaocore_grupo6_api.modules.venda.enums.StatusVenda;
import br.com.gestaocore_grupo6_api.modules.venda.mapper.VendaMapper;
import br.com.gestaocore_grupo6_api.modules.venda.repository.VendaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VendaService {

    private final VendaRepository vendaRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final VendaMapper vendaMapper;

    public List<VendaDTO> retornarVendas() {
        List<VendaEntity> vendas = vendaRepository.findAll();
        return vendas
                .stream()
                .map(vendaMapper::toDTO)
                .toList();
    }

    public VendaDTO retornarVendaPorId(UUID id) {
        return vendaMapper.toDTO(buscarVendaPorId(id));
    }

    @Transactional
    public VendaDTO cadastrarVenda(CriarVendaRequestDTO dto) {
        ClienteEntity cliente = clienteRepository
                .findById(dto.clienteId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));

        VendaEntity venda = VendaEntity.builder()
                .dataVenda(dto.dataVenda())
                .cliente(cliente)
                .status(StatusVenda.ATIVA)
                .build();

        // Processa cada item da venda
        List<ItemVendaEntity> itens = dto.itens().stream()
                .map(itemDto -> criarItemVenda(itemDto, venda))
                .toList();

        venda.setItens(itens);
        VendaEntity vendaSalva = vendaRepository.save(venda);

        return vendaMapper.toDTO(vendaSalva);
    }

    @Transactional
    public VendaDTO cancelarVenda(UUID id) {
        VendaEntity venda = buscarVendaPorId(id);

        if (venda.getStatus() == StatusVenda.CANCELADA) {
            throw new IllegalStateException("Esta venda já está cancelada");
        }

        // Restaura o estoque de cada item
        venda.getItens().forEach(item -> {
            ProdutoEntity produto = item.getProduto();
            produto.setQtdEstoque(produto.getQtdEstoque() + item.getQuantidade());
            produtoRepository.save(produto);
        });

        venda.setStatus(StatusVenda.CANCELADA);
        VendaEntity vendaCancelada = vendaRepository.save(venda);

        return vendaMapper.toDTO(vendaCancelada);
    }

    @Transactional
    public void deletarVenda(UUID id) {
        VendaEntity venda = buscarVendaPorId(id);

        // Se a venda está ativa, precisa cancelar primeiro para restaurar o estoque
        if (venda.getStatus() == StatusVenda.ATIVA) {
            throw new IllegalStateException("Não é possível deletar uma venda ativa. Cancele a venda primeiro.");
        }

        vendaRepository.deleteById(id);
    }

    private VendaEntity buscarVendaPorId(UUID id) {
        return vendaRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Venda não encontrada"));
    }

    private ItemVendaEntity criarItemVenda(CriarItemVendaRequestDTO dto, VendaEntity venda) {
        ProdutoEntity produto = produtoRepository
                .findById(dto.produtoId())
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

        // Valida se há estoque suficiente
        if (produto.getQtdEstoque() < dto.quantidade()) {
            throw new IllegalArgumentException(
                    String.format("Estoque insuficiente para o produto '%s'. Disponível: %.2f, Solicitado: %.2f",
                            produto.getNome(),
                            produto.getQtdEstoque(),
                            dto.quantidade())
            );
        }

        // Captura o preço atual do produto (valorNaVenda = preço unitário no momento da venda)
        Double valorNaVenda = produto.getPreco();
        Double valorTotal = dto.quantidade() * valorNaVenda;

        // Atualiza o estoque do produto
        produto.setQtdEstoque(produto.getQtdEstoque() - dto.quantidade());
        produtoRepository.save(produto);

        return ItemVendaEntity.builder()
                .produto(produto)
                .quantidade(dto.quantidade())
                .valorNaVenda(valorNaVenda)
                .valorTotal(valorTotal)
                .venda(venda)
                .build();
    }
}
