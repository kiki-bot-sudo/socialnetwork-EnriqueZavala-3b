package com.enriquezavala.socialnetwork.domain.repository;

import com.enriquezavala.socialnetwork.domain.LikePhotography;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikePhotographyRepository {

    List<LikePhotography> getAll();
    LikePhotography save(LikePhotography like);
    void deleteById(Long id);
    List<LikePhotography> findByPhotographId(Long photographId);
    boolean existsByUserIdAndPhotographId(Long userId, Long photographId);
    long countByPhotographId(Long photographId);
}
