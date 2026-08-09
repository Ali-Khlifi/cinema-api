package fr.uga.gestioncinema.service.impl;

import fr.uga.gestioncinema.dto.FilmProjectionDto;
import fr.uga.gestioncinema.entities.FilmProjection;
import fr.uga.gestioncinema.exceptions.CustomErrorService;
import fr.uga.gestioncinema.mappers.FilmProjectionMapper;
import fr.uga.gestioncinema.repositories.FilmProjectionRepository;
import fr.uga.gestioncinema.service.FilmProjectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class FilmProjectionServiceImpl implements FilmProjectionService {
    @Autowired
    CustomErrorService customErrorService;
    private final FilmProjectionRepository repository;
    private final FilmProjectionMapper mapper;

    @Override
    public FilmProjectionDto fetchOne(Long id) {
        return mapper.toDto(repository.findById(id)
                .orElseThrow(() -> customErrorService.customErrorException("Not found", HttpStatus.NOT_FOUND)));

    }

    @Override
    public FilmProjectionDto save(FilmProjectionDto dto) {
        return mapper.toDto(repository.save(mapper.toEntity(dto)));
    }

    @Override
    public List<FilmProjectionDto> fetchAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }
    @Override
    public List<FilmProjectionDto> findByRoom(Long roomId) {
        return repository.findUniqueProjectionsByRoomId(roomId)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public FilmProjectionDto update(FilmProjectionDto dto) {
        final var filmProjection = repository.findById(dto.getId())
                .orElseThrow(() -> customErrorService.customErrorException("Not found", HttpStatus.NOT_FOUND));

        mapper.update(dto, filmProjection);

        return mapper.toDto(repository.save(filmProjection));
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);

    }

    @Override
    public List<FilmProjectionDto> findByDate(LocalDate date) {

        return repository.findByProjectionDate(date)
                .stream()
                .map(mapper::toDto)
                .toList();

    }

    @Override
    public List<FilmProjectionDto> findUpcoming(LocalDate fromDate, LocalTime fromTime) {

        final var filmProjections = repository.findAll();

        List<FilmProjection> upComingProjections = new ArrayList<>();

        for(FilmProjection projection : filmProjections){
            if(projection.getProjectionDate().isAfter(fromDate)
                    || (projection.getProjectionDate().isEqual(fromDate)
                    && !projection.getStartTime().isBefore(fromTime)))
                upComingProjections.add(projection);
        }
        return
        upComingProjections
                .stream()
                .map(mapper::toDto)
                .toList();

    }

    @Override
    public boolean isOngoing(Long id, LocalDate date, LocalTime time) {

        final var filmProjection = repository.findById(id)
                .orElseThrow(() -> customErrorService.customErrorException("Not found", HttpStatus.NOT_FOUND));

        return filmProjection.getProjectionDate().isEqual(date)
                && !filmProjection.getStartTime().isAfter(time)
                && filmProjection.getEndTime().isAfter(time);
    }

}

