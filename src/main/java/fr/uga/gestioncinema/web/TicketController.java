package fr.uga.gestioncinema.web;

import fr.uga.gestioncinema.mappers.TicketMapper;
import fr.uga.gestioncinema.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.openapitools.api.TicketApi;
import org.openapitools.model.TicketOpenApiModel;
import org.openapitools.model.TicketReservation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
public class TicketController implements TicketApi {
    private final TicketService service;
    private final TicketMapper mapper;


    /*@GetMapping(path = "/tickets")
    public List<Ticket> tickets(){
        return ticketRepository.findAll();
    }
    // liste des tickets qui sont vendus
    // @RequestBody --> les données du ticket sont envoyés dans le corps de la requête sous format JSON
    @PostMapping("/ticketSold")
    public List<Ticket> payerTickets(@RequestBody TicketForm ticketForm){
        return ticketService.payerTickets(ticketForm);
    }
    @PostMapping("/addTicket")
    public Ticket addTicket(@RequestBody TicketForm ticketForm){
        return ticketService.addTicket(ticketForm);
    }*/

    @Override
    public ResponseEntity<TicketOpenApiModel> create(TicketOpenApiModel model) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                mapper.toOpenApiModel(
                        service.save(
                                mapper.toDto(model))));
    }

    @Override
    public ResponseEntity<TicketOpenApiModel> findOne(final Long id) {
        return ResponseEntity.ok().body(
                mapper.toOpenApiModel(
                        service.fetchOne(id)));
    }

    @Override
    public ResponseEntity<TicketOpenApiModel> update(TicketOpenApiModel model) {
        return ResponseEntity.ok().body(
                mapper.toOpenApiModel(
                        service.update(
                                mapper.toDto(model))));
    }

    @Override
    public ResponseEntity<List<TicketOpenApiModel>> findAll() {
        return ResponseEntity.ok().body(mapper.toOpenApiModelList(service.fetchAll()));
    }
    @Override
    public ResponseEntity<Void> delete(Long id) throws Exception {
        service.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    @Override
    public ResponseEntity<List<TicketOpenApiModel>> findByFilmProjection(Long id) throws Exception {
        return ResponseEntity.ok().body(mapper.toOpenApiModelList(service.findByFilmProjection(id)));
    }
    @Override
    public ResponseEntity<List<TicketOpenApiModel>> ticketSold(TicketReservation ticketReservation) throws Exception {
        return ResponseEntity.ok().body(mapper.toOpenApiModelList(service.payerTickets(ticketReservation)));
    }

}
