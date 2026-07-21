package com.enriquezavala.socialnetwork.Repository;

import com.enriquezavala.socialnetwork.Entity.LikeFotografia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeFotografiaRepository extends JpaRepository<LikeFotografia, Long> {
}
