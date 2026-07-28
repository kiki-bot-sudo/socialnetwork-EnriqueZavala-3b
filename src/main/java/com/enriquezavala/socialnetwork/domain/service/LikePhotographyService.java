package com.enriquezavala.socialnetwork.domain.service;

import com.enriquezavala.socialnetwork.domain.model.LikePhotography;
import com.enriquezavala.socialnetwork.domain.repository.LikePhotographyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LikePhotographyService {

    private final LikePhotographyRepository likePhotographyRepository;

    public LikePhotographyService(LikePhotographyRepository likePhotographyRepository) {
        this.likePhotographyRepository = likePhotographyRepository;
    }

    public List<LikePhotography> getAll() {
        return likePhotographyRepository.getAll();
    }

    public List<LikePhotography> findByPhotographId(Long photographId) {
        return likePhotographyRepository.findByPhotographId(photographId);
    }

    public long countByPhotographId(Long photographId) {
        return likePhotographyRepository.countByPhotographId(photographId);
    }

    public LikePhotography save(LikePhotography like) {
        boolean yaExiste = likePhotographyRepository.existsByUserIdAndPhotographId(
                like.getUser().getId(), like.getPhotograph().getId()
        );
        if (yaExiste) {
            throw new IllegalStateException("El usuario ya dio like a esta foto");
        }
        return likePhotographyRepository.save(like);
    }

    public void deleteById(Long id) {
        likePhotographyRepository.deleteById(id);
    }
}