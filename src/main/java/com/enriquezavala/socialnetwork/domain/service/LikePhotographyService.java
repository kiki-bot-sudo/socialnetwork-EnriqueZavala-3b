package com.enriquezavala.socialnetwork.domain.service;

import com.enriquezavala.socialnetwork.domain.exception.ForbiddenOperationException;
import com.enriquezavala.socialnetwork.domain.exception.ResourceNotFoundException;
import com.enriquezavala.socialnetwork.domain.model.LikePhotography;
import com.enriquezavala.socialnetwork.domain.model.Photograph;
import com.enriquezavala.socialnetwork.domain.model.User;
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

    /**
     * Registra un like del usuario autenticado. El usuario se toma del token,
     * nunca del cuerpo de la petición.
     */
    public LikePhotography save(LikePhotography like, String requesterEmail) {
        if (like == null) {
            throw new IllegalArgumentException("Los datos del like son obligatorios");
        }
        if (like.getPhotograph() == null || like.getPhotograph().getId() == null) {
            throw new IllegalArgumentException("El like necesita una fotografía con ID");
        }
        if (photographRepository.findById(like.getPhotograph().getId()).isEmpty()) {
            throw new ResourceNotFoundException(
                    "La fotografía con ID " + like.getPhotograph().getId() + " no existe");
        }
        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("El usuario autenticado no existe"));

        boolean yaExiste = likePhotographyRepository.existsByUserIdAndPhotographId(
                requester.getId(), like.getPhotograph().getId()
        );
        if (yaExiste) {
            throw new IllegalStateException("El usuario ya dio like a esta foto");
        }

        User userRef = new User();
        userRef.setId(requester.getId());
        Photograph photographRef = new Photograph();
        photographRef.setId(like.getPhotograph().getId());

        // Un POST siempre crea: se ignora cualquier ID enviado para no sobrescribir otro like.
        like.setId(null);
        like.setUser(userRef);
        like.setPhotograph(photographRef);
        return likePhotographyRepository.save(like);
    }

    public void deleteById(Long id, String requesterEmail) {
        LikePhotography like = likePhotographyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "El like con ID " + id + " no existe"));
        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("El usuario autenticado no existe"));

        if (like.getUser() == null || !requester.getId().equals(like.getUser().getId())) {
            throw new ForbiddenOperationException("Solo puedes quitar tus propios likes");
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
