package com.enriquezavala.socialnetwork.web.controller;

import com.enriquezavala.socialnetwork.domain.model.Comment;
import com.enriquezavala.socialnetwork.domain.exception.ResourceNotFoundException;
import com.enriquezavala.socialnetwork.domain.service.CommentService;
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

import java.security.Principal;
import java.util.List;

@Tag(name = "Comments", description = "Management of comments on photographs")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @Operation(summary = "List all comments")
    @ApiResponse(responseCode = "200", description = "List retrieved successfully")
    @GetMapping
    public ResponseEntity<List<Comment>> getAll() {
        return ResponseEntity.ok(commentService.getAll());
    }

    @Operation(summary = "Get a comment by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comment found"),
            @ApiResponse(responseCode = "404", description = "Comment not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Comment> getById(
            @Parameter(description = "Comment ID", example = "1") @PathVariable Long id) {
        Comment comment = commentService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "El comentario con ID " + id + " no existe"));
        return ResponseEntity.ok(comment);
    }

    @Operation(summary = "List comments on a photograph")
    @ApiResponse(responseCode = "200", description = "List retrieved successfully")
    @GetMapping("/photograph/{photographId}")
    public ResponseEntity<List<Comment>> getByPhotographId(
            @Parameter(description = "Photograph ID", example = "1") @PathVariable Long photographId) {
        return ResponseEntity.ok(commentService.findByPhotographId(photographId));
    }

    @Operation(
            summary = "Create a comment",
            description = "Adds a comment from a user to an existing photograph",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = Comment.class),
                            examples = @ExampleObject(
                                    name = "Sample comment",
                                    value = """
                                            {
                                              "content": "Great shot, looks amazing!",
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
            @ApiResponse(responseCode = "201", description = "Comment created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid data (blank content, missing user or photograph)", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Comment> save(@RequestBody Comment comment,
                                        @Parameter(hidden = true) Principal principal) {
        Comment saved = commentService.save(comment, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @Operation(summary = "Delete a comment by ID")
    @ApiResponse(responseCode = "204", description = "Comment deleted successfully")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(
            @Parameter(description = "ID of the comment to delete", example = "1") @PathVariable Long id,
            @Parameter(hidden = true) Principal principal) {
        commentService.deleteById(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
}
