package com.enriquezavala.socialnetwork.persistence.crud;

import com.enriquezavala.socialnetwork.persistence.Entity.Fotografia;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FotografiaCrudRepository extends CrudRepository<Fotografia, Long> {

    List<Fotografia> findByUsuarioId(Long usuarioId);
}
