package br.com.gestaocore_grupo6_api.modules.usuario.entity;

import java.util.UUID;

import br.com.gestaocore_grupo6_api.modules.usuario.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "usuario")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @NotBlank
  @Size(min = 3, max = 255)
  private String nome;

  @Email
  @NotBlank
  private String email;

  @NotBlank
  private String senha;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Builder.Default
  private Role role = Role.USER;

  public void merge(UsuarioEntity usuario) {
    if (usuario.getNome() != null) {
      this.nome = usuario.getNome();
    }
    if (usuario.getEmail() != null) {
      this.email = usuario.getEmail();
    }
    if (usuario.getSenha() != null) {
      this.senha = usuario.getSenha();
    }
    if (usuario.getRole() != null) {
      this.role = usuario.getRole();
    }
  }

}
