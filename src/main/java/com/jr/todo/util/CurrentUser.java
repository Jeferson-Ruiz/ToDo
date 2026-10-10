package com.jr.todo.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import com.jr.todo.modules.user.entity.User;
import com.jr.todo.modules.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;

@Component
public class CurrentUser {
    private UserRepository userRepository;

    public CurrentUser(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User currentUser() throws EntityNotFoundException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
    }
}
