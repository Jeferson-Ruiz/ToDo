package com.jr.todo.modules.users;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
        long id = 1L;
        boolean enable = false;
        when(userSearchMethods.findUserById(id)).thenReturn(DataProviderAdministrative.userMock());
        administrativeService.updateEnable(id, enable);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals(enable, captor.getValue().isEnabled());
    }

    @Test
    void testUpdateUserNotFound() {
        doThrow(new EntityNotFoundException("usuario no encontado"))
                .when(userSearchMethods).findUserById(anyLong());
        assertThrows(EntityNotFoundException.class, () -> {
            administrativeService.updateEnable(1L, true);
        });
    }

    @Test
    void testUpdateIsEnable() {
        boolean enable = true;
        long id = 1L;
        User user = DataProviderAdministrative.userMock();
        when(userSearchMethods.findUserById(id)).thenReturn(user);
        administrativeService.updateEnable(id, enable);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateRole() {
        long id = 1L;
        Role role = Role.ADMIN;

        when(userSearchMethods.findUserById(anyLong())).thenReturn(DataProviderAdministrative.userMock());
        administrativeService.updateRole(id, role);
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(captor.capture());
        assertEquals(role, captor.getValue().getRole());
    }

    @Test
    void testUpdateRoleUserNotFound() {
        long id = 1L;
        Role role = Role.ADMIN;
        doThrow(new EntityNotFoundException("Usuario no encontrado")).when(userSearchMethods).findUserById(anyLong());
        assertThrows(EntityNotFoundException.class, () -> {
            administrativeService.updateRole(id, role);
        });
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdatePassword() {
        long id = 1L;
        String newPassword = "passwordPlano";
        String passwordEncriptada = "passwordEncriptada";

        when(userSearchMethods.findUserById(anyLong())).thenReturn(DataProviderAdministrative.userMock());
        when(passwordEncoder.encode(newPassword)).thenReturn(passwordEncriptada);

        administrativeService.updatePassword(id, newPassword);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals(passwordEncriptada, captor.getValue().getPassword());
    }

    @Test
    void testUpdatePasswordUserNotFound() {
        long id = 1L;
        String newPassword = "passwordPlano";

        doThrow(new EntityNotFoundException("Usuario no encontrado"))
                .when(userSearchMethods).findUserById(anyLong());

        assertThrows(EntityNotFoundException.class, () -> {
            administrativeService.updatePassword(id, newPassword);
        });
        verify(userRepository, never()).save(any(User.class));
    }
}