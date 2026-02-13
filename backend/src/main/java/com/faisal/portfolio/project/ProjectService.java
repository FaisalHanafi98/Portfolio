package com.faisal.portfolio.project;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public List<ProjectDto> getAllProjects() {
        return projectRepository.findAllOrdered().stream()
                .map(ProjectDto::from)
                .toList();
    }

    public ProjectDetailDto getProjectBySlug(String slug) {
        Project project = projectRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + slug));
        return ProjectDetailDto.from(project);
    }
}
