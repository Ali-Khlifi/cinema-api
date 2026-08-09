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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link CategoryServiceImp} using Mockito only.
 * No Spring context, no database — dependencies are mocked.
 *
 */
@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    private static final Long CATEGORY_ID = 1L;
    private static final String CATEGORY_NAME = "Action";
    private static final String UPDATED_NAME = "Updated Category";
    private static final String DELETED_NAME = "Category to delete";

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

    @Test
    @DisplayName("update should return updated Category")
    void update_shouldReturnCategory(){
        // Arrange
        CategoryDto existingEntity = CategoryDto.builder().id(CATEGORY_ID).name(UPDATED_NAME).build();
        CategoryDto expected = CategoryDto.builder().id(CATEGORY_ID).name(UPDATED_NAME).build();

        existingEntity.setId(CATEGORY_ID);
        existingEntity.setName(UPDATED_NAME);

        when(repository.findById(CATEGORY_ID)).thenReturn(Optional.of(entity));
        when(mapper.toDto(entity)).thenReturn(expected);

        // Act
        CategoryDto result = categoryService.update(existingEntity);

        // Assert
        assertThat(result).isEqualTo(expected);
        verify(mapper).update(existingEntity, entity);
    }
    @Test
    @DisplayName("should return all Category entities")
    void fetchAll_shouldReturnAllCategories(){
        // Arrange
        List<CategoryDto> expectedDtos = Arrays.asList(
                CategoryDto.builder().id(1L).name("Category 1").build(),
                CategoryDto.builder().id(2L).name("Category 2").build(),
                CategoryDto.builder().id(3L).name("Category 3").build()
        );
        List<Category> entities = Arrays.asList(
                Category.builder().id(1L).name("Category 1").build(),
                Category.builder().id(2L).name("Category 2").build(),
                Category.builder().id(3L).name("Category 3").build()
        );
        when(repository.findAll()).thenReturn(entities);
        when(mapper.toDto(entities.get(0))).thenReturn(expectedDtos.get(0));
        when(mapper.toDto(entities.get(1))).thenReturn(expectedDtos.get(1));
        when(mapper.toDto(entities.get(2))).thenReturn(expectedDtos.get(2));

        // Act
        List<CategoryDto> result = categoryService.fetchAll();

        // Assert
        assertThat(result).isEqualTo(expectedDtos);
        assertThat(result).hasSize(3);
        verify(repository).findAll();
    }
    @Test
    @DisplayName("should delete Category By id")
    void deleteCategory(){
        // Arrange
        Category existingCategory = Category.builder()
                .id(CATEGORY_ID)
                .name(DELETED_NAME)
                .build();

        when(repository.findById(CATEGORY_ID)).thenReturn(Optional.of(existingCategory));
        doNothing().when(repository).deleteById(CATEGORY_ID);

        // Act
        categoryService.delete(CATEGORY_ID);

        // Assert
        verify(repository).findById(CATEGORY_ID);
        verify(repository).deleteById(CATEGORY_ID);
    }
}
