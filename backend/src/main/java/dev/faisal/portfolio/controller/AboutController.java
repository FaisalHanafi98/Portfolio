package dev.faisal.portfolio.controller;

import dev.faisal.portfolio.dto.AboutDTO;
import dev.faisal.portfolio.dto.ApiResponse;
import dev.faisal.portfolio.service.AboutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/about")
@RequiredArgsConstructor
@Tag(name = "About", description = "Personal information endpoints")
public class AboutController {

    private final AboutService aboutService;

    @GetMapping
    @Operation(summary = "Get about information", description = "Returns personal bio and contact information")
    public ResponseEntity<ApiResponse<AboutDTO>> getAbout() {
        AboutDTO about = aboutService.getAboutInfo();
        return ResponseEntity.ok(ApiResponse.success(about));
    }
}
