package dev.faisal.portfolio.repository;

import dev.faisal.portfolio.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    Optional<Project> findBySlug(String slug);

    @Query("SELECT p FROM Project p ORDER BY p.featured DESC, p.displayOrder ASC")
    List<Project> findAllOrderByFeaturedAndDisplayOrder();

    List<Project> findByFeaturedTrueOrderByDisplayOrderAsc();
}
