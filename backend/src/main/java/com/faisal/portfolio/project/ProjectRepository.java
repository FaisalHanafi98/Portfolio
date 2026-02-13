package com.faisal.portfolio.project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    Optional<Project> findBySlug(String slug);

    @Query("SELECT p FROM Project p ORDER BY p.featured DESC, p.displayOrder ASC")
    List<Project> findAllOrdered();
}
