package br.com.gestaocore_grupo6_api.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.gestaocore_grupo6_api.entity.Produto;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, UUID> {

  List<Produto> findByCategoria(String categoria);

  List<Produto> findByNomeContainingIgnoreCase(String nome);

}
