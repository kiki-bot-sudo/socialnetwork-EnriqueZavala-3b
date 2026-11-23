package com.enriquezavala.socialnetwork.web.controller;

import com.enriquezavala.socialnetwork.domain.model.Photograph;
import com.enriquezavala.socialnetwork.domain.exception.ResourceNotFoundException;
import com.enriquezavala.socialnetwork.domain.service.PhotographService;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Tag(name = "Photographs", description = "Management of photographs posted by users")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/photographs")
public class PhotographController {

    private final PhotographService photographService;

    public PhotographController(PhotographService photographService) {
        this.photographService = photographService;
    }

    @Operation(summary = "List all photographs")
    @ApiResponse(responseCode = "200", description = "List retrieved successfully")
    @GetMapping
    public ResponseEntity<List<Photograph>> getAll() {
        return ResponseEntity.ok(photographService.getAll());
    }

    @Operation(
            summary = "Post a new photograph uploading the image file",
            description = "multipart/form-data with the image file (JPG, PNG, WEBP or GIF, max 10 MB), "
                    + "title, category and an optional description. The image is stored on the server "
                    + "and served at /uploads/…"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Photograph created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid data or file is not an allowed image", content = @Content),
            @ApiResponse(responseCode = "413", description = "Image larger than 10 MB", content = @Content)
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Photograph> upload(
            @RequestPart("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("category") String category,
            @RequestParam(value = "description", required = false) String description,
            @Parameter(hidden = true) Principal principal) throws IOException {
        Photograph photograph = new Photograph();
        photograph.setTitle(title);
        photograph.setCategory(category);
        photograph.setDescription(description);
        Photograph saved = photographService.saveWithImage(photograph, file.getBytes(), principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @Operation(summary = "List the allowed photograph categories")
    @ApiResponse(responseCode = "200", description = "List retrieved successfully")
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        return ResponseEntity.ok(photographService.getCategories());
    }

    @Operation(summary = "Get a photograph by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Photograph found"),
            @ApiResponse(responseCode = "404", description = "Photograph not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Photograph> getById(
            @Parameter(description = "Photograph ID", example = "1") @PathVariable Long id) {
        Photograph photograph = photographService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "La fotografía con ID " + id + " no existe"));
        return ResponseEntity.ok(photograph);
    }

    @Operation(summary = "List photographs from a user", description = "Returns every photograph posted by a specific user")
    @ApiResponse(responseCode = "200", description = "List retrieved successfully")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Photograph>> getByUserId(
            @Parameter(description = "ID of the owner user", example = "1") @PathVariable Long userId) {
        return ResponseEntity.ok(photographService.findByUserId(userId));
    }

    @Operation(
            summary = "Post a new photograph",
            description = "Creates a photograph associated with an existing user",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = Photograph.class),
                            examples = @ExampleObject(
                                    name = "Sample photograph",
                                    value = """
                                            {
                                              "title": "Sunset at the beach",
                                              "description": "Photo taken in Puerto Vallarta",
                                              "imageUrl": "https://example.com/img/sunset.jpg",
                                              "category": "Atardecer"
                                            }
                                            """
                            )
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Photograph created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid data (blank title/URL or missing user)", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Photograph> save(@RequestBody Photograph photograph,
                                           @Parameter(hidden = true) Principal principal) {
        Photograph saved = photographService.save(photograph, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @Operation(summary = "Delete a photograph by ID")
    @ApiResponse(responseCode = "204", description = "Photograph deleted successfully")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(
            @Parameter(description = "ID of the photograph to delete", example = "1") @PathVariable Long id,
            @Parameter(hidden = true) Principal principal) {
        photographService.deleteById(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
}
