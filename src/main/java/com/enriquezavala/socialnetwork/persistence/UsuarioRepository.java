package com.enriquezavala.socialnetwork.persistence;

import com.enriquezavala.socialnetwork.domain.User;
import com.enriquezavala.socialnetwork.domain.repository.UserRepository;
import com.enriquezavala.socialnetwork.persistence.Entity.Usuario;
import com.enriquezavala.socialnetwork.persistence.crud.UsuarioCrudRepository;
import com.enriquezavala.socialnetwork.persistence.mapper.UserMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UsuarioRepository implements UserRepository {

    private final UsuarioCrudRepository usuarioCrudRepository;
    private final UserMapper userMapper;

    public UsuarioRepository(UsuarioCrudRepository usuarioCrudRepository, UserMapper userMapper) {
        this.usuarioCrudRepository = usuarioCrudRepository;
        this.userMapper = userMapper;
    }

    @Override
    public List<User> getAll() {
        List<Usuario> usuarios = (List<Usuario>) usuarioCrudRepository.findAll();
        return userMapper.toUsers(usuarios);
    }

    @Override
    public Optional<User> findById(Long id) {
        return usuarioCrudRepository.findById(id)
                .map(userMapper::toUser);
    }

    @Override
    public User save(User user) {
        Usuario usuario = userMapper.toUsuario(user);
        Usuario guardado = usuarioCrudRepository.save(usuario);
        return userMapper.toUser(guardado);
    }

    @Override
    public void deleteById(Long id) {
        usuarioCrudRepository.deleteById(id);
    }
}