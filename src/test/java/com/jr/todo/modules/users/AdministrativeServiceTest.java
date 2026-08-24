package com.jr.todo.modules.users;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.jr.todo.DataProviderAdministrative;
import com.jr.todo.dto.UserCreateDto;
import com.jr.todo.enums.Role;
import com.jr.todo.modules.user.dto.UserResponseDto;
import com.jr.todo.modules.user.entity.User;
import com.jr.todo.modules.user.repository.UserRepository;
import com.jr.todo.modules.user.service.AdministrativeService;
import com.jr.todo.util.UserSearchMethods;
import com.jr.todo.util.UserValidationHelper;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class AdministrativeServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserValidationHelper userValidationHelper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserSearchMethods userSearchMethods;

    @InjectMocks
    private AdministrativeService administrativeService;

    @Test
    void testCreateUser() {
        UserCreateDto useDto = DataProviderAdministrative.userDtoMock();
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        UserResponseDto result = administrativeService.createUser(useDto);
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals(result.name(), captor.getValue().getName());
    }

    @Test
    void testCreateUserErrorUsername() {
        UserCreateDto userDto = DataProviderAdministrative.userDtoMock();
        doThrow(new IllegalArgumentException("username ya registrado"))
                .when(userValidationHelper).validateUsername(anyString());

        assertThrows(IllegalArgumentException.class, () -> {
            administrativeService.createUser(userDto);
        });

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testCreateUserErrorEmail() {
        UserCreateDto userDto = DataProviderAdministrative.userDtoMock();
        doThrow(new IllegalArgumentException("email ya registrado"))
                .when(userValidationHelper).validateEmail(anyString());
        assertThrows(IllegalArgumentException.class, () -> {
            administrativeService.createUser(userDto);
        });
        verify(userRepository, never()).save(any(User.class));

    }

    @Test
    void testUpdateEnable() {
        String email = "prueba@correo.com";
        boolean enable = true;
        when(userSearchMethods.findByEmail(email)).thenReturn(DataProviderAdministrative.userMock());
        administrativeService.updateEnable(email, enable);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals(enable, captor.getValue().isEnabled());
    }

    @Test
    void testUpdateUserNotFound() {
        String email = "correo@correo.com";
        doThrow(new EntityNotFoundException("usuario no encontado"))
                .when(userSearchMethods).findByEmail(anyString());
        assertThrows(EntityNotFoundException.class, () -> {
            administrativeService.updateEnable(email, true);
        });
    }

    @Test
    void testUpdateIsEnable() {
        boolean enable = false;
        String email = "correo@correo.com";
        doThrow(new IllegalArgumentException("Usuario ya desactivado")).when(userValidationHelper)
                .isEnabled(anyString());

        assertThrows(IllegalArgumentException.class, () -> {
            administrativeService.updateEnable(email, enable);
        });
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateRole() {
        String email = "correo@correo.com";
        Role role = Role.ADMIN;

        when(userSearchMethods.findByEmail(anyString())).thenReturn(DataProviderAdministrative.userMock());
        administrativeService.updateRole(email, role);
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(captor.capture());
        assertEquals(role, captor.getValue().getRole());
    }

    @Test
    void testUpdateRoleUserNotFound() {
        String email = "correo@correo.com";
        Role role = Role.ADMIN;
        doThrow(new EntityNotFoundException("Usuario no encontrado")).when(userSearchMethods).findByEmail(anyString());
        assertThrows(EntityNotFoundException.class, () -> {
            administrativeService.updateRole(email, role);
        });
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdatePassword() {
        String email = "correo@correo.com";
        String newPassword = "passwordPlano";
        String passwordEncriptada = "passwordEncriptada";

        when(userSearchMethods.findByEmail(anyString())).thenReturn(DataProviderAdministrative.userMock());
        when(passwordEncoder.encode(newPassword)).thenReturn(passwordEncriptada);

        administrativeService.updatePassword(email, newPassword);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals(passwordEncriptada, captor.getValue().getPassword());
    }

    @Test
    void testUpdatePasswordUserNotFound() {
        String email = "correo@correo.com";
        String newPassword = "passwordPlano";

        doThrow(new EntityNotFoundException("Usuario no encontrado"))
                .when(userSearchMethods).findByEmail(anyString());

        assertThrows(EntityNotFoundException.class, () -> {
            administrativeService.updatePassword(email, newPassword);
        });
        verify(userRepository, never()).save(any(User.class));
    }
}