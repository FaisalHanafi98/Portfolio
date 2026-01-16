package dev.faisal.portfolio.controller;

import dev.faisal.portfolio.dto.ApiResponse;
import dev.faisal.portfolio.dto.ExperienceDTO;
import dev.faisal.portfolio.service.ExperienceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/experience")
@RequiredArgsConstructor
@Tag(name = "Experience", description = "Work experience endpoints")
public class ExperienceController {

    private final ExperienceService experienceService;

    @GetMapping
    @Operation(summary = "Get all experiences", description = "Returns work experience timeline")
    public ResponseEntity<ApiResponse<List<ExperienceDTO>>> getAllExperiences() {
        List<ExperienceDTO> experiences = experienceService.getAllExperiences();
        return ResponseEntity.ok(ApiResponse.success(experiences));
    }
}
