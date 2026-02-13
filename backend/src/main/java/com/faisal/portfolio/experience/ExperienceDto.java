package com.faisal.portfolio.experience;

import java.util.List;

public record ExperienceDto(
        Long id,
        String company,
        String role,
        String location,
        String startDate,
        String endDate,
        String description,
        List<String> highlights
) {
    public static ExperienceDto from(Experience experience) {
        return new ExperienceDto(
                experience.getId(),
                experience.getCompany(),
                experience.getRole(),
                experience.getLocation(),
                experience.getStartDate().toString(),
                experience.getEndDate() != null ? experience.getEndDate().toString() : null,
                experience.getDescription(),
                experience.getHighlights()
        );
    }
}
