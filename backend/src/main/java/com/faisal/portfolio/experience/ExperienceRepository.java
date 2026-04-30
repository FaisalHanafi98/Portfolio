package com.faisal.portfolio.experience;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExperienceRepository extends JpaRepository<Experience, Long> {

    List<Experience> findAllByOrderByDisplayOrderAsc();

    Optional<Experience> findByCompany(String company);
}
