package fr.uga.gestioncinema.service;

import fr.uga.gestioncinema.dto.UserDto;
import jakarta.validation.Valid;

import java.util.List;

/**
 * Service interface for User entity operations.
 */
public interface UserService {
    UserDto save(@Valid UserDto dto);
    UserDto update(@Valid UserDto dto);
    List<UserDto> fetchAll();
    UserDto fetchOne(Long id);
    UserDto findByKeycloakId(String keycloakId);
    UserDto findByEmail(String email);
    void delete(Long id);
}









