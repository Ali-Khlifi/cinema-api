package fr.uga.gestioncinema.web;

import fr.uga.gestioncinema.mappers.CategoryMapper;
import fr.uga.gestioncinema.mappers.VilleMapper;
import fr.uga.gestioncinema.service.CategoryService;
import fr.uga.gestioncinema.service.VilleService;
import lombok.RequiredArgsConstructor;
import org.openapitools.api.CategoryApi;
import org.openapitools.api.VilleApi;
import org.openapitools.model.CategoryOpenApiModel;
import org.openapitools.model.VilleOpenApiModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class VilleController implements VilleApi {
    private final VilleMapper mapper;
    private final VilleService service;


    @Override
    public ResponseEntity<VilleOpenApiModel> create(VilleOpenApiModel model){
        return ResponseEntity.status(HttpStatus.CREATED).body( mapper.toOpenApiModel(service.save(mapper.toDto(model))));
    }

    @Override
    public ResponseEntity<VilleOpenApiModel> update(VilleOpenApiModel model){
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(mapper.toOpenApiModel(service.update(mapper.toDto(model))));
    }
    @Override
    public ResponseEntity<VilleOpenApiModel> findOne(Long id) throws Exception {
        return ResponseEntity.ok().body(mapper.toOpenApiModel(service.fetchOne(id)));
    }
    @Override
    public ResponseEntity<List<VilleOpenApiModel>> findAll() throws Exception {
        return ResponseEntity.ok().body(mapper.toOpenApiModelList(service.fetchAll()));
    }
    @Override
    public ResponseEntity<Void> delete(Long id) throws Exception {
        service.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
