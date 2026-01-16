package dev.faisal.portfolio.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AboutDTO {
    private String name;
    private String tagline;
    private String bio;
    private String email;
    private String linkedin;
    private String github;
    private String resumeUrl;
    private String photoUrl;
}
