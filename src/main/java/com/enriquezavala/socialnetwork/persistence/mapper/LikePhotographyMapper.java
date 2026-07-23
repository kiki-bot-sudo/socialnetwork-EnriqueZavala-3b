package com.enriquezavala.socialnetwork.persistence.mapper;

import com.enriquezavala.socialnetwork.domain.LikePhotography;
import com.enriquezavala.socialnetwork.persistence.Entity.LikeFotografia;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { UserMapper.class, PhotographMapper.class })
public interface LikePhotographyMapper {

    @Mapping(source = "usuario", target = "user")
    @Mapping(source = "fotografia", target = "photograph")
    LikePhotography toLikePhotography(LikeFotografia likeFotografia);
}