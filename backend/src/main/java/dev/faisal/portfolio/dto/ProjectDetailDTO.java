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
public class ProjectDetailDTO {
    private Long id;
    private String slug;
    private String title;
    private String shortDescription;
    private String fullDescription;
    private String problemStatement;
    private String solution;
    private List<String> keyFeatures;
    private List<TechnologyDTO> technologies;
    private ProjectMetricsDTO metrics;
    private List<ProjectImageDTO> images;
    private ProjectLinksDTO links;
    private String period;
    private Boolean featured;
}
