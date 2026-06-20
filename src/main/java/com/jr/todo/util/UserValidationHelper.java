package com.jr.todo.util;

import org.springframework.stereotype.Component;
import com.jr.todo.modules.user.entity.User;
import com.jr.todo.modules.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;

@Component
public class UserValidationHelper {
    private final UserRepository userRepository;

    public UserValidationHelper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void validateUsername(String name) {
        if (userRepository.existsByName(name)) {
            throw new IllegalArgumentException("username ya registrado");
        }
    }

    public void validateEmail(String email) {
        if (userRepository.existByEmail(email)) {
            throw new IllegalArgumentException("username ya registrado");
        }
    }

    public User findUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        return user;
    }
}