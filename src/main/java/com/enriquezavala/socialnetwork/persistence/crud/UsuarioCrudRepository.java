package com.enriquezavala.socialnetwork.persistence.crud;

import com.enriquezavala.socialnetwork.persistence.Entity.Usuario;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UsuarioCrudRepository extends CrudRepository<Usuario, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<Usuario> findByEmail(String email);
}
