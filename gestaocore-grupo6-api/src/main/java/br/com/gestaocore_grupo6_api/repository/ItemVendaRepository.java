package br.com.gestaocore_grupo6_api.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.gestaocore_grupo6_api.entity.ItemVenda;
import br.com.gestaocore_grupo6_api.entity.Venda;

@Repository
public interface ItemVendaRepository extends JpaRepository<ItemVenda, UUID> {

  List<ItemVenda> findByVenda(Venda venda);

}
