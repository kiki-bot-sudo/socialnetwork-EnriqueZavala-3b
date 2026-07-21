package com.enriquezavala.socialnetwork.Repository;

import com.enriquezavala.socialnetwork.Entity.Fotografia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FotografiaRepository extends JpaRepository<Fotografia, Long> {
}
