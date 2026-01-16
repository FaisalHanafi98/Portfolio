package dev.faisal.portfolio.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExperienceDTO {
    private Long id;
    private String company;
    private String role;
    private String location;
    private String startDate;
    private String endDate;
    private String description;
    private List<String> highlights;
}
