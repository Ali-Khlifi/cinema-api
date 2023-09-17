package fr.uga.gestioncinema.service.impl;

import fr.uga.gestioncinema.dto.FilmDto;
import fr.uga.gestioncinema.entities.Film;
import fr.uga.gestioncinema.exceptions.NotFoundException;
import fr.uga.gestioncinema.mappers.FilmMapper;
import fr.uga.gestioncinema.repositories.FilmRepository;
import fr.uga.gestioncinema.service.FilmService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
class FilmServiceImplTest {

    @Mock
    FilmRepository repository;
    @Mock
    FilmMapper mapper;

    FilmService service;

    @BeforeEach
    void setUp() {
        service = new FilmServiceImpl(repository, mapper);
    }

    @Test
    void testReadImages() throws IOException {
        // given
        final var id = 1L;
        final var photoName = "example.jpg";
        final var entity = Film.builder().id(id).photo(photoName).build();
        final byte[] expectedBytes = "file content".getBytes();

        // Mock le comportement de filmRepository
        when(repository.findById(id)).thenReturn(Optional.of(entity));

        // Mock le comportement des méthodes statiques
        try (MockedStatic<Paths> pathsMock = Mockito.mockStatic(Paths.class);
             MockedStatic<Files> filesMock = Mockito.mockStatic(Files.class)) {
            Path mockedPath = mock(Path.class);
            pathsMock.when(() -> Paths.get(anyString())).thenReturn(mockedPath);
            filesMock.when(() -> Files.readAllBytes(mockedPath)).thenReturn(expectedBytes);

            // Appellez la méthode à tester
            byte[] actualBytes = service.readImages(id);

            // Vérifiez le résultat
            assertEquals(expectedBytes, actualBytes);
        }
    }

    @Test
    void testSaveFilmWithImage() throws IOException {
        //given
        Long id = 1L;
        Film film = Film.builder().id(id).build();
        String fileName = "testImage.jpg";
        byte[] fileContent = "image content".getBytes();

        // when
        MultipartFile imageFile = mock(MultipartFile.class);
        when(imageFile.getOriginalFilename()).thenReturn(fileName);
        when(imageFile.getBytes()).thenReturn(fileContent);
        when(repository.findById(id)).thenReturn(Optional.of(film));

        // Mock les méthodes statiques
        try (MockedStatic<Paths> pathsMock = Mockito.mockStatic(Paths.class);
             MockedStatic<Files> filesMock = Mockito.mockStatic(Files.class)) {

            Path mockedPath = mock(Path.class);
            pathsMock.when(() -> Paths.get(anyString())).thenReturn(mockedPath);
            filesMock.when(() -> Files.write(mockedPath, fileContent)).thenReturn(mockedPath);
            // Appellez la méthode à tester
            service.saveFilmWithImage(id, imageFile);

            // verify
            verify(repository).save(film);
            assertEquals(fileName, film.getPhoto());
        }
    }

    @Test
    void testFetchOne() {
        // given
        final var id = 1L;
        final var entity = Film.builder().id(id).build();
        final var dto = FilmDto.builder().id(id).build();

        // when
        when(mapper.toDto(any(Film.class))).thenReturn(dto);
        when(repository.findById(id)).thenReturn(Optional.ofNullable(entity));

        FilmDto returnedDto = service.fetchOne(id);
        assertEquals(dto, returnedDto);

        // then
        verify(repository, times(1)).findById(id);
        verify(mapper, times(1)).toDto(entity);
    }

    @Test
    void testSave() {
        // given
        final var id = 1L;
        final var entity = Film.builder().id(id).build();
        final var dto = FilmDto.builder().id(id).build();
        final var saved = entity.toBuilder().id(id).build();
        final var expectedDto  = dto.toBuilder().id(id).build();

        // when
        when(mapper.toDto(saved)).thenReturn(expectedDto);
        when(mapper.toEntity(dto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(saved);

        FilmDto actualDto = service.save(dto);
        assertEquals(expectedDto, actualDto);

        // then
        verify(repository, times(1)).save(entity);
        verify(mapper, times(1)).toDto(saved);
        verify(mapper, times(1)).toEntity(dto);
    }

    @Test
    void testFetchAll() {
        final var dto = FilmDto.builder().build();
        final var entity = Film.builder().build();

        // when
        when(mapper.toDto(any(Film.class))).thenReturn(dto);
        when(repository.findAll()).thenReturn(Arrays.asList(entity));

        final var actualDto = service.fetchAll();

        assertEquals(1, actualDto.size(), "Expected list size of 1, but got a different size.");

        //then
        verify(repository, times(1)).findAll();
        verify(mapper, times(1)).toDto(entity);
    }

    @Test
    void testUpdate() {
        final var id = 1L;
        final var dto = FilmDto.builder().id(id).build();
        final var entity = Film.builder().id(id).build();

        // when
        when(mapper.toDto(entity)).thenReturn(dto);
        when(repository.findById(dto.getId())).thenReturn(Optional.ofNullable(entity));

        FilmDto actualDto = service.update(dto);
        assertEquals(actualDto, dto);

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
        final var dto = FilmDto.builder().id(id).build();
        // when
        when(repository.findById(id)).thenReturn(Optional.empty());

        // then
        assertThrows(NotFoundException.class, () -> service.update(dto), "A NotFoundException is expected");

        verify(repository, times(1)).findById(id);
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
}