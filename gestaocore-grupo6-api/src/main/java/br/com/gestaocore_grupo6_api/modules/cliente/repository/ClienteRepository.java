package br.com.gestaocore_grupo6_api.modules.cliente.repository;

import br.com.gestaocore_grupo6_api.modules.cliente.entity.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ClienteRepository extends JpaRepository<ClienteEntity, UUID> {
}
