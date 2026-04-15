package br.com.gestaocore_grupo6_api.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.gestaocore_grupo6_api.entity.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, UUID> {

  Optional<Cliente> findByEmail(String email);

}
