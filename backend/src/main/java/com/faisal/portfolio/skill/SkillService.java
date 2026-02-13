package com.faisal.portfolio.skill;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SkillService {

    private final SkillCategoryRepository categoryRepository;

    public SkillService(SkillCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<SkillCategoryDto> getAllSkillsGrouped() {
        return categoryRepository.findAllWithSkills().stream()
                .map(SkillCategoryDto::from)
                .toList();
    }
}
