# Spring Boot TDD Workflow

Target 80%+ coverage (JaCoCo). Write tests first — always.

## Red-Green-Refactor

```bash
# Step 1: Write failing test
# Step 2: ./mvnw test -Dtest=TestClassName  (confirm RED)
# Step 3: Implement minimal code
# Step 4: ./mvnw test -Dtest=TestClassName  (confirm GREEN)
# Step 5: Refactor, run full suite: ./mvnw test
```

## Unit Test (Service Layer)

```java
@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectService projectService;

    @Test
    void create_WithValidRequest_ReturnsProjectResponse() {
        // Arrange
        CreateProjectRequest request = new CreateProjectRequest(
            "RinggitSense", "Financial tracking app", List.of("Python", "FastAPI", "React"));
        Project savedProject = Project.builder().id(1L).title("RinggitSense").build();

        given(projectRepository.save(any(Project.class))).willReturn(savedProject);

        // Act
        ProjectResponse response = projectService.create(request);

        // Assert
        assertThat(response.getTitle()).isEqualTo("RinggitSense");
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void findById_WhenNotFound_ThrowsEntityNotFoundException() {
        given(projectRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.findById(99L))
            .isInstanceOf(EntityNotFoundException.class);
    }
}
```

## Web Layer Test (Controller)

```java
@WebMvcTest(ProjectController.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    @Test
    void createProject_WithValidBody_Returns201() throws Exception {
        ProjectResponse response = new ProjectResponse(1L, "RinggitSense", "Finance app");
        given(projectService.create(any())).willReturn(response);

        mockMvc.perform(post("/api/v1/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "title": "RinggitSense",
                        "description": "Finance app",
                        "techStack": ["Python", "FastAPI"]
                    }
                """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title").value("RinggitSense"))
            .andExpect(header().string("Location", containsString("/api/v1/projects/1")));
    }

    @Test
    void createProject_WithMissingTitle_Returns422() throws Exception {
        mockMvc.perform(post("/api/v1/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\": \"no title\"}"))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.code").value("validation_error"));
    }
}
```

## Integration Test (with Testcontainers)

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class ProjectIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("portfolio_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void fullProjectLifecycle_CreateAndRetrieve() {
        CreateProjectRequest request = new CreateProjectRequest(
            "CREAMS", "Rehabilitation management", List.of("Laravel", "PHP"));

        ResponseEntity<ProjectResponse> createResponse = restTemplate
                .postForEntity("/api/v1/projects", request, ProjectResponse.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        Long id = createResponse.getBody().getId();
        ResponseEntity<ProjectResponse> getResponse = restTemplate
                .getForEntity("/api/v1/projects/" + id, ProjectResponse.class);

        assertThat(getResponse.getBody().getTitle()).isEqualTo("CREAMS");
    }
}
```

## Coverage with JaCoCo

```xml
<!-- pom.xml -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <configuration>
        <rules>
            <rule>
                <limits>
                    <limit>
                        <counter>LINE</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.80</minimum>
                    </limit>
                </limits>
            </rule>
        </rules>
    </configuration>
</plugin>
```

```bash
# Run with coverage
./mvnw verify

# Fast parallel execution
./mvnw -T 4 test

# Specific test
./mvnw test -Dtest=ProjectServiceTest
```
