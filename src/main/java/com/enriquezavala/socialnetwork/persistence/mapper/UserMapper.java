package com.enriquezavala.socialnetwork.persistence.mapper;


import com.enriquezavala.socialnetwork.domain.User;
import com.enriquezavala.socialnetwork.persistence.Entity.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mappings({
            @Mapping(source = "nombre", target = "name"),
            @Mapping(source = "biografia", target = "biography"),
            @Mapping(source = "fotoPerfil", target = "profilePicture"),
            @Mapping(target = "comments", ignore = true),
            @Mapping(target = "photographs", ignore = true),
            @Mapping(target = "likes", ignore = true)
    })
    User toUser(Usuario usuario);
}
