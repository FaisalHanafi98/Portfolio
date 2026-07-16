# Spring Boot Security

## Authentication: Stateless JWT

```java
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtService.isTokenValid(token, userDetails)) {
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        filterChain.doFilter(request, response);
    }
}
```

## Authorization: Method-Level Security

```java
// Enable at configuration level
@EnableMethodSecurity
@Configuration
public class SecurityConfig { ... }

// Apply on methods
@GetMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<List<UserResponse>> getAllUsers() { ... }

@PutMapping("/{id}")
@PreAuthorize("#id == authentication.principal.id or hasRole('ADMIN')")
public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest request) { ... }
```

## Password Security

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12); // cost factor 12
}

// Usage
String hashed = passwordEncoder.encode(rawPassword);
boolean valid = passwordEncoder.matches(rawPassword, hashed);
```

## Input Validation (Never Trust User Input)

```java
// DTO with Bean Validation
public record RegisterRequest(
    @NotBlank @Email String email,
    @NotBlank @Size(min = 8, max = 100) String password,
    @NotBlank @Size(max = 100) String name
) {}

// Parameterized queries — never string concatenation
// GOOD: Spring Data derived query (automatic parameterization)
Optional<User> findByEmail(String email);

// GOOD: JPQL with named parameters
@Query("SELECT u FROM User u WHERE u.email = :email AND u.active = true")
Optional<User> findActiveByEmail(@Param("email") String email);

// BAD: String concatenation
@Query("SELECT u FROM User u WHERE u.email = '" + email + "'") // NEVER
```

## Secrets Management

```yaml
# application.yml — reference env vars, never hardcode
spring:
  datasource:
    url: ${DATABASE_URL}
    username: ${DATABASE_USERNAME}
    password: ${DATABASE_PASSWORD}

jwt:
  secret: ${JWT_SECRET}
  expiration: ${JWT_EXPIRATION:86400000}
```

## Security Headers and CORS

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)  // Disabled for stateless JWT
            .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
            .headers(headers -> headers
                .frameOptions(HeadersConfigurer.FrameOptionsConfig::deny)
                .xssProtection(XssProtectionConfig::block)
                .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'")))
            .cors(cors -> cors.configurationSource(corsConfigurationSource()));
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("https://yourportfolio.com"));  // No wildcards
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}
```

## Rate Limiting (Bucket4j)

```java
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final Cache<String, Bucket> buckets = Caffeine.newBuilder()
            .expireAfterAccess(1, TimeUnit.HOURS)
            .build();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String ip = request.getRemoteAddr();
        Bucket bucket = buckets.get(ip, k -> Bucket.builder()
                .addLimit(Bandwidth.simple(100, Duration.ofMinutes(1)))
                .build());

        if (bucket.tryConsume(1)) {
            chain.doFilter(request, response);
        } else {
            response.setStatus(429);
            response.getWriter().write("{\"error\":\"rate_limit_exceeded\"}");
        }
    }
}
```

## Security Checklist

Before deploying any Portfolio change:
- [ ] No hardcoded credentials (use env vars)
- [ ] JWT validation filter active
- [ ] `@PreAuthorize` on all sensitive endpoints
- [ ] Parameterized queries only (no string concatenation)
- [ ] Passwords hashed with BCrypt (cost >= 12)
- [ ] CORS restricted to specific origins
- [ ] Security headers configured
- [ ] Rate limiting active on auth endpoints
- [ ] OWASP DependencyCheck in CI: `mvn verify -Powasp`
