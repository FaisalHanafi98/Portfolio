package dev.faisal.portfolio.service;

import dev.faisal.portfolio.dto.SkillCategoryDTO;
import dev.faisal.portfolio.dto.SkillDTO;
import dev.faisal.portfolio.repository.SkillCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillCategoryRepository skillCategoryRepository;

    @Transactional(readOnly = true)
    public List<SkillCategoryDTO> getAllSkillsGroupedByCategory() {
        return skillCategoryRepository.findAllWithSkillsOrderByDisplayOrder()
                .stream()
                .map(category -> SkillCategoryDTO.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .displayOrder(category.getDisplayOrder())
                        .skills(category.getSkills().stream()
                                .map(skill -> SkillDTO.builder()
                                        .id(skill.getId())
                                        .name(skill.getName())
                                        .proficiency(skill.getProficiency())
                                        .iconUrl(skill.getIconUrl())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }
}
