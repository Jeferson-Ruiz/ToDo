package com.jr.todo.modules.users;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.jr.todo.DataProviderUser;
import com.jr.todo.modules.user.entity.User;
import com.jr.todo.modules.user.repository.UserRepository;
import com.jr.todo.modules.user.service.UserService;
import com.jr.todo.util.UserSearchMethods;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserSearchMethods userSearchMethods;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void testUpdatePasswod() {
        String email = "prueba@correo.com";
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";

        when(userSearchMethods.findByEmail(anyString())).thenReturn(DataProviderUser.UserMock());
        when(passwordEncoder.matches(oldPassword, "password1")).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn("nuevoHash");

        userService.updatePasswod(email, oldPassword, newPassword);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertNotNull(captor.getValue());
        assertEquals("nuevoHash", captor.getValue().getPassword());
    }

    @Test
    void updatePasswodUserNotFound() {
        String email = "prueba@correo.com";
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";
        when(userSearchMethods.findByEmail(anyString()))
                .thenThrow(new EntityNotFoundException("Usuario no encontrado"));

        assertThrows(EntityNotFoundException.class, () -> {
            userService.updatePasswod(email, oldPassword, newPassword);
        });

        verify(userRepository, never()).save(any());
    }

    @Test
    void updatePasswodUserNotMatches() {
        String email = "prueba@correo.com";
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";

        when(userSearchMethods.findByEmail(anyString())).thenReturn(DataProviderUser.UserMock());
        when(passwordEncoder.matches(oldPassword, "password1")).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> {
            userService.updatePasswod(email, oldPassword, newPassword);
        });

        verify(userRepository, never()).save(any());
    }

}
