package br.com.gestaocore_grupo6_api.modules.produto.mapper;

import br.com.gestaocore_grupo6_api.modules.produto.dto.AtualizarProdutoRequestDTO;
import br.com.gestaocore_grupo6_api.modules.produto.dto.ProdutoDTO;
import br.com.gestaocore_grupo6_api.modules.produto.dto.CriarProdutoRequestDTO;
import br.com.gestaocore_grupo6_api.modules.produto.entity.ProdutoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProdutoMapper {

    @Mapping(target = "id", ignore = true)
    ProdutoEntity toEntity(CriarProdutoRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    ProdutoEntity toEntity(AtualizarProdutoRequestDTO dto);

    ProdutoDTO toDTO(ProdutoEntity produtoEntity);
}
