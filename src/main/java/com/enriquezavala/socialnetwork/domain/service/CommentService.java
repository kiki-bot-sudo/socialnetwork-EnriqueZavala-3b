package com.enriquezavala.socialnetwork.domain.service;

import com.enriquezavala.socialnetwork.domain.exception.ResourceNotFoundException;
import com.enriquezavala.socialnetwork.domain.model.Comment;
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
        return commentRepository.findByPhotographId(photographId);
    }

    public Comment save(Comment comment) {
        if (comment.getContent() == null || comment.getContent().isBlank()) {
            throw new IllegalArgumentException("El comentario no puede estar vacío");
        }
        if (comment.getUser() == null) {
            throw new IllegalArgumentException("El comentario necesita un autor");
        }
        if (comment.getPhotograph() == null) {
            throw new IllegalArgumentException("El comentario necesita una foto asociada");
        }
        if (comment.getUser().getId() == null) {
            throw new IllegalArgumentException("El autor necesita un ID");
        }
        if (comment.getPhotograph().getId() == null) {
            throw new IllegalArgumentException("La fotografía necesita un ID");
        }
        if (userRepository.findById(comment.getUser().getId()).isEmpty()) {
            throw new ResourceNotFoundException(
                    "El usuario con ID " + comment.getUser().getId() + " no existe");
        }
        if (photographRepository.findById(comment.getPhotograph().getId()).isEmpty()) {
            throw new ResourceNotFoundException(
                    "La fotografía con ID " + comment.getPhotograph().getId() + " no existe");
        }
        return commentRepository.save(comment);
    }

    public void deleteById(Long id) {
        commentRepository.deleteById(id);
    }
}
