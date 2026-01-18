package fr.uga.gestioncinema.service.impl;

import fr.uga.gestioncinema.dto.UserDto;
import fr.uga.gestioncinema.exceptions.CustomErrorService;
import fr.uga.gestioncinema.mappers.UserMapper;
import fr.uga.gestioncinema.repositories.UserRepository;
import fr.uga.gestioncinema.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service implementation for User entity operations.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    CustomErrorService customErrorService;

    private final UserRepository repository;
    private final UserMapper mapper;

    @Override
    public UserDto fetchOne(Long id) {
        return mapper.toDto(repository.findById(id)
                .orElseThrow(() -> customErrorService.customErrorException("User not found", HttpStatus.NOT_FOUND)));
    }

    @Override
    public UserDto findByKeycloakId(String keycloakId) {
        return mapper.toDto(repository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> customErrorService.customErrorException("User not found with Keycloak ID: " + keycloakId, HttpStatus.NOT_FOUND)));
    }

    @Override
    public UserDto findByEmail(String email) {
        return mapper.toDto(repository.findByEmail(email)
                .orElseThrow(() -> customErrorService.customErrorException("User not found with email: " + email, HttpStatus.NOT_FOUND)));
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public UserDto save(UserDto dto) {
        return mapper.toDto(repository.save(mapper.toEntity(dto)));
    }

    @Override
    public UserDto update(@Valid final UserDto dto) {
        final var user = repository.findById(dto.getId())
                .orElseThrow(() -> customErrorService.customErrorException("User not found", HttpStatus.NOT_FOUND));

        mapper.update(dto, user);
        repository.save(user);

        return mapper.toDto(user);
    }

    @Override
    public List<UserDto> fetchAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }
}









