package dev.faisal.portfolio.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectMetricsDTO {
    private Integer databaseTables;
    private Integer userRoles;
    private Integer linesOfCode;
    private String complexityReduction;
    private String recognition;
}
