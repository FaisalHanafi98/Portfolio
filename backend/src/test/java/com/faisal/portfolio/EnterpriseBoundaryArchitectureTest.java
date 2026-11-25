package com.faisal.portfolio;

import com.faisal.portfolio.common.GlobalExceptionHandler;
import com.faisal.portfolio.common.ResourceNotFoundException;
import jakarta.persistence.Entity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EnterpriseBoundaryArchitectureTest {

    @RestController
    @RequestMapping("/test-boundary")
    static class SimulatedFailureController {
        @GetMapping("/db-failure")
        public ResponseEntity<Void> failWithJdbcDetails() {
            throw new RuntimeException(
                    "FATAL: connection to jdbc:postgresql://portfolio-db:5432/portfolio failed at PortfolioApplication.java:42"
            );
        }

        @GetMapping("/missing")
        public ResponseEntity<Void> failMissing() {
            throw new ResourceNotFoundException("Project not found: unknown-slug");
        }
    }

    @Test
    void mainSourcesContainNoStdoutStderrPrintStackTraceOrFieldAutowired() throws IOException {
        Path mainJavaRoot = Path.of("src/main/java");
        assertThat(Files.isDirectory(mainJavaRoot)).isTrue();

        List<Path> files;
        try (Stream<Path> walk = Files.walk(mainJavaRoot)) {
            files = walk.filter(p -> p.toString().endsWith(".java")).sorted().toList();
        }
        assertThat(files).isNotEmpty();

        for (Path file : files) {
            List<String> lines = Files.readAllLines(file);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                int lineNumber = i + 1;
                assertThat(line)
                        .as("%s:%d must not use System.out", file, lineNumber)
                        .doesNotContain("System.out");
                assertThat(line)
                        .as("%s:%d must not use System.err", file, lineNumber)
                        .doesNotContain("System.err");
                assertThat(line)
                        .as("%s:%d must not call printStackTrace()", file, lineNumber)
                        .doesNotContain("printStackTrace(");
                assertThat(line.trim())
                        .as("%s:%d must use constructor injection instead of @Autowired", file, lineNumber)
                        .doesNotStartWith("@" + Autowired.class.getSimpleName());
            }
        }
    }

    @Test
    void restControllersExposeDtosOrMapsNeverJpaEntities() throws ClassNotFoundException {
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(RestController.class));

        var candidates = scanner.findCandidateComponents("com.faisal.portfolio");
        assertThat(candidates).isNotEmpty();

        for (var candidate : candidates) {
            Class<?> controllerClass = Class.forName(candidate.getBeanClassName());
            for (Method method : controllerClass.getDeclaredMethods()) {
                boolean isEndpoint = method.isAnnotationPresent(GetMapping.class)
                        || method.isAnnotationPresent(PostMapping.class)
                        || method.isAnnotationPresent(PutMapping.class)
                        || method.isAnnotationPresent(DeleteMapping.class)
                        || method.isAnnotationPresent(PatchMapping.class)
                        || method.isAnnotationPresent(RequestMapping.class);
                if (!isEndpoint) {
                    continue;
                }
                assertNoJpaEntityInType(controllerClass.getSimpleName() + "." + method.getName(), method.getGenericReturnType());
            }
        }
    }

    @Test
    void prodConfigLocksDownActuatorPrometheusSwaggerAndErrors() {
        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(
                new ClassPathResource("application.yml"),
                new ClassPathResource("application-prod.yml")
        );
        Properties prodProps = yaml.getObject();
        assertThat(prodProps).isNotNull();

        String exposure = prodProps.getProperty("management.endpoints.web.exposure.include", "");
        Set<String> exposedEndpoints = Set.of(exposure.split("\\s*,\\s*"));
        assertThat(exposedEndpoints).containsExactly("health");
        assertThat(exposedEndpoints)
                .doesNotContain("prometheus", "metrics", "info", "env", "beans", "heapdump", "threaddump", "*");

        assertThat(prodProps.getProperty("management.endpoint.health.show-details")).isEqualTo("never");
        assertThat(prodProps.getProperty("management.endpoint.prometheus.access")).isEqualTo("none");
        assertThat(prodProps.getProperty("springdoc.api-docs.enabled")).isEqualTo("false");
        assertThat(prodProps.getProperty("springdoc.swagger-ui.enabled")).isEqualTo("false");
        assertThat(prodProps.getProperty("server.error.include-stacktrace")).isEqualTo("never");
        assertThat(prodProps.getProperty("server.error.include-message")).isEqualTo("never");
    }

    @Test
    void apiErrorsDoNotLeakJdbcUrlsStackTracesOrInternalHostnames() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new SimulatedFailureController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mockMvc.perform(get("/test-boundary/db-failure"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("An unexpected error occurred"))
                .andExpect(content().string(not(containsString("jdbc:"))))
                .andExpect(content().string(not(containsString("postgresql"))))
                .andExpect(content().string(not(containsString("portfolio-db"))))
                .andExpect(content().string(not(containsString(":5432"))))
                .andExpect(content().string(not(containsString(".java:"))));

        mockMvc.perform(get("/test-boundary/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(content().string(not(containsString("jdbc:"))))
                .andExpect(content().string(not(containsString("RuntimeException"))));
    }

    private static void assertNoJpaEntityInType(String location, Type type) {
        if (type instanceof Class<?> clazz) {
            assertThat(clazz.isAnnotationPresent(Entity.class))
                    .as("%s must not expose JPA @Entity %s", location, clazz.getName())
                    .isFalse();
            return;
        }
        if (type instanceof ParameterizedType pt) {
            assertNoJpaEntityInType(location, pt.getRawType());
            for (Type arg : pt.getActualTypeArguments()) {
                assertNoJpaEntityInType(location, arg);
            }
        }
    }
}
