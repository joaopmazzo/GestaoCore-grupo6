package br.com.gestaocore_grupo6_api.entity;

import java.util.UUID;

import br.com.gestaocore_grupo6_api.enums.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Usuario {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @NotBlank
  private String nome;

  @Email
  @NotBlank
  private String email;

  @NotBlank
  @Size(min = 6, max = 12)
  private String senha;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Builder.Default
  private Role role = Role.USER;

}
