package br.com.gestaocore_grupo6_api.modules.venda.entity;

import br.com.gestaocore_grupo6_api.modules.produto.entity.ProdutoEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "item_venda")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemVendaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "produto_id")
    private ProdutoEntity produto;

    @NotNull
    private Double quantidade;

    @NotNull
    private Double valorTotal;

    @NotNull
    private Double valorNaVenda;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "venda_id")
    private VendaEntity venda;

}
