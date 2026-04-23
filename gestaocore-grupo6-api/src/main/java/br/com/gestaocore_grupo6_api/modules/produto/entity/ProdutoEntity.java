package br.com.gestaocore_grupo6_api.modules.produto.entity;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "produto")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProdutoEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @NotBlank
  private String nome;

  @NotBlank
  private String categoria;

  @NotNull
  @DecimalMin("0.1")
  private Double preco;

  @NotNull
  @DecimalMin("0.1")
  private Double qtdEstoque;

  public void merge(ProdutoEntity produto) {
    if (produto.getNome() != null) {
      this.nome = produto.getNome();
    }
    if (produto.getCategoria() != null) {
      this.categoria = produto.getCategoria();
    }
    if (produto.getPreco() != null) {
      this.preco = produto.getPreco();
    }
    if (produto.getQtdEstoque() != null) {
      this.qtdEstoque = produto.getQtdEstoque();
    }
  }
}
