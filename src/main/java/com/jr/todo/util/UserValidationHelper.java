package com.jr.todo.util;

import org.springframework.stereotype.Component;
import com.jr.todo.modules.user.repository.UserRepository;

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

    public void isEnabled(String email) {
        if (!userRepository.isUserEnabled(email)) {
            throw new IllegalAccessError("Usuario desactivado, validar mediante emial");
        }
    }
}