package com.enriquezavala.socialnetwork.persistence.crud;

import com.enriquezavala.socialnetwork.persistence.Entity.Comentario;
import org.springframework.data.repository.CrudRepository;

public interface ComentarioCrudRepository extends CrudRepository<Comentario, Long> {
}
