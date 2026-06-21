package fr.uga.gestioncinema.service;

import fr.uga.gestioncinema.dto.CategoryDto;
import fr.uga.gestioncinema.entities.Category;
import fr.uga.gestioncinema.mappers.CategoryMapper;
import fr.uga.gestioncinema.repositories.CategorieRepository;
import fr.uga.gestioncinema.service.impl.CategoryServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link CategoryServiceImp} using Mockito only.
 * No Spring context, no database — dependencies are mocked.
 *
 */
@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    private static final Long CATEGORY_ID = 1L;
    private static final String CATEGORY_NAME = "Action";

    @Mock
    private CategorieRepository repository;

    @Mock
    private CategoryMapper mapper;

    @InjectMocks
    private CategoryServiceImp categoryService;

    private Category entity;

    /** Shared fake entity — recreated before each test for isolation. */
    @BeforeEach
    void setUp() {
        entity = new Category();
        entity.setId(CATEGORY_ID);
    }

    @Test
    @DisplayName("findByName should return category when name exists")
    void findByName_shouldReturnCategoryWhenNameExists() {
        // Arrange
        entity.setName(CATEGORY_NAME);
        CategoryDto expected = CategoryDto.builder().id(CATEGORY_ID).name(CATEGORY_NAME).build();
        when(repository.findByName(CATEGORY_NAME)).thenReturn(Optional.of(entity));
        when(mapper.toDto(entity)).thenReturn(expected);

        // Act
        CategoryDto result = categoryService.findByName(CATEGORY_NAME);

        // Assert
        assertThat(result).isEqualTo(expected);
        verify(repository).findByName(CATEGORY_NAME);
    }

    @Test
    @DisplayName("fetchOne should return category when id exists")
    void fetchOne_shouldReturnCategoryWhenExists() {
        // Arrange
        CategoryDto expected = CategoryDto.builder().id(CATEGORY_ID).build();
        when(repository.findById(CATEGORY_ID)).thenReturn(Optional.of(entity));
        when(mapper.toDto(entity)).thenReturn(expected);

        // Act
        CategoryDto result = categoryService.fetchOne(CATEGORY_ID);

        // Assert
        assertThat(result).isEqualTo(expected);
        verify(repository).findById(CATEGORY_ID);
    }
    @Test
    @DisplayName("save should return saved category")
    void save_shouldReturnCategory() {
        // Arrange
        CategoryDto input = CategoryDto.builder().name(CATEGORY_NAME).build();
        CategoryDto expected = CategoryDto.builder().id(CATEGORY_ID).name(CATEGORY_NAME).build();
        entity.setName(CATEGORY_NAME);

        when(mapper.toEntity(input)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toDto(entity)).thenReturn(expected);

        // Act
        CategoryDto result = categoryService.save(input);

        // Assert
        assertThat(result).isEqualTo(expected);
        verify(mapper).toEntity(input);
        verify(repository).save(entity);
        verify(mapper).toDto(entity);
    }
}
