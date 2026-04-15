package br.com.gestaocore_grupo6_api.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.gestaocore_grupo6_api.entity.Cliente;
import br.com.gestaocore_grupo6_api.entity.Venda;

@Repository
public interface VendaRepository extends JpaRepository<Venda, UUID> {

  List<Venda> findByCliente(Cliente cliente);

  List<Venda> findByDataVendaBetween(LocalDateTime dataInicio, LocalDateTime dataFim);

}
