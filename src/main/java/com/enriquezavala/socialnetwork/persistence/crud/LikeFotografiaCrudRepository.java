package com.enriquezavala.socialnetwork.persistence.crud;

import com.enriquezavala.socialnetwork.persistence.Entity.LikeFotografia;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface LikeFotografiaCrudRepository extends CrudRepository<LikeFotografia, Long> {

    List<LikeFotografia> findByFotografiaId(Long fotografiaId);
    boolean existsByUsuarioIdAndFotografiaId(Long usuarioId, Long fotografiaId);
    long countByFotografiaId(Long fotografiaId);
}
