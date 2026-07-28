package com.enriquezavala.socialnetwork.domain.service;

import com.enriquezavala.socialnetwork.domain.model.Photograph;
import com.enriquezavala.socialnetwork.domain.repository.PhotographRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PhotographService {

    private final PhotographRepository photographRepository;

    public PhotographService(PhotographRepository photographRepository) {
        this.photographRepository = photographRepository;
    }

    public List<Photograph> getAll() {
        return photographRepository.getAll();
    }

    public Optional<Photograph> findById(Long id) {
        return photographRepository.findById(id);
    }

    public List<Photograph> findByUserId(Long userId) {
        return photographRepository.findByUserId(userId);
    }

    public Photograph save(Photograph photograph) {
        if (photograph.getTitle() == null || photograph.getTitle().isBlank()) {
            throw new IllegalArgumentException("El título no puede estar vacío");
        }
        if (photograph.getImageUrl() == null || photograph.getImageUrl().isBlank()) {
            throw new IllegalArgumentException("La foto necesita una URL de imagen");
        }
        if (photograph.getUser() == null) {
            throw new IllegalArgumentException("La foto necesita un usuario dueño");
        }
        return photographRepository.save(photograph);
    }

    public void deleteById(Long id) {
        photographRepository.deleteById(id);
    }
}