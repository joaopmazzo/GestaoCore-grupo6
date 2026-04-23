package br.com.gestaocore_grupo6_api.modules.produto.repository;

import br.com.gestaocore_grupo6_api.modules.produto.entity.ProdutoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProdutoRepository extends JpaRepository<ProdutoEntity, UUID> {
}
