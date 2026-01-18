package fr.uga.gestioncinema.web;

import fr.uga.gestioncinema.mappers.UserMapper;
import fr.uga.gestioncinema.service.UserService;
import lombok.RequiredArgsConstructor;
import org.openapitools.api.UserApi;
import org.openapitools.model.UserOpenApiModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller for User entity operations.
 * Implements the UserApi interface generated from OpenAPI specification.
 */
@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
public class UserController implements UserApi {
    
    private final UserMapper mapper;
    private final UserService service;

    @Override
    public ResponseEntity<UserOpenApiModel> create(UserOpenApiModel model) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toOpenApiModel(service.save(mapper.toDto(model))));
    }

    @Override
    public ResponseEntity<UserOpenApiModel> update(UserOpenApiModel model) {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(mapper.toOpenApiModel(service.update(mapper.toDto(model))));
    }

    @Override
    public ResponseEntity<UserOpenApiModel> findOne(Long id) throws Exception {
        return ResponseEntity.ok()
                .body(mapper.toOpenApiModel(service.fetchOne(id)));
    }

    @Override
    public ResponseEntity<List<UserOpenApiModel>> findAll() throws Exception {
        return ResponseEntity.ok()
                .body(mapper.toOpenApiModelList(service.fetchAll()));
    }

    @Override
    public ResponseEntity<Void> delete(Long id) throws Exception {
        service.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    public ResponseEntity<UserOpenApiModel> findByEmail(String email) throws Exception {
        return ResponseEntity.ok()
                .body(mapper.toOpenApiModel(service.findByEmail(email)));
    }
}









