package com.faisal.portfolio.skill;

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

@WebMvcTest(SkillController.class)
@Import(GlobalExceptionHandler.class)
class SkillControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SkillService skillService;

    @Test
    void getAllSkills_returnsCategoriesWithSkills() throws Exception {
        List<SkillCategoryDto> categories = List.of(
                new SkillCategoryDto(1L, "Backend", List.of(
                        new SkillCategoryDto.SkillDto(1L, "Java", 85, null),
                        new SkillCategoryDto.SkillDto(2L, "Spring Boot", 80, null)
                )),
                new SkillCategoryDto(2L, "Frontend", List.of(
                        new SkillCategoryDto.SkillDto(3L, "React", 75, null)
                ))
        );
        when(skillService.getAllSkillsGrouped()).thenReturn(categories);

        mockMvc.perform(get("/api/v1/skills"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].name").value("Backend"))
                .andExpect(jsonPath("$.data[0].skills", hasSize(2)))
                .andExpect(jsonPath("$.data[0].skills[0].name").value("Java"))
                .andExpect(jsonPath("$.data[0].skills[0].proficiency").value(85))
                .andExpect(jsonPath("$.data[1].name").value("Frontend"))
                .andExpect(jsonPath("$.data[1].skills", hasSize(1)))
                .andExpect(header().exists("Cache-Control"));
    }

    @Test
    void getAllSkills_emptyList() throws Exception {
        when(skillService.getAllSkillsGrouped()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/skills"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(0)));
    }
}
