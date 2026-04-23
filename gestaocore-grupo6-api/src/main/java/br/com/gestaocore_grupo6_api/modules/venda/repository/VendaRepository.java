package br.com.gestaocore_grupo6_api.modules.venda.repository;

import br.com.gestaocore_grupo6_api.modules.venda.entity.VendaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VendaRepository extends JpaRepository<VendaEntity, UUID> {
}
