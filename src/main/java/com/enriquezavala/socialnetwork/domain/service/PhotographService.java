package com.enriquezavala.socialnetwork.domain.service;

import com.enriquezavala.socialnetwork.domain.exception.ResourceNotFoundException;
import com.enriquezavala.socialnetwork.domain.model.Photograph;
import com.enriquezavala.socialnetwork.domain.repository.PhotographRepository;
import com.enriquezavala.socialnetwork.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PhotographService {

    private final PhotographRepository photographRepository;
    private final UserRepository userRepository;

    public PhotographService(PhotographRepository photographRepository, UserRepository userRepository) {
        this.photographRepository = photographRepository;
        this.userRepository = userRepository;
    }

    public List<Photograph> getAll() {
        return photographRepository.getAll();
    }

    public Optional<Photograph> findById(Long id) {
        return photographRepository.findById(id);
    }

    public List<Photograph> findByUserId(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new ResourceNotFoundException(
                    "El usuario con ID " + userId + " no existe");
        }
        return photographRepository.findByUserId(userId);
    }

    public Photograph save(Photograph photograph) {
        if (photograph == null) {
            throw new IllegalArgumentException("Los datos de la fotografía son obligatorios");
        }
        if (photograph.getTitle() == null || photograph.getTitle().isBlank()) {
            throw new IllegalArgumentException("El título no puede estar vacío");
        }
        if (photograph.getImageUrl() == null || photograph.getImageUrl().isBlank()) {
            throw new IllegalArgumentException("La foto necesita una URL de imagen");
        }
        if (photograph.getUser() == null) {
            throw new IllegalArgumentException("La foto necesita un usuario dueño");
        }
        if (photograph.getUser().getId() == null) {
            throw new IllegalArgumentException("El usuario dueño necesita un ID");
        }
        if (userRepository.findById(photograph.getUser().getId()).isEmpty()) {
            throw new ResourceNotFoundException(
                    "El usuario con ID " + photograph.getUser().getId() + " no existe");
        }
        return photographRepository.save(photograph);
    }

    public void deleteById(Long id) {
        if (photographRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException(
                    "La fotografía con ID " + id + " no existe");
        }
        photographRepository.deleteById(id);
    }
}
