# Spring Boot Development Patterns

## Layered Architecture: Controller → Service → Repository

```java
// Controller — HTTP concerns only, thin
@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    public ResponseEntity<Page<ProjectResponse>> getProjects(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(projectService.findAll(PageRequest.of(page, size)));
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody CreateProjectRequest request) {
        ProjectResponse project = projectService.create(request);
        URI location = URI.create("/api/v1/projects/" + project.getId());
        return ResponseEntity.created(location).body(project);
    }
}

// Service — business logic, transaction boundaries
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectService {

    private final ProjectRepository projectRepository;

    @Transactional
    public ProjectResponse create(CreateProjectRequest request) {
        Project project = Project.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .techStack(request.getTechStack())
                .build();
        return ProjectResponse.from(projectRepository.save(project));
    }

    public Page<ProjectResponse> findAll(Pageable pageable) {
        return projectRepository.findAll(pageable).map(ProjectResponse::from);
    }
}

// Repository — data access
public interface ProjectRepository extends JpaRepository<Project, Long> {
    Page<Project> findByStatus(ProjectStatus status, Pageable pageable);
    List<Project> findByTechStackContaining(String tech);
}
```

## Constructor Injection (always — never field injection)

```java
// GOOD: Constructor injection — explicit, testable
@Service
@RequiredArgsConstructor  // Lombok generates constructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final CacheManager cacheManager;
}

// BAD: Field injection — hides dependencies, harder to test
@Service
public class ProjectService {
    @Autowired
    private ProjectRepository projectRepository;
}
```

## Input Validation

```java
// DTO with Bean Validation
public record CreateProjectRequest(
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must be under 200 characters")
    String title,

    @NotBlank String description,

    @NotEmpty List<String> techStack
) {}

// Controller: @Valid triggers validation, @ControllerAdvice handles errors
@PostMapping
public ResponseEntity<ProjectResponse> create(@Valid @RequestBody CreateProjectRequest request) { ... }
```

## Exception Handling

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<FieldError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> new FieldError(e.getField(), e.getDefaultMessage()))
                .toList();
        return ResponseEntity.unprocessableEntity()
                .body(new ErrorResponse("validation_error", "Validation failed", errors));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity.notFound().build();
    }
}
```

## Caching

```java
@Service
public class ProjectService {

    @Cacheable(value = "projects", key = "#id")
    public ProjectResponse findById(Long id) {
        return projectRepository.findById(id)
                .map(ProjectResponse::from)
                .orElseThrow(EntityNotFoundException::new);
    }

    @CacheEvict(value = "projects", key = "#id")
    @Transactional
    public ProjectResponse update(Long id, UpdateProjectRequest request) { ... }
}
```

## X-Forwarded-For Warning (Proxy Setup)

```yaml
# application.properties — required when behind reverse proxy
server:
  forward-headers-strategy: framework
```

```java
// Register ForwardedHeaderFilter if using FRAMEWORK strategy
@Bean
public FilterRegistrationBean<ForwardedHeaderFilter> forwardedHeaderFilter() {
    FilterRegistrationBean<ForwardedHeaderFilter> bean = new FilterRegistrationBean<>();
    bean.setFilter(new ForwardedHeaderFilter());
    return bean;
}
```

**Do NOT read `X-Forwarded-For` directly — it's trivially spoofable without trusted proxy handling.**

## Async Operations

```java
@Service
public class EmailService {

    @Async
    public CompletableFuture<Void> sendProjectUpdateEmail(Project project) {
        // Non-blocking email sending
        return CompletableFuture.runAsync(() -> emailClient.send(project.getOwnerEmail(), ...));
    }
}
```
