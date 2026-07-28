package com.enriquezavala.socialnetwork.persistence;

import com.enriquezavala.socialnetwork.domain.model.LikePhotography;
import com.enriquezavala.socialnetwork.domain.repository.LikePhotographyRepository;
import com.enriquezavala.socialnetwork.persistence.Entity.LikeFotografia;
import com.enriquezavala.socialnetwork.persistence.crud.LikeFotografiaCrudRepository;
import com.enriquezavala.socialnetwork.persistence.mapper.LikePhotographyMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LikeFotografiaRepository implements LikePhotographyRepository {

    private final LikeFotografiaCrudRepository likeFotografiaCrudRepository;
    private final LikePhotographyMapper likePhotographyMapper;

    public LikeFotografiaRepository(LikeFotografiaCrudRepository likeFotografiaCrudRepository, LikePhotographyMapper likePhotographyMapper) {
        this.likeFotografiaCrudRepository = likeFotografiaCrudRepository;
        this.likePhotographyMapper = likePhotographyMapper;
    }

    @Override
    public List<LikePhotography> getAll() {
        List<LikeFotografia> likes = (List<LikeFotografia>) likeFotografiaCrudRepository.findAll();
        return likePhotographyMapper.toLikePhotographies(likes);
    }

    @Override
    public LikePhotography save(LikePhotography like) {
        LikeFotografia likeFotografia = likePhotographyMapper.toLikeFotografia(like);
        LikeFotografia guardado = likeFotografiaCrudRepository.save(likeFotografia);
        return likePhotographyMapper.toLikePhotography(guardado);
    }

    @Override
    public void deleteById(Long id) {
        likeFotografiaCrudRepository.deleteById(id);
    }

    @Override
    public List<LikePhotography> findByPhotographId(Long photographId) {
        List<LikeFotografia> likes = likeFotografiaCrudRepository.findByFotografiaId(photographId);
        return likePhotographyMapper.toLikePhotographies(likes);
    }

    @Override
    public boolean existsByUserIdAndPhotographId(Long userId, Long photographId) {
        return likeFotografiaCrudRepository.existsByUsuarioIdAndFotografiaId(userId, photographId);
    }

    @Override
    public long countByPhotographId(Long photographId) {
        return likeFotografiaCrudRepository.countByFotografiaId(photographId);
    }
}