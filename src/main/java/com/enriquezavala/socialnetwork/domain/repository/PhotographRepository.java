package com.enriquezavala.socialnetwork.domain.repository;

import com.enriquezavala.socialnetwork.domain.Photograph;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhotographRepository {

    List<Photograph> getAll();
    Optional<Photograph> findById(Long id);
    Photograph save(Photograph photograph);
    void deleteById(Long id);
    List<Photograph> findByUserId(Long userId);
}
