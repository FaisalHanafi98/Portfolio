package dev.faisal.portfolio.service;

import dev.faisal.portfolio.dto.AboutDTO;
import org.springframework.stereotype.Service;

@Service
public class AboutService {

    public AboutDTO getAboutInfo() {
        return AboutDTO.builder()
                .name("Mohamad Faisal Bin Mohd Hanafi")
                .tagline("Full-Stack Developer & Data Science Graduate")
                .bio("I'm a Data Science graduate turned full-stack developer based in Kuala Lumpur, Malaysia. " +
                     "I enjoy creating things that live on the internet, whether that be websites, applications, " +
                     "or anything in between. I graduated from International Islamic University Malaysia (IIUM) " +
                     "with a CGPA of 3.72 and received the Gold Medal for Best Final Year Project in the Development category.")
                .email("faisal@example.com")
                .linkedin("https://linkedin.com/in/faisal-hanafi")
                .github("https://github.com/FaisalHanafi")
                .resumeUrl("/resume.pdf")
                .photoUrl(null)
                .build();
    }
}
