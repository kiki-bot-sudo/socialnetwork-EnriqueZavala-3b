package com.enriquezavala.socialnetwork.persistence.crud;

import com.enriquezavala.socialnetwork.persistence.Entity.Comentario;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ComentarioCrudRepository extends CrudRepository<Comentario, Long> {
    List<Comentario> findByFotografiaId(Long fotografiaId);
}
