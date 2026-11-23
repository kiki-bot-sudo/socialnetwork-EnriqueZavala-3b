package com.enriquezavala.socialnetwork.domain.service;

import com.enriquezavala.socialnetwork.domain.exception.ForbiddenOperationException;
import com.enriquezavala.socialnetwork.domain.exception.ResourceNotFoundException;
import com.enriquezavala.socialnetwork.domain.model.Photograph;
import com.enriquezavala.socialnetwork.domain.model.User;
import com.enriquezavala.socialnetwork.domain.repository.ImageStorage;
import com.enriquezavala.socialnetwork.domain.repository.PhotographRepository;
import com.enriquezavala.socialnetwork.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PhotographService {

    private final PhotographRepository photographRepository;
    private final UserRepository userRepository;
    private final ImageStorage imageStorage;

    public PhotographService(PhotographRepository photographRepository, UserRepository userRepository,
                             ImageStorage imageStorage) {
        this.photographRepository = photographRepository;
        this.userRepository = userRepository;
        this.imageStorage = imageStorage;
    }

    /**
     * Publica una foto subiendo el archivo de imagen (en lugar de una URL).
     * Primero valida los datos, luego guarda la imagen; si algo falla después,
     * la imagen guardada se borra para no dejar archivos huérfanos.
     */
    public Photograph saveWithImage(Photograph photograph, byte[] image, String requesterEmail) {
        if (photograph == null) {
            throw new IllegalArgumentException("Los datos de la fotografía son obligatorios");
        }
        if (photograph.getTitle() == null || photograph.getTitle().isBlank()) {
            throw new IllegalArgumentException("El título no puede estar vacío");
        }
        if (normalizeCategory(photograph.getCategory()) == null) {
            throw new IllegalArgumentException(
                    "La categoría es obligatoria. Opciones: " + String.join(", ", CATEGORIES));
        }

        String path = imageStorage.store(image);
        photograph.setImageUrl(path);
        try {
            return save(photograph, requesterEmail);
        } catch (RuntimeException ex) {
            imageStorage.deleteIfStored(path);
            throw ex;
        }
    }

    /** Categorías permitidas para las fotografías. */
    public static final List<String> CATEGORIES = List.of(
            "Paisaje", "Retrato", "Urbano", "Naturaleza",
            "Animales", "Arquitectura", "Comida", "Atardecer"
    );

    public List<String> getCategories() {
        return CATEGORIES;
    }

    /** Devuelve la categoría con su escritura oficial, o null si no es válida. */
    private static String normalizeCategory(String category) {
        if (category == null) return null;
        return CATEGORIES.stream()
                .filter(c -> c.equalsIgnoreCase(category.trim()))
                .findFirst()
                .orElse(null);
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

    /**
     * Publica una foto a nombre del usuario autenticado. El dueño se toma del token,
     * nunca del cuerpo de la petición.
     */
    public Photograph save(Photograph photograph, String requesterEmail) {
        if (photograph == null) {
            throw new IllegalArgumentException("Los datos de la fotografía son obligatorios");
        }
        if (photograph.getTitle() == null || photograph.getTitle().isBlank()) {
            throw new IllegalArgumentException("El título no puede estar vacío");
        }
        if (photograph.getImageUrl() == null || photograph.getImageUrl().isBlank()) {
            throw new IllegalArgumentException("La foto necesita una URL de imagen");
        }
        String category = normalizeCategory(photograph.getCategory());
        if (category == null) {
            throw new IllegalArgumentException(
                    "La categoría es obligatoria. Opciones: " + String.join(", ", CATEGORIES));
        }
        photograph.setCategory(category);
        User owner = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("El usuario autenticado no existe"));

        User ownerRef = new User();
        ownerRef.setId(owner.getId());

        // Un POST siempre crea: se ignora cualquier ID enviado para no sobrescribir otra foto.
        photograph.setId(null);
        photograph.setUser(ownerRef);
        photograph.setComments(null);
        photograph.setLikes(null);
        return photographRepository.save(photograph);
    }

    public void deleteById(Long id, String requesterEmail) {
        Photograph photograph = photographRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "La fotografía con ID " + id + " no existe"));
        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("El usuario autenticado no existe"));

        if (photograph.getUser() == null
                || !requester.getId().equals(photograph.getUser().getId())) {
            throw new ForbiddenOperationException("Solo el dueño puede borrar esta fotografía");
        }
        photographRepository.deleteById(id);
        // Si la imagen fue subida a este servidor, también se borra el archivo.
        imageStorage.deleteIfStored(photograph.getImageUrl());
    }
}
