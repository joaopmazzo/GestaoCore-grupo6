package br.com.gestaocore_grupo6_api.modules.cliente.service;

import br.com.gestaocore_grupo6_api.modules.cliente.dto.AtualizarClienteRequestDTO;
import br.com.gestaocore_grupo6_api.modules.cliente.dto.ClienteDTO;
import br.com.gestaocore_grupo6_api.modules.cliente.dto.CriarClienteRequestDTO;
import br.com.gestaocore_grupo6_api.modules.cliente.entity.ClienteEntity;
import br.com.gestaocore_grupo6_api.modules.cliente.mapper.ClienteMapper;
import br.com.gestaocore_grupo6_api.modules.cliente.repository.ClienteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    public List<ClienteDTO> retornarClientes() {
        List<ClienteEntity> clientes = clienteRepository.findAll();
        return clientes
                .stream()
                .map(clienteMapper::toDTO)
                .toList();
    }

    public ClienteDTO retornarClientePorId(UUID id) {
        return clienteMapper.toDTO(buscarClientePorId(id));
    }

    public ClienteDTO cadastrarCliente(CriarClienteRequestDTO dto) {
        ClienteEntity cliente = clienteMapper.toEntity(dto);
        return clienteMapper.toDTO(clienteRepository.save(cliente));
    }

    public ClienteDTO atualizarCliente(UUID id, AtualizarClienteRequestDTO dto) {
        ClienteEntity clienteASerAtualizado = buscarClientePorId(id);
        ClienteEntity clienteAtualizado = clienteMapper.toEntity(dto);
        clienteASerAtualizado.merge(clienteAtualizado);
        return clienteMapper.toDTO(clienteRepository.save(clienteASerAtualizado));
    }

    public void deletarCliente(UUID id) {
        buscarClientePorId(id);
        clienteRepository.deleteById(id);
    }

    private ClienteEntity buscarClientePorId(UUID id) {
        return clienteRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));
    }

}
