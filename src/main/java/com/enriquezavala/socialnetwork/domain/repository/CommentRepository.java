package com.enriquezavala.socialnetwork.domain.repository;

import com.enriquezavala.socialnetwork.domain.Comment;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository {

    List<Comment> getAll();
    Optional<Comment> findById(Long id);
    Comment save(Comment comment);
    void deleteById(Long id);
    List<Comment> findByPhotographId(Long photographId);

}
