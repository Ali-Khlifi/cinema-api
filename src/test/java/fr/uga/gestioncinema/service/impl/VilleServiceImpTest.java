package fr.uga.gestioncinema.service.impl;

import fr.uga.gestioncinema.dto.VilleDto;
import fr.uga.gestioncinema.entities.Ville;
import fr.uga.gestioncinema.exceptions.NotFoundException;
import fr.uga.gestioncinema.mappers.VilleMapper;
import fr.uga.gestioncinema.repositories.VilleRepository;
import fr.uga.gestioncinema.service.VilleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
class VilleServiceImpTest {

    @Mock
    VilleRepository repository;
    @Mock
    VilleMapper mapper;

    VilleService service;
    @BeforeEach
    void setup() {service = new VilleServiceImp(repository, mapper);}

    @Test
    void testFetchOne() {
        // given
        final var id = 1L;
        final var name = "name";
        final var longitude = 2d;

        final var entity = Ville.builder().id(id).name(name).latitude(longitude).longitude(longitude).altitude(longitude).build();
        final var dto = VilleDto.builder().id(id).name(name).latitude(longitude).longitude(longitude).altitude(longitude).build();

        // when
        when(mapper.toDto(any(Ville.class))).thenReturn(dto);
        when(repository.findById(id)).thenReturn(Optional.ofNullable(entity));

        VilleDto returnedDto = service.fetchOne(id);
        assertEquals(dto, returnedDto);

        // then
        verify(mapper, times(1)).toDto(entity);
        verify(repository, times(1)).findById(id);

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
    void testSave() {
        // given
        final var id = 1L;
        final var name = "name";
        final var longitude = 2d;
        final var entity = Ville.builder().id(id).name(name).latitude(longitude).longitude(longitude).altitude(longitude).build();
        final var dto = VilleDto.builder().id(id).name(name).latitude(longitude).longitude(longitude).altitude(longitude).build();
        final var expected = VilleDto.builder().id(id).name(name).latitude(longitude).longitude(longitude).altitude(longitude).build();
        final var saved = entity.toBuilder().id(id).build();

        // when
        when(mapper.toEntity(any(VilleDto.class))).thenReturn(entity);
        when(mapper.toDto(any(Ville.class))).thenReturn(dto);
        when(repository.save(any(Ville.class))).thenReturn(saved);

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
        // given
        final var id = 1L;
        final var name = "name";
        final var longitude = 2d;

        final var entity = Ville.builder().id(id).name(name).latitude(longitude).longitude(longitude).altitude(longitude).build();
        final var dto = VilleDto.builder().id(id).name(name).latitude(longitude).longitude(longitude).altitude(longitude).build();

        // when
        when(mapper.toDto(any(Ville.class))).thenReturn(dto);
        when(repository.findById(dto.getId())).thenReturn(Optional.ofNullable(entity));

        VilleDto returnedDto = service.update(dto);

        // then
        verify(repository, times(1)).findById(dto.getId());
        verify(mapper, times(1)).toDto(entity);
        verify(repository, times(1)).save(entity);
        verify(mapper, times(1)).update(dto, entity);

        assertEquals(returnedDto, dto);

    }
    @Test
    void testUpdateNonExisting() {
        // given
        final var id = 1L;
        final var dto = VilleDto.builder().id(id).build();
        // when
        when(repository.findById(id)).thenReturn(Optional.empty());

        // then
        assertThrows(NotFoundException.class, () -> service.update(dto), "A NotFoundException is expected");

        verify(repository, times(1)).findById(id);
    }

    @Test
    void testFetchAll() {
        // given
        final var entity = Ville.builder().build();
        final var dto = VilleDto.builder().build();

        // when
        when(mapper.toDto(any(Ville.class))).thenReturn(dto);
        when(repository.findAll()).thenReturn(Arrays.asList(entity));

        final var returned = service.fetchAll();

        // then
        assertEquals(1, returned.size(), "Expected list size of 1, but got a different size.");

        verify(repository, times(1)).findAll();
        verify(mapper, times(1)).toDto(entity);
    }
}