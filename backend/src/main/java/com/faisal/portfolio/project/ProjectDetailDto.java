package com.faisal.portfolio.project;

import java.util.List;

public record ProjectDetailDto(
        Long id,
        String slug,
        String title,
        String shortDescription,
        String fullDescription,
        String problemStatement,
        String solution,
        boolean featured,
        String period,
        String githubUrl,
        String liveUrl,
        List<TechnologyDto> technologies,
        List<ImageDto> images
) {
    public record TechnologyDto(Long id, String name, String iconUrl) {
    }

    public record ImageDto(String url, String altText, int displayOrder) {
    }

    public static ProjectDetailDto from(Project project) {
        List<TechnologyDto> techs = project.getTechnologies().stream()
                .map(t -> new TechnologyDto(t.getId(), t.getName(), t.getIconUrl()))
                .toList();

        List<ImageDto> imgs = project.getImages().stream()
                .map(i -> new ImageDto(i.getUrl(), i.getAltText(), i.getDisplayOrder()))
                .toList();

        return new ProjectDetailDto(
                project.getId(),
                project.getSlug(),
                project.getTitle(),
                project.getShortDescription(),
                project.getFullDescription(),
                project.getProblemStatement(),
                project.getSolution(),
                project.isFeatured(),
                project.getPeriod(),
                project.getGithubUrl(),
                project.getLiveUrl(),
                techs,
                imgs
        );
    }
}
