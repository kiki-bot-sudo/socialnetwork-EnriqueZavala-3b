package com.enriquezavala.socialnetwork.persistence;

import com.enriquezavala.socialnetwork.domain.model.Photograph;
import com.enriquezavala.socialnetwork.domain.repository.PhotographRepository;
import com.enriquezavala.socialnetwork.persistence.Entity.Fotografia;
import com.enriquezavala.socialnetwork.persistence.crud.FotografiaCrudRepository;
import com.enriquezavala.socialnetwork.persistence.mapper.PhotographMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class FotografiaRepository implements PhotographRepository {

    private final FotografiaCrudRepository fotografiaCrudRepository;
    private final PhotographMapper photographMapper;

    public FotografiaRepository(FotografiaCrudRepository fotografiaCrudRepository, PhotographMapper photographMapper) {
        this.fotografiaCrudRepository = fotografiaCrudRepository;
        this.photographMapper = photographMapper;
    }

    @Override
    public List<Photograph> getAll() {
        List<Fotografia> fotografias = (List<Fotografia>) fotografiaCrudRepository.findAll();
        return photographMapper.toPhotographs(fotografias);
    }

    @Override
    public Optional<Photograph> findById(Long id) {
        return fotografiaCrudRepository.findById(id)
                .map(photographMapper::toPhotograph);
    }

    @Override
    public Photograph save(Photograph photograph) {
        Fotografia fotografia = photographMapper.toFotografia(photograph);
        Fotografia guardada = fotografiaCrudRepository.save(fotografia);
        return photographMapper.toPhotograph(guardada);
    }

    @Override
    public void deleteById(Long id) {
        fotografiaCrudRepository.deleteById(id);
    }

    @Override
    public List<Photograph> findByUserId(Long userId) {
        List<Fotografia> fotografias = fotografiaCrudRepository.findByUsuarioId(userId);
        return photographMapper.toPhotographs(fotografias);
    }
}