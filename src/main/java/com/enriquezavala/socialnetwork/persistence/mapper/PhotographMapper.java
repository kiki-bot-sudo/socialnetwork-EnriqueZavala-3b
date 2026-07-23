package com.enriquezavala.socialnetwork.persistence.mapper;

import com.enriquezavala.socialnetwork.domain.Photograph;
import com.enriquezavala.socialnetwork.persistence.Entity.Fotografia;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface PhotographMapper {

   @Mappings({
           @Mapping(source = "titulo", target = "title"),
           @Mapping(source = "descripcion", target = "description"),
           @Mapping(source = "urlImagen", target = "imageUrl"),
           @Mapping(source = "usuario", target = "user"),
})
   Photograph toPhotograph(Fotografia fotografia);
}
