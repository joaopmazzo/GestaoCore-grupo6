package br.com.gestaocore_grupo6_api.modules.venda.mapper;

import br.com.gestaocore_grupo6_api.modules.venda.dto.CriarVendaRequestDTO;
import br.com.gestaocore_grupo6_api.modules.venda.dto.VendaDTO;
import br.com.gestaocore_grupo6_api.modules.venda.entity.VendaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ItemVendaMapper.class})
public interface VendaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cliente.id", source = "clienteId")
    @Mapping(target = "itens", ignore = true)
    VendaEntity toEntity(CriarVendaRequestDTO dto);

    @Mapping(target = "clienteId", source = "cliente.id")
    VendaDTO toDTO(VendaEntity vendaEntity);
}
