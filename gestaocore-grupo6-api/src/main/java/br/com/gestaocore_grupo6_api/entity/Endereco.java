package br.com.gestaocore_grupo6_api.entity;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Embeddable
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Endereco {

  @NotBlank
  private String rua;

  @NotBlank
  private String numero;

  @NotBlank
  private String cidade;

  @NotBlank
  private String estado;

  @NotBlank
  private String cep;

  @NotBlank
  private String complemento;

}
