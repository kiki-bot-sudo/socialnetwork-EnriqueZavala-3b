package com.enriquezavala.socialnetwork.persistence.mapper;

import com.enriquezavala.socialnetwork.domain.Comment;
import com.enriquezavala.socialnetwork.persistence.Entity.Comentario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
        componentModel = "spring",
        uses = { UserMapper.class, PhotographMapper.class }
)
public interface CommentMapper {

    @Mappings({
            @Mapping(source = "contenido", target = "content"),
            @Mapping(source = "usuario", target = "user"),
            @Mapping(source = "fotografia", target = "photograph")
    })
    Comment toComment(Comentario comentario);
}