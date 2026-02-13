package com.faisal.portfolio.skill;

import java.util.List;

public record SkillCategoryDto(
        Long id,
        String name,
        List<SkillDto> skills
) {
    public record SkillDto(Long id, String name, int proficiency, String iconUrl) {
    }

    public static SkillCategoryDto from(SkillCategory category) {
        List<SkillDto> skillDtos = category.getSkills().stream()
                .map(s -> new SkillDto(s.getId(), s.getName(), s.getProficiency(), s.getIconUrl()))
                .toList();

        return new SkillCategoryDto(
                category.getId(),
                category.getName(),
                skillDtos
        );
    }
}
