package com.faisal.portfolio.project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProjectRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProjectRepository projectRepository;

    @BeforeEach
    void setUp() {
        Project featured = new Project();
        featured.setSlug("featured-project");
        featured.setTitle("Featured Project");
        featured.setFeatured(true);
        featured.setDisplayOrder(2);
        entityManager.persist(featured);

        Project regular1 = new Project();
        regular1.setSlug("regular-first");
        regular1.setTitle("Regular First");
        regular1.setFeatured(false);
        regular1.setDisplayOrder(1);
        entityManager.persist(regular1);

        Project regular2 = new Project();
        regular2.setSlug("regular-second");
        regular2.setTitle("Regular Second");
        regular2.setFeatured(false);
        regular2.setDisplayOrder(3);
        entityManager.persist(regular2);

        entityManager.flush();
    }

    @Test
    void findBySlug_existingSlug_returnsProject() {
        Optional<Project> result = projectRepository.findBySlug("featured-project");

        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Featured Project");
        assertThat(result.get().isFeatured()).isTrue();
    }

    @Test
    void findBySlug_nonExistentSlug_returnsEmpty() {
        Optional<Project> result = projectRepository.findBySlug("nonexistent");

        assertThat(result).isEmpty();
    }

    @Test
    void findAllOrdered_returnsFeaturedFirstThenByDisplayOrder() {
        List<Project> results = projectRepository.findAllOrdered();

        assertThat(results).hasSize(3);
        assertThat(results.get(0).getSlug()).isEqualTo("featured-project");
        assertThat(results.get(1).getSlug()).isEqualTo("regular-first");
        assertThat(results.get(2).getSlug()).isEqualTo("regular-second");
    }
}
