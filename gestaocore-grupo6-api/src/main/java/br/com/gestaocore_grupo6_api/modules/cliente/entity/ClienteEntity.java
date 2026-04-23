package br.com.gestaocore_grupo6_api.modules.cliente.entity;

import java.util.UUID;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cliente")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClienteEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @NotBlank
  private String nome;

  @Email
  @NotBlank
  private String email;

  @NotBlank
  private String telefone;

  @Embedded
  private Endereco endereco;

  public void merge(ClienteEntity cliente) {
    if (cliente.getNome() != null) {
      this.nome = cliente.getNome();
    }
    if (cliente.getEmail() != null) {
      this.email = cliente.getEmail();
    }
    if (cliente.getTelefone() != null) {
      this.telefone = cliente.getTelefone();
    }
    if (cliente.getEndereco() != null) {
      this.endereco = cliente.getEndereco();
    }
  }
}
