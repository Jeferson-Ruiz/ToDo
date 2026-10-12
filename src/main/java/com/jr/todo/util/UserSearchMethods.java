package com.jr.todo.util;

import org.springframework.stereotype.Service;
import com.jr.todo.modules.user.entity.User;
import com.jr.todo.modules.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class UserSearchMethods {

    private final UserRepository userRepository;

    public UserSearchMethods(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findUserById(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        return user;
    }

    public User findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        return user;
    }

}
