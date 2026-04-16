package br.com.gestaocore_grupo6_api.modules.usuario.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
  ADMIN("ROLE_ADMIN"),
  USER("ROLE_USER"),
  GERENTE("ROLE_GERENTE");

  private final String authority;

  Role(String authority) {
    this.authority = authority;
  }

  @Override
  public String getAuthority() {
    return authority;
  }

}
