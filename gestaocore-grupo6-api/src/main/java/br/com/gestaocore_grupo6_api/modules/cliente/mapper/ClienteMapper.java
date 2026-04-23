package br.com.gestaocore_grupo6_api.modules.cliente.mapper;

import br.com.gestaocore_grupo6_api.modules.cliente.dto.AtualizarClienteRequestDTO;
import br.com.gestaocore_grupo6_api.modules.cliente.dto.ClienteDTO;
import br.com.gestaocore_grupo6_api.modules.cliente.dto.CriarClienteRequestDTO;
import br.com.gestaocore_grupo6_api.modules.cliente.entity.ClienteEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    @Mapping(target = "id", ignore = true)
    ClienteEntity toEntity(CriarClienteRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    ClienteEntity toEntity(AtualizarClienteRequestDTO dto);

    ClienteDTO toDTO(ClienteEntity clienteEntity);
}
