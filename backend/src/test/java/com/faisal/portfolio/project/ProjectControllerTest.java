package com.faisal.portfolio.project;

import com.faisal.portfolio.common.GlobalExceptionHandler;
import com.faisal.portfolio.common.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
@Import(GlobalExceptionHandler.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProjectService projectService;

    @Test
    void health_returnsOkWithStatus() throws Exception {
        mockMvc.perform(get("/api/v1/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("ok"))
                .andExpect(header().exists("Cache-Control"));
    }

    @Test
    void getAllProjects_returnsProjectList() throws Exception {
        List<ProjectDto> projects = List.of(
                new ProjectDto(1L, "creams", "CREAMS", "Rehabilitation management", List.of("Laravel", "MySQL"), true, "2024"),
                new ProjectDto(2L, "portfolio", "Portfolio", "Personal site", List.of("Spring Boot", "React"), false, "2025")
        );
        when(projectService.getAllProjects()).thenReturn(projects);

        mockMvc.perform(get("/api/v1/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].slug").value("creams"))
                .andExpect(jsonPath("$.data[1].slug").value("portfolio"))
                .andExpect(header().exists("Cache-Control"));
    }

    @Test
    void getAllProjects_emptyList() throws Exception {
        when(projectService.getAllProjects()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(0)));
    }

    @Test
    void getProject_existingSlug_returnsDetail() throws Exception {
        ProjectDetailDto detail = new ProjectDetailDto(
                1L, "creams", "CREAMS", "Short desc", "Full desc",
                "Problem", "Solution", true, "2024",
                "https://github.com/example", null,
                List.of(new ProjectDetailDto.TechnologyDto(1L, "Laravel", null)),
                List.of(new ProjectDetailDto.ImageDto("/img.png", "Screenshot", 1)),
                List.of("Used RBAC pattern"),
                java.util.Map.of("Backend", List.of("Laravel", "PHP")),
                List.of("Complex domain model"),
                "University-hosted, zero cost"
        );
        when(projectService.getProjectBySlug("creams")).thenReturn(detail);

        mockMvc.perform(get("/api/v1/projects/creams"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.slug").value("creams"))
                .andExpect(jsonPath("$.data.title").value("CREAMS"))
                .andExpect(jsonPath("$.data.architectureDecisions[0]").value("Used RBAC pattern"))
                .andExpect(jsonPath("$.data.techStack.Backend", hasSize(2)))
                .andExpect(jsonPath("$.data.challenges[0]").value("Complex domain model"))
                .andExpect(jsonPath("$.data.costJustification").value("University-hosted, zero cost"))
                .andExpect(header().exists("Cache-Control"));
    }

    @Test
    void getProject_nonExistentSlug_returns404() throws Exception {
        when(projectService.getProjectBySlug("nonexistent"))
                .thenThrow(new ResourceNotFoundException("Project not found: nonexistent"));

        mockMvc.perform(get("/api/v1/projects/nonexistent"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("Project not found: nonexistent"));
    }
}
