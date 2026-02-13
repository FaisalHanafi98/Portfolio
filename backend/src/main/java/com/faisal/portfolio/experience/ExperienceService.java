package com.faisal.portfolio.experience;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ExperienceService {

    private final ExperienceRepository experienceRepository;

    public ExperienceService(ExperienceRepository experienceRepository) {
        this.experienceRepository = experienceRepository;
    }

    public List<ExperienceDto> getAllExperiences() {
        return experienceRepository.findAllByOrderByDisplayOrderAsc().stream()
                .map(ExperienceDto::from)
                .toList();
    }
}
