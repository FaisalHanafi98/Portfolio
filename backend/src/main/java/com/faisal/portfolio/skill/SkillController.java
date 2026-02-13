package com.faisal.portfolio.skill;

import com.faisal.portfolio.common.ApiResponse;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/skills")
public class SkillController {

    private static final CacheControl CACHE = CacheControl.maxAge(1, TimeUnit.HOURS)
            .staleWhileRevalidate(1, TimeUnit.DAYS);

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SkillCategoryDto>>> getAllSkills() {
        return ResponseEntity.ok()
                .cacheControl(CACHE)
                .body(ApiResponse.success(skillService.getAllSkillsGrouped()));
    }
}
