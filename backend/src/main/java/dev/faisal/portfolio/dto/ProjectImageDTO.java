package dev.faisal.portfolio.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectImageDTO {
    private Long id;
    private String url;
    private String altText;
    private Integer displayOrder;
}
