package com.enriquezavala.socialnetwork.domain.repository;


import com.enriquezavala.socialnetwork.domain.model.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository {

    List<User> getAll();
    Optional<User> findById(Long id);
    User save(User user);
    void deleteById(Long id);

}
