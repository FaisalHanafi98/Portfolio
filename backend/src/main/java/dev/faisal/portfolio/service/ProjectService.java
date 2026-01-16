package dev.faisal.portfolio.service;

import dev.faisal.portfolio.dto.*;
import dev.faisal.portfolio.exception.ResourceNotFoundException;
import dev.faisal.portfolio.model.Project;
import dev.faisal.portfolio.model.Technology;
import dev.faisal.portfolio.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    @Transactional(readOnly = true)
    public List<ProjectSummaryDTO> getAllProjects() {
        return projectRepository.findAllOrderByFeaturedAndDisplayOrder()
                .stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProjectDetailDTO getProjectBySlug(String slug) {
        Project project = projectRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "slug", slug));
        return toDetailDTO(project);
    }

    private ProjectSummaryDTO toSummaryDTO(Project project) {
        return ProjectSummaryDTO.builder()
                .id(project.getId())
                .slug(project.getSlug())
                .title(project.getTitle())
                .shortDescription(project.getShortDescription())
                .technologies(project.getTechnologies().stream()
                        .map(Technology::getName)
                        .collect(Collectors.toList()))
                .thumbnailUrl(project.getThumbnailUrl())
                .featured(project.getFeatured())
                .build();
    }

    private ProjectDetailDTO toDetailDTO(Project project) {
        return ProjectDetailDTO.builder()
                .id(project.getId())
                .slug(project.getSlug())
                .title(project.getTitle())
                .shortDescription(project.getShortDescription())
                .fullDescription(project.getFullDescription())
                .problemStatement(project.getProblemStatement())
                .solution(project.getSolution())
                .keyFeatures(project.getKeyFeatures())
                .technologies(project.getTechnologies().stream()
                        .map(t -> TechnologyDTO.builder()
                                .id(t.getId())
                                .name(t.getName())
                                .category(t.getCategory())
                                .iconUrl(t.getIconUrl())
                                .build())
                        .collect(Collectors.toList()))
                .metrics(ProjectMetricsDTO.builder()
                        .databaseTables(project.getMetricDatabaseTables())
                        .userRoles(project.getMetricUserRoles())
                        .linesOfCode(project.getMetricLinesOfCode())
                        .complexityReduction(project.getMetricComplexityReduction())
                        .recognition(project.getMetricRecognition())
                        .build())
                .images(project.getImages().stream()
                        .map(i -> ProjectImageDTO.builder()
                                .id(i.getId())
                                .url(i.getUrl())
                                .altText(i.getAltText())
                                .displayOrder(i.getDisplayOrder())
                                .build())
                        .collect(Collectors.toList()))
                .links(ProjectLinksDTO.builder()
                        .github(project.getGithubUrl())
                        .live(project.getLiveUrl())
                        .build())
                .period(project.getPeriod())
                .featured(project.getFeatured())
                .build();
    }
}
