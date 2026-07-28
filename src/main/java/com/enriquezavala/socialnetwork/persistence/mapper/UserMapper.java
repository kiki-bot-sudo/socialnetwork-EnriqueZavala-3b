package com.enriquezavala.socialnetwork.persistence.mapper;


import com.enriquezavala.socialnetwork.domain.model.User;
import com.enriquezavala.socialnetwork.persistence.Entity.Usuario;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mappings({
            @Mapping(source = "nombre", target = "name"),
            @Mapping(source = "biografia", target = "biography"),
            @Mapping(source = "fotoPerfil", target = "profilePicture"),
            @Mapping(target = "comments", ignore = true),
            @Mapping(target = "photographs", ignore = true),
            @Mapping(target = "likes", ignore = true),
    })
    User toUser(Usuario usuario);
    List<User> toUsers(List<Usuario> usuarios);

    @InheritInverseConfiguration
    Usuario toUsuario(User user);
}
