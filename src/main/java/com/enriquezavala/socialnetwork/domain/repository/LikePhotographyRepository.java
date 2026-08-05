package com.enriquezavala.socialnetwork.domain.repository;

import com.enriquezavala.socialnetwork.domain.model.LikePhotography;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikePhotographyRepository {

    List<LikePhotography> getAll();
    Optional<LikePhotography> findById(Long id);
    LikePhotography save(LikePhotography like);
    void deleteById(Long id);
    List<LikePhotography> findByPhotographId(Long photographId);
    boolean existsByUserIdAndPhotographId(Long userId, Long photographId);
    long countByPhotographId(Long photographId);
}
