package com.enriquezavala.socialnetwork.persistence.mapper;

import com.enriquezavala.socialnetwork.domain.model.LikePhotography;
import com.enriquezavala.socialnetwork.persistence.Entity.LikeFotografia;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = { UserMapper.class, PhotographMapper.class })
public interface LikePhotographyMapper {

    @Mapping(source = "usuario", target = "user")
    @Mapping(source = "fotografia", target = "photograph")
    LikePhotography toLikePhotography(LikeFotografia likeFotografia);
    List<LikePhotography> toLikePhotographies(List<LikeFotografia> likeFotografias);


    @InheritInverseConfiguration
    LikeFotografia toLikeFotografia(LikePhotography likePhotography);

}