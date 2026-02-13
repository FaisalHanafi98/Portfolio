package com.faisal.portfolio.experience;

import com.faisal.portfolio.common.ApiResponse;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/experience")
public class ExperienceController {

    private static final CacheControl CACHE = CacheControl.maxAge(1, TimeUnit.HOURS)
            .staleWhileRevalidate(1, TimeUnit.DAYS);

    private final ExperienceService experienceService;

    public ExperienceController(ExperienceService experienceService) {
        this.experienceService = experienceService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ExperienceDto>>> getAllExperiences() {
        return ResponseEntity.ok()
                .cacheControl(CACHE)
                .body(ApiResponse.success(experienceService.getAllExperiences()));
    }
}
