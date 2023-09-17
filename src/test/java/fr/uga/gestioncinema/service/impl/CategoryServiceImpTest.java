package fr.uga.gestioncinema.service.impl;

import fr.uga.gestioncinema.dto.CategoryDto;
import fr.uga.gestioncinema.entities.Category;
import fr.uga.gestioncinema.exceptions.NotFoundException;
import fr.uga.gestioncinema.mappers.CategoryMapper;
import fr.uga.gestioncinema.repositories.CategorieRepository;
import fr.uga.gestioncinema.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImpTest {

    @Mock
    CategoryMapper mapper;
    @Mock
    CategorieRepository repository;

    CategoryService service;

    @BeforeEach
    void setUp() {service = new CategoryServiceImp(repository, mapper);}

    @Test
    void TestFetchOne() {
        // given
        final var id = 1L;
        final var name = "name";
        final var dto = CategoryDto.builder().id(id).name(name).build();
        final var entity = Category.builder().id(id).name(name).build();

        // when
        when(mapper.toDto(any(Category.class))).thenReturn(dto);
        when(repository.findById(id)).thenReturn(Optional.ofNullable(entity));

        CategoryDto returnedDto = service.fetchOne(id);
        assertEquals(dto, returnedDto);

        // then
        verify(repository, times(1)).findById(id);
        verify(mapper, times(1)).toDto(entity);
    }
    @Test
    void testFetchOneWithInvalidId() {
        // given
        final var invalidId = 99L;

        // when
        when(repository.findById(invalidId)).thenReturn(Optional.empty());

        // then
        assertThrows(NotFoundException.class, () -> service.fetchOne(invalidId));
        verify(repository, times(1)).findById(invalidId);
    }

    @Test
    void testDelete() {
        // given
        final var id = 1L;
        // when
        service.delete(id);
        // then
        verify(repository, times(1)).deleteById(id);
    }

    @Test
    void TestSave() {
        // given
        final var id = 1L;
        final var dto = CategoryDto.builder().id(id).build();
        final var entity = Category.builder().id(id).build();
        final var expected = CategoryDto.builder().id(id).build();
        final var saved = entity.toBuilder().id(id).build();

        // when
        when(mapper.toEntity(any(CategoryDto.class))).thenReturn(entity);
        when(mapper.toDto(any(Category.class))).thenReturn(dto);
        when(repository.save(any(Category.class))).thenReturn(saved);

        final var actual = service.save(dto);

        // then
        assertEquals(expected, actual);

        // verify methods called
        verify(repository, times(1)).save(entity);
        verify(mapper, times(1)).toDto(entity);
        verify(mapper, times(1)).toEntity(dto);
    }

    @Test
    void testUpdate() {
        final var id = 1L;
        final var name = "name";
        final var dto = CategoryDto.builder().id(id).name(name).build();
        final var entity = Category.builder().id(id).name(name).build();

        // when
        when(mapper.toDto(any(Category.class))).thenReturn(dto);
        when(repository.findById(dto.getId())).thenReturn(Optional.ofNullable(entity));

        CategoryDto returnedDto = service.update(dto);
        assertEquals(returnedDto, dto);

        // then
        verify(repository, times(1)).findById(dto.getId());
        verify(mapper, times(1)).toDto(entity);
        verify(repository, times(1)).save(entity);
        verify(mapper, times(1)).update(dto, entity);


    }
    @Test
    void testUpdateNonExisting() {
        // given
        final var id = 1L;
        final var dto = CategoryDto.builder().id(id).build();
        // when
        when(repository.findById(id)).thenReturn(Optional.empty());

        // then
        assertThrows(NotFoundException.class, () -> service.update(dto), "A NotFoundException is expected");

        verify(repository, times(1)).findById(id);
    }


    @Test
    void testFetchAll() {;
        final var dto = CategoryDto.builder().build();
        final var entity = Category.builder().build();

        // when
        when(mapper.toDto(any(Category.class))).thenReturn(dto);
        when(repository.findAll()).thenReturn(Arrays.asList(entity));

        final var returned = service.fetchAll();

        assertEquals(1, returned.size(), "Expected list size of 1, but got a different size.");

        //then
        verify(repository, times(1)).findAll();
        verify(mapper, times(1)).toDto(entity);
    }
}