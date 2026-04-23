package br.com.gestaocore_grupo6_api.modules.venda.mapper;

import br.com.gestaocore_grupo6_api.modules.venda.dto.CriarItemVendaRequestDTO;
import br.com.gestaocore_grupo6_api.modules.venda.dto.ItemVendaDTO;
import br.com.gestaocore_grupo6_api.modules.venda.entity.ItemVendaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ItemVendaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "produto.id", source = "produtoId")
    @Mapping(target = "venda", ignore = true)
    ItemVendaEntity toEntity(CriarItemVendaRequestDTO dto);

    @Mapping(target = "produtoId", source = "produto.id")
    ItemVendaDTO toDTO(ItemVendaEntity itemVendaEntity);
}
