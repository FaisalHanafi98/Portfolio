package dev.faisal.portfolio.controller;

import dev.faisal.portfolio.dto.ApiResponse;
import dev.faisal.portfolio.dto.ProjectDetailDTO;
import dev.faisal.portfolio.dto.ProjectSummaryDTO;
import dev.faisal.portfolio.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@Tag(name = "Projects", description = "Project management endpoints")
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    @Operation(summary = "Get all projects", description = "Returns a list of all projects with summary information")
    public ResponseEntity<ApiResponse<List<ProjectSummaryDTO>>> getAllProjects() {
        List<ProjectSummaryDTO> projects = projectService.getAllProjects();
        return ResponseEntity.ok(ApiResponse.success(projects));
    }

    @GetMapping("/{slug}")
    @Operation(summary = "Get project by slug", description = "Returns detailed information about a specific project")
    public ResponseEntity<ApiResponse<ProjectDetailDTO>> getProjectBySlug(
            @Parameter(description = "Project slug", example = "creams")
            @PathVariable String slug) {
        ProjectDetailDTO project = projectService.getProjectBySlug(slug);
        return ResponseEntity.ok(ApiResponse.success(project));
    }
}
