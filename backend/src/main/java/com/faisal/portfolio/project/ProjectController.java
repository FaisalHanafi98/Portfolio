package com.faisal.portfolio.project;

import com.faisal.portfolio.common.ApiResponse;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1")
public class ProjectController {

    private static final CacheControl CACHE = CacheControl.maxAge(1, TimeUnit.HOURS)
            .staleWhileRevalidate(1, TimeUnit.DAYS);

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, String>>> health() {
        return ResponseEntity.ok()
                .cacheControl(CACHE)
                .body(ApiResponse.success(Map.of("status", "ok")));
    }

    @GetMapping("/projects")
    public ResponseEntity<ApiResponse<List<ProjectDto>>> getAllProjects() {
        return ResponseEntity.ok()
                .cacheControl(CACHE)
                .body(ApiResponse.success(projectService.getAllProjects()));
    }

    @GetMapping("/projects/{slug}")
    public ResponseEntity<ApiResponse<ProjectDetailDto>> getProject(@PathVariable String slug) {
        return ResponseEntity.ok()
                .cacheControl(CACHE)
                .body(ApiResponse.success(projectService.getProjectBySlug(slug)));
    }
}
