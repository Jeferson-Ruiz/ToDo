package com.jr.todo.modules.user.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.jr.todo.dto.PagedResponse;
import com.jr.todo.enums.Role;
import com.jr.todo.modules.user.dto.AdminCreateDto;
import com.jr.todo.modules.user.dto.UserResponseDto;
import com.jr.todo.modules.user.entity.User;
import com.jr.todo.modules.user.repository.UserRepository;
import com.jr.todo.util.DateValidationsHelper;
import com.jr.todo.util.PaginationUtil;
import com.jr.todo.util.UserSearchMethods;
import com.jr.todo.util.UserValidationHelper;
import jakarta.persistence.EntityNotFoundException;

@Service
public class AdministrativeService implements IAdministrativeService {

    private final UserRepository userRepository;
    private final UserValidationHelper userValidation;
    private PasswordEncoder passwordEncoder;
    private final UserSearchMethods userSearchMethods;

    public AdministrativeService(UserRepository userRepository, UserValidationHelper userValidation,
            PasswordEncoder passwordEncoder, UserSearchMethods userSearchMethods) {
        this.userRepository = userRepository;
        this.userValidation = userValidation;
        this.passwordEncoder = passwordEncoder;
        this.userSearchMethods = userSearchMethods;
    }

    // Metodos de ADMIN
    @Override
    public UserResponseDto createUser(AdminCreateDto userDto) {
        String username = userDto.username().strip();
        String email = userDto.email().strip();

        userValidation.validateUsername(username);
        userValidation.validateEmail(email);

        User user = userDto.toEntity();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(userDto.password()));
        user.setEnabled(true);
        user.setRegistrationDte(LocalDateTime.now());
        return UserResponseDto.toDto(userRepository.save(user));
    }

    @Override
    public PagedResponse<UserResponseDto> findAllUsers(int page) {
        Pageable pageable = PaginationUtil.of(page, "registrationDte");
        Page<User> userPage = userRepository.findAll(pageable);
        Page<UserResponseDto> dtoPage = userPage.map(UserResponseDto::toDto);
        return PaginationUtil.toResponse(dtoPage);
    }

    @Override 
    public UserResponseDto findByName(String name){
        User user = userRepository.findUserByName(name)
            .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        return UserResponseDto.toDto(user);
    }

    @Override
    public List<UserResponseDto> findAllByDate(LocalDate dateInit, LocalDate dateFinish) {
        LocalDateTime start = dateInit.atStartOfDay();
        LocalDateTime end = dateFinish.atTime(LocalTime.MAX);

        DateValidationsHelper.dateValidation(dateInit, dateFinish);
        List<User> users = userRepository.findAllByDate(start, end);
        return toDto(users);
    }

    @Override
    public List<UserResponseDto> findAllByRole(Role role) {
        List<User> users = userRepository.findAllByRole(role);
        return toDto(users);
    }

    @Override
    public void updateEnable(long id, boolean enable) {
        User user = userSearchMethods.findUserById(id);
        if (user.isEnabled() == enable) {
            return;
        }
        user.setEnabled(enable);
        userRepository.save(user);
    }

    @Override
    public void updateRole(long id, Role role) {
        User user = userSearchMethods.findUserById(id);
        user.setRole(role);
        userRepository.save(user);
    }

    @Override
    public void updatePassword(long id, String newPassword) {
        User user = userSearchMethods.findUserById(id);

        String savePassword = newPassword.strip();
        user.setPassword(passwordEncoder.encode(savePassword));
        userRepository.save(user);
    }

    // Helpers
    private List<UserResponseDto> toDto(List<User> users) {
        return users.stream()
                .map(UserResponseDto::toDto)
                .collect(Collectors.toList());
    }
}