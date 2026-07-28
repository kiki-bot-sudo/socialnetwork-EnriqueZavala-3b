package com.enriquezavala.socialnetwork.persistence;

import com.enriquezavala.socialnetwork.domain.model.Comment;
import com.enriquezavala.socialnetwork.domain.repository.CommentRepository;
import com.enriquezavala.socialnetwork.persistence.Entity.Comentario;
import com.enriquezavala.socialnetwork.persistence.crud.ComentarioCrudRepository;
import com.enriquezavala.socialnetwork.persistence.mapper.CommentMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ComentarioRepository implements CommentRepository {

    private final ComentarioCrudRepository comentarioCrudRepository;
    private final CommentMapper commentMapper;

    public ComentarioRepository(ComentarioCrudRepository comentarioCrudRepository, CommentMapper commentMapper) {
        this.comentarioCrudRepository = comentarioCrudRepository;
        this.commentMapper = commentMapper;
    }

    @Override
    public List<Comment> getAll() {
        List<Comentario> comentarios = (List<Comentario>) comentarioCrudRepository.findAll();
        return commentMapper.toComments(comentarios);
    }

    @Override
    public Optional<Comment> findById(Long id) {
        return comentarioCrudRepository.findById(id)
                .map(commentMapper::toComment);
    }

    @Override
    public Comment save(Comment comment) {
        Comentario comentario = commentMapper.toComentario(comment);
        Comentario guardado = comentarioCrudRepository.save(comentario);
        return commentMapper.toComment(guardado);
    }

    @Override
    public void deleteById(Long id) {
        comentarioCrudRepository.deleteById(id);
    }

    @Override
    public List<Comment> findByPhotographId(Long photographId) {
        List<Comentario> comentarios = comentarioCrudRepository.findByFotografiaId(photographId);
        return commentMapper.toComments(comentarios);
    }
}