package com.enriquezavala.socialnetwork.domain.service;

import com.enriquezavala.socialnetwork.domain.exception.ForbiddenOperationException;
import com.enriquezavala.socialnetwork.domain.exception.ResourceNotFoundException;
import com.enriquezavala.socialnetwork.domain.model.Comment;
import com.enriquezavala.socialnetwork.domain.model.Photograph;
import com.enriquezavala.socialnetwork.domain.model.User;
import com.enriquezavala.socialnetwork.domain.repository.CommentRepository;
import com.enriquezavala.socialnetwork.domain.repository.PhotographRepository;
import com.enriquezavala.socialnetwork.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PhotographRepository photographRepository;

    public CommentService(CommentRepository commentRepository, UserRepository userRepository,
                          PhotographRepository photographRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.photographRepository = photographRepository;
    }

    public List<Comment> getAll() {
        return commentRepository.getAll();
    }

    public Optional<Comment> findById(Long id) {
        return commentRepository.findById(id);
    }

    public List<Comment> findByPhotographId(Long photographId) {
        if (photographRepository.findById(photographId).isEmpty()) {
            throw new ResourceNotFoundException(
                    "La fotografía con ID " + photographId + " no existe");
        }
        return commentRepository.findByPhotographId(photographId);
    }

    /**
     * Crea un comentario a nombre del usuario autenticado. El autor se toma del token,
     * nunca del cuerpo de la petición.
     */
    public Comment save(Comment comment, String requesterEmail) {
        if (comment == null) {
            throw new IllegalArgumentException("Los datos del comentario son obligatorios");
        }
        if (comment.getContent() == null || comment.getContent().isBlank()) {
            throw new IllegalArgumentException("El comentario no puede estar vacío");
        }
        if (comment.getPhotograph() == null) {
            throw new IllegalArgumentException("El comentario necesita una foto asociada");
        }
        if (comment.getPhotograph().getId() == null) {
            throw new IllegalArgumentException("La fotografía necesita un ID");
        }
        if (photographRepository.findById(comment.getPhotograph().getId()).isEmpty()) {
            throw new ResourceNotFoundException(
                    "La fotografía con ID " + comment.getPhotograph().getId() + " no existe");
        }
        User author = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("El usuario autenticado no existe"));

        User authorRef = new User();
        authorRef.setId(author.getId());
        Photograph photographRef = new Photograph();
        photographRef.setId(comment.getPhotograph().getId());

        // Un POST siempre crea: se ignora cualquier ID enviado para no sobrescribir otro comentario.
        comment.setId(null);
        comment.setUser(authorRef);
        comment.setPhotograph(photographRef);
        return commentRepository.save(comment);
    }

    /**
     * Puede borrar el comentario su autor o el dueño de la foto comentada.
     */
    public void deleteById(Long id, String requesterEmail) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "El comentario con ID " + id + " no existe"));
        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("El usuario autenticado no existe"));

        boolean isAuthor = comment.getUser() != null
                && requester.getId().equals(comment.getUser().getId());
        boolean isPhotoOwner = comment.getPhotograph() != null
                && photographRepository.findById(comment.getPhotograph().getId())
                        .map(Photograph::getUser)
                        .map(owner -> requester.getId().equals(owner.getId()))
                        .orElse(false);

        if (!isAuthor && !isPhotoOwner) {
            throw new ForbiddenOperationException(
                    "Solo el autor o el dueño de la foto pueden borrar este comentario");
        }
        commentRepository.deleteById(id);
    }
}
