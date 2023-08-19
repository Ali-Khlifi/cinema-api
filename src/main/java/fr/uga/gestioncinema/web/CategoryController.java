package fr.uga.gestioncinema.web;

import fr.uga.gestioncinema.mappers.CategoryMapper;
import fr.uga.gestioncinema.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.openapitools.api.CategoryApi;
import org.openapitools.model.CategoryOpenApiModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CategoryController implements CategoryApi {
    private final CategoryMapper mapper;
    private final CategoryService service;


    @Override
    public ResponseEntity<CategoryOpenApiModel> create(CategoryOpenApiModel model){
        return ResponseEntity.status(HttpStatus.CREATED).body( mapper.toOpenApiModel(service.save(mapper.toDto(model))));
    }

    @Override
    public ResponseEntity<CategoryOpenApiModel> update(CategoryOpenApiModel model){
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(mapper.toOpenApiModel(service.update(mapper.toDto(model))));
    }
    @Override
    public ResponseEntity<CategoryOpenApiModel> findOne(Long id) throws Exception {
        return ResponseEntity.ok().body(mapper.toOpenApiModel(service.fetchOne(id)));
    }
    @Override
    public ResponseEntity<List<CategoryOpenApiModel>> findAll() throws Exception {
        return ResponseEntity.ok().body(mapper.toOpenApiModelList(service.fetchAll()));
    }
    @Override
    public ResponseEntity<Void> delete(Long id) throws Exception {
        service.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
