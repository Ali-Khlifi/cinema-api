package fr.uga.gestioncinema.service.impl;

import fr.uga.gestioncinema.dto.TicketDto;
import fr.uga.gestioncinema.entities.Ticket;
import fr.uga.gestioncinema.exceptions.NotFoundException;
import fr.uga.gestioncinema.mappers.TicketMapper;
import fr.uga.gestioncinema.repositories.TicketRepository;
import fr.uga.gestioncinema.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.openapitools.model.TicketReservation;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static fr.uga.gestioncinema.utils.StringUtils.Exceptions.NO_DATA;

@Primary
@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {
    private final TicketRepository repository;
    private final TicketMapper mapper;

    @Override
    public List<TicketDto> payerTickets(TicketReservation ticketReservation) {

        List<TicketDto> ticketsReserved = new ArrayList<>();

        List<Long> ticketIdsClient = ticketReservation.getTicketIds();

        for(Long id : ticketIdsClient){
            ticketsReserved.add(mapper.toDto(repository.findById(id)
                    .orElseThrow(() -> new NotFoundException(NO_DATA))));
        }
        for(TicketDto ticketDto : ticketsReserved){
            ticketDto.setNomClient(ticketReservation.getNomClient());
            ticketDto.setCodePayement(ticketReservation.getCodePayement());
            ticketDto.setReservee(true);
            repository.save( mapper.toEntity(ticketDto));
        }
        return ticketsReserved;
    }

    @Override
    public Ticket addTicket(TicketReservation ticketReservation) {
        return null;
    }

    @Override
    public TicketDto fetchOne(Long id) {
        return mapper.toDto(repository.findById(id)
                .orElseThrow(() -> new NotFoundException(NO_DATA)));
    }

    @Override
    public TicketDto save(TicketDto dto) {
        return mapper.toDto(repository.save(mapper.toEntity(dto)));
    }

    @Override
    public List<TicketDto> fetchAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public List<TicketDto> findByFilmProjection(Long filmProjectionId) {
        return repository.findByFilmProjection_Id(filmProjectionId)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public TicketDto update(TicketDto dto) {
        final var ticket = repository.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException(NO_DATA));

        mapper.update(dto, ticket);

        return mapper.toDto(repository.save(ticket));
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);

    }
   /* @Override
    public Ticket addTicket(TicketForm ticketForm) {
        Ticket ticket = new Ticket();
        ticket.setNomClient(ticketForm.getNomClient());
        ticket.setCodePayement(ticketForm.getCodePayement());
        ticket.setReservee(true);

        // Récupérer la place et la projection d'un film à partir de leurs ID et mise à jour de l'objet Ticket
        //Optional<FilmProjection> projection = filmProjectionRepository.findById(ticketForm.getFilmProjectionId());

        //if(place.isPresent() && projection.isPresent()){
        //ticket.setPlace(place.get());
        //ticket.setFilmProjection(projection.get());

        //return ticketRepository.save(ticket);
        //}
        return ticket;*/
}

