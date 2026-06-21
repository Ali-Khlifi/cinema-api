package fr.uga.gestioncinema.web;

import fr.uga.gestioncinema.config.TestSecurityConfig;
import fr.uga.gestioncinema.dto.CategoryDto;
import fr.uga.gestioncinema.entities.Category;
import fr.uga.gestioncinema.repositories.CategorieRepository;
import fr.uga.gestioncinema.service.CategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test with {@code @SpringBootTest}.
 * <p>
 * <b>CategoryServiceTest</b> (Mockito)  → no Spring, no DB, mocks only, very fast
 * <b>This class</b> (@SpringBootTest)    → full Spring context, H2 database (see application-test.yml)
 * <p>
 * What happens here:
 * <ol>
 *   <li>Spring Boot starts with profile {@code test} → loads {@code application-test.yml}</li>
 *   <li>Real beans are injected: {@code CategoryService}, {@code CategorieRepository}, {@code CategoryMapper}</li>
 *   <li>Data is saved in H2 (in-memory), not PostgreSQL</li>
 *   <li>{@code @Transactional} rolls back after each test → DB stays clean</li>
 * </ol>
 */
@SpringBootTest
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
@Transactional
class CategoryServiceIntegrationTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategorieRepository repository;

    /**
     * No mocks here: we save a real row in H2, then call the real service + real repository.
     */
    @Test
    @DisplayName("findByName should return category from H2 database")
    void findByName_shouldReturnCategoryFromDatabase() {
        // Arrange — persist in H2 (real repository, not mocked)
        Category category = new Category();
        category.setName("Action");
        repository.save(category);

        // Act — real service, real mapper, real SQL via Spring Data
        CategoryDto result = categoryService.findByName("Action");

        // Assert
        assertThat(result.getName()).isEqualTo("Action");
        assertThat(result.getId()).isNotNull();
    }
}
