package com.jr.todo.util;

import com.jr.todo.modules.user.entity.User;
import com.jr.todo.modules.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;

public class UserSearchMethods {

    private final UserRepository userRepository;

    public UserSearchMethods(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        return user;
    }

}
