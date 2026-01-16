package dev.faisal.portfolio.repository;

import dev.faisal.portfolio.model.SkillCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillCategoryRepository extends JpaRepository<SkillCategory, Long> {

    @Query("SELECT DISTINCT sc FROM SkillCategory sc LEFT JOIN FETCH sc.skills ORDER BY sc.displayOrder ASC")
    List<SkillCategory> findAllWithSkillsOrderByDisplayOrder();
}
