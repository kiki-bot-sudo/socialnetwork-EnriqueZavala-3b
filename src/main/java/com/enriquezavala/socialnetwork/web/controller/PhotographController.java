package com.enriquezavala.socialnetwork.web.controller;

import com.enriquezavala.socialnetwork.domain.model.Photograph;
import com.enriquezavala.socialnetwork.domain.service.PhotographService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/photographs")
public class PhotographController {

    private final PhotographService photographService;

    public PhotographController(PhotographService photographService) {
        this.photographService = photographService;
    }

    @GetMapping
    public ResponseEntity<List<Photograph>> getAll() {
        return ResponseEntity.ok(photographService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Photograph> getById(@PathVariable Long id) {
        return photographService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Photograph>> getByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(photographService.findByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<Photograph> save(@RequestBody Photograph photograph) {
        Photograph saved = photographService.save(photograph);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        photographService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}