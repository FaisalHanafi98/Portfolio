package com.faisal.portfolio.experience;

import com.faisal.portfolio.common.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExperienceController.class)
@Import(GlobalExceptionHandler.class)
class ExperienceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExperienceService experienceService;

    @Test
    void getAllExperiences_returnsList() throws Exception {
        List<ExperienceDto> experiences = List.of(
                new ExperienceDto(1L, "TechCorp", "Software Engineer", "Kuala Lumpur",
                        "2024-01-01", null, "Full-stack development",
                        List.of("Built REST APIs", "Mentored juniors")),
                new ExperienceDto(2L, "StartupXYZ", "Intern", "Remote",
                        "2023-06-01", "2023-12-31", "Frontend development",
                        List.of("React components"))
        );
        when(experienceService.getAllExperiences()).thenReturn(experiences);

        mockMvc.perform(get("/api/v1/experience"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].company").value("TechCorp"))
                .andExpect(jsonPath("$.data[0].role").value("Software Engineer"))
                .andExpect(jsonPath("$.data[0].highlights", hasSize(2)))
                .andExpect(jsonPath("$.data[1].company").value("StartupXYZ"))
                .andExpect(jsonPath("$.data[1].endDate").value("2023-12-31"))
                .andExpect(header().exists("Cache-Control"));
    }

    @Test
    void getAllExperiences_emptyList() throws Exception {
        when(experienceService.getAllExperiences()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/experience"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(0)));
    }
}
