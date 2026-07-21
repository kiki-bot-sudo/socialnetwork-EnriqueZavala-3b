package com.enriquezavala.socialnetwork.Repository;

import com.enriquezavala.socialnetwork.Entity.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
}
