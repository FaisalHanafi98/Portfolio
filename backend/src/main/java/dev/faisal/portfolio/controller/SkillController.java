package dev.faisal.portfolio.controller;

import dev.faisal.portfolio.dto.ApiResponse;
import dev.faisal.portfolio.dto.SkillCategoryDTO;
import dev.faisal.portfolio.service.SkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/skills")
@RequiredArgsConstructor
@Tag(name = "Skills", description = "Skills management endpoints")
public class SkillController {

    private final SkillService skillService;

    @GetMapping
    @Operation(summary = "Get all skills", description = "Returns all skills grouped by category")
    public ResponseEntity<ApiResponse<List<SkillCategoryDTO>>> getAllSkills() {
        List<SkillCategoryDTO> skills = skillService.getAllSkillsGroupedByCategory();
        return ResponseEntity.ok(ApiResponse.success(skills));
    }
}
