package com.faisal.portfolio;

import com.faisal.portfolio.project.Project;
import com.faisal.portfolio.project.ProjectRepository;
import com.faisal.portfolio.seed.DataSeeder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class DefaultDevProfileIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Environment environment;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private ProjectRepository projects;

    @Autowired
    private DataSeeder seeder;

    @Test
    void bootsWithDefaultDevProfileAndServesSeededProjects() throws Exception {
        assertThat(environment.matchesProfiles("dev"))
                .as("The application should resolve the dev profile when no explicit profile is set")
                .isTrue();

        assertThat(environment.getProperty("spring.datasource.url"))
                .as("The dev profile should provide the H2 datasource URL")
                .isEqualTo("jdbc:h2:mem:portfoliodb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");

        try (Connection connection = dataSource.getConnection()) {
            assertThat(connection.getMetaData().getURL())
                    .as("The active datasource should point at the dev H2 database")
                    .contains("jdbc:h2:mem:portfoliodb");
        }

        mockMvc.perform(get("/api/v1/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(greaterThan(0)))
                .andExpect(jsonPath("$.data[0].slug", not(blankOrNullString())));
    }

    @Test
    void listsTheEngineeringTrilogyAsTheOnlyFlagships() throws Exception {
        mockMvc.perform(get("/api/v1/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[?(@.featured == true)].slug",
                        contains("creams", "sen2nal", "baiti")));
    }

    @Test
    @Transactional
    void refreshesExistingFlagshipsWithoutReplacingProjectRows() throws Exception {
        Project creams = projects.findBySlug("creams").orElseThrow();
        Long id = creams.getId();
        int imageCount = creams.getImages().size();
        long projectCount = projects.count();
        Project additional = projects.findBySlug("auto-recruit").orElseThrow();
        additional.setFeatured(true);
        additional.setShortDescription("Previously edited description");
        creams.setArchitectureDecisions(List.of("Outdated architecture"));

        seeder.run();
        seeder.run();

        assertThat(projects.count()).isEqualTo(projectCount);
        assertThat(creams.getId()).isEqualTo(id);
        assertThat(creams.getImages()).hasSize(imageCount);
        assertThat(additional.getShortDescription()).isEqualTo("Previously edited description");
        assertThat(additional.isFeatured()).isFalse();
        mockMvc.perform(get("/api/v1/projects/creams"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.architectureDecisions[0]", containsString("CentreScope")));
    }
}
