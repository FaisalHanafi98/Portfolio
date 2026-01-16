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
public class ProjectSummaryDTO {
    private Long id;
    private String slug;
    private String title;
    private String shortDescription;
    private List<String> technologies;
    private String thumbnailUrl;
    private Boolean featured;
}
