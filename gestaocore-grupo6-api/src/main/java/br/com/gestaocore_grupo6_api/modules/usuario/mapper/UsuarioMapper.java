package br.com.gestaocore_grupo6_api.modules.usuario.mapper;

import br.com.gestaocore_grupo6_api.modules.usuario.dto.AtualizarUsuarioRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import br.com.gestaocore_grupo6_api.modules.usuario.dto.UsuarioDTO;
import br.com.gestaocore_grupo6_api.modules.usuario.dto.CriarUsuarioRequestDTO;
import br.com.gestaocore_grupo6_api.modules.usuario.entity.UsuarioEntity;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    @Mapping(target = "id", ignore = true)
    UsuarioEntity toEntity(CriarUsuarioRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    UsuarioEntity toEntity(AtualizarUsuarioRequestDTO dto);

    @Mapping(target = "role", expression = "java(usuarioEntity.getRole().name())")
    UsuarioDTO toDTO(UsuarioEntity usuarioEntity);
}
