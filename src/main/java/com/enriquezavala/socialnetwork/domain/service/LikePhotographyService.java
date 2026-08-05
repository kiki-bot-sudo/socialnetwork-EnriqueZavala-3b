package com.enriquezavala.socialnetwork.domain.service;

import com.enriquezavala.socialnetwork.domain.exception.ResourceNotFoundException;
import com.enriquezavala.socialnetwork.domain.model.LikePhotography;
import com.enriquezavala.socialnetwork.domain.repository.LikePhotographyRepository;
import com.enriquezavala.socialnetwork.domain.repository.PhotographRepository;
import com.enriquezavala.socialnetwork.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LikePhotographyService {

    private final LikePhotographyRepository likePhotographyRepository;
    private final UserRepository userRepository;
    private final PhotographRepository photographRepository;

    public LikePhotographyService(LikePhotographyRepository likePhotographyRepository,
                                  UserRepository userRepository,
                                  PhotographRepository photographRepository) {
        this.likePhotographyRepository = likePhotographyRepository;
        this.userRepository = userRepository;
        this.photographRepository = photographRepository;
    }

    public List<LikePhotography> getAll() {
        return likePhotographyRepository.getAll();
    }

    public List<LikePhotography> findByPhotographId(Long photographId) {
        validatePhotographExists(photographId);
        return likePhotographyRepository.findByPhotographId(photographId);
    }

    public long countByPhotographId(Long photographId) {
        validatePhotographExists(photographId);
        return likePhotographyRepository.countByPhotographId(photographId);
    }

    public LikePhotography save(LikePhotography like) {
        if (like == null) {
            throw new IllegalArgumentException("Los datos del like son obligatorios");
        }
        if (like.getUser() == null || like.getUser().getId() == null) {
            throw new IllegalArgumentException("El like necesita un usuario con ID");
        }
        if (like.getPhotograph() == null || like.getPhotograph().getId() == null) {
            throw new IllegalArgumentException("El like necesita una fotografía con ID");
        }
        if (userRepository.findById(like.getUser().getId()).isEmpty()) {
            throw new ResourceNotFoundException(
                    "El usuario con ID " + like.getUser().getId() + " no existe");
        }
        if (photographRepository.findById(like.getPhotograph().getId()).isEmpty()) {
            throw new ResourceNotFoundException(
                    "La fotografía con ID " + like.getPhotograph().getId() + " no existe");
        }
        boolean yaExiste = likePhotographyRepository.existsByUserIdAndPhotographId(
                like.getUser().getId(), like.getPhotograph().getId()
        );
        if (yaExiste) {
            throw new IllegalStateException("El usuario ya dio like a esta foto");
        }
        return likePhotographyRepository.save(like);
    }

    public void deleteById(Long id) {
        if (likePhotographyRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("El like con ID " + id + " no existe");
        }
        likePhotographyRepository.deleteById(id);
    }

    private void validatePhotographExists(Long photographId) {
        if (photographRepository.findById(photographId).isEmpty()) {
            throw new ResourceNotFoundException(
                    "La fotografía con ID " + photographId + " no existe");
        }
    }
}
