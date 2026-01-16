package dev.faisal.portfolio.service;

import dev.faisal.portfolio.dto.ExperienceDTO;
import dev.faisal.portfolio.repository.ExperienceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExperienceService {

    private final ExperienceRepository experienceRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    @Transactional(readOnly = true)
    public List<ExperienceDTO> getAllExperiences() {
        return experienceRepository.findAllByOrderByDisplayOrderAsc()
                .stream()
                .map(exp -> ExperienceDTO.builder()
                        .id(exp.getId())
                        .company(exp.getCompany())
                        .role(exp.getRole())
                        .location(exp.getLocation())
                        .startDate(exp.getStartDate().format(DATE_FORMATTER))
                        .endDate(exp.getEndDate() != null ? exp.getEndDate().format(DATE_FORMATTER) : null)
                        .description(exp.getDescription())
                        .highlights(exp.getHighlights())
                        .build())
                .collect(Collectors.toList());
    }
}
