package br.com.gestaocore_grupo6_api.modules.venda.entity;

import br.com.gestaocore_grupo6_api.modules.cliente.entity.ClienteEntity;
import br.com.gestaocore_grupo6_api.modules.venda.enums.StatusVenda;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "venda")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VendaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    private LocalDateTime dataVenda;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private ClienteEntity cliente;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private StatusVenda status = StatusVenda.ATIVA;

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ItemVendaEntity> itens = new ArrayList<>();
}
