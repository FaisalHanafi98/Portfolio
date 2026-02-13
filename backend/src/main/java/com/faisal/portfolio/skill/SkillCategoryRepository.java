package com.faisal.portfolio.skill;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SkillCategoryRepository extends JpaRepository<SkillCategory, Long> {

    @Query("SELECT DISTINCT c FROM SkillCategory c LEFT JOIN FETCH c.skills ORDER BY c.displayOrder ASC")
    List<SkillCategory> findAllWithSkills();
}
