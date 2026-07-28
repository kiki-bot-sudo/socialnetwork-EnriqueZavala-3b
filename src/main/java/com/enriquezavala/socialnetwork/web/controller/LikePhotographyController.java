package com.enriquezavala.socialnetwork.web.controller;

import com.enriquezavala.socialnetwork.domain.model.LikePhotography;
import com.enriquezavala.socialnetwork.domain.service.LikePhotographyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/likes")
public class LikePhotographyController {

    private final LikePhotographyService likePhotographyService;

    public LikePhotographyController(LikePhotographyService likePhotographyService) {
        this.likePhotographyService = likePhotographyService;
    }

    @GetMapping
    public ResponseEntity<List<LikePhotography>> getAll() {
        return ResponseEntity.ok(likePhotographyService.getAll());
    }

    @GetMapping("/photograph/{photographId}")
    public ResponseEntity<List<LikePhotography>> getByPhotographId(@PathVariable Long photographId) {
        return ResponseEntity.ok(likePhotographyService.findByPhotographId(photographId));
    }

    @GetMapping("/photograph/{photographId}/count")
    public ResponseEntity<Long> countByPhotographId(@PathVariable Long photographId) {
        return ResponseEntity.ok(likePhotographyService.countByPhotographId(photographId));
    }

    @PostMapping
    public ResponseEntity<LikePhotography> save(@RequestBody LikePhotography like) {
        LikePhotography saved = likePhotographyService.save(like);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        likePhotographyService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}