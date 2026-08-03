package com.enriquezavala.socialnetwork.web.controller;

import com.enriquezavala.socialnetwork.domain.model.LikePhotography;
import com.enriquezavala.socialnetwork.domain.service.LikePhotographyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Likes", description = "Management of likes on photographs")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/likes")
public class LikePhotographyController {

    private final LikePhotographyService likePhotographyService;

    public LikePhotographyController(LikePhotographyService likePhotographyService) {
        this.likePhotographyService = likePhotographyService;
    }

    @Operation(summary = "List all likes")
    @ApiResponse(responseCode = "200", description = "List retrieved successfully")
    @GetMapping
    public ResponseEntity<List<LikePhotography>> getAll() {
        return ResponseEntity.ok(likePhotographyService.getAll());
    }

    @Operation(summary = "List likes on a photograph")
    @ApiResponse(responseCode = "200", description = "List retrieved successfully")
    @GetMapping("/photograph/{photographId}")
    public ResponseEntity<List<LikePhotography>> getByPhotographId(
            @Parameter(description = "Photograph ID", example = "1") @PathVariable Long photographId) {
        return ResponseEntity.ok(likePhotographyService.findByPhotographId(photographId));
    }

    @Operation(summary = "Count likes on a photograph", description = "Returns the total number of likes a photograph has")
    @ApiResponse(responseCode = "200", description = "Count retrieved successfully")
    @GetMapping("/photograph/{photographId}/count")
    public ResponseEntity<Long> countByPhotographId(
            @Parameter(description = "Photograph ID", example = "1") @PathVariable Long photographId) {
        return ResponseEntity.ok(likePhotographyService.countByPhotographId(photographId));
    }

    @Operation(
            summary = "Like a photograph",
            description = "Registers a like from a user on a photograph. Duplicate likes from the same user on the same photograph are not allowed",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = LikePhotography.class),
                            examples = @ExampleObject(
                                    name = "Sample like",
                                    value = """
                                            {
                                              "user": {
                                                "id": 2
                                              },
                                              "photograph": {
                                                "id": 1
                                              }
                                            }
                                            """
                            )
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Like registered successfully"),
            @ApiResponse(responseCode = "409", description = "The user already liked this photograph", content = @Content)
    })
    @PostMapping
    public ResponseEntity<LikePhotography> save(@RequestBody LikePhotography like) {
        LikePhotography saved = likePhotographyService.save(like);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @Operation(summary = "Remove a like by ID")
    @ApiResponse(responseCode = "204", description = "Like deleted successfully")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(
            @Parameter(description = "ID of the like to delete", example = "1") @PathVariable Long id) {
        likePhotographyService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
