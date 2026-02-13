package com.faisal.portfolio.project;

import java.util.List;

public record ProjectDto(
        Long id,
        String slug,
        String title,
        String shortDescription,
        List<String> technologies,
        boolean featured,
        String period
) {
    public static ProjectDto from(Project project) {
        List<String> techNames = project.getTechnologies().stream()
                .map(Technology::getName)
                .sorted()
                .toList();

        return new ProjectDto(
                project.getId(),
                project.getSlug(),
                project.getTitle(),
                project.getShortDescription(),
                techNames,
                project.isFeatured(),
                project.getPeriod()
        );
    }
}
