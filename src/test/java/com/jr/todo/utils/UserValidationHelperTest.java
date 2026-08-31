package com.jr.todo.utils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.DisabledException;
import com.jr.todo.modules.user.repository.UserRepository;
import com.jr.todo.util.UserValidationHelper;

@ExtendWith(MockitoExtension.class)
public class UserValidationHelperTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserValidationHelper userValidationHelper;

    @Test
    void testValidateUsername() {
        String userna = "prueba";
        userValidationHelper.validateUsername(userna);
        verify(userRepository).existsByName(userna);
    }

    @Test
    void testValidateUsernameErrorExist() {
        String username = "prueba";
        when(userRepository.existsByName(username)).thenReturn(true);
        assertThrows(IllegalArgumentException.class,
                () -> userValidationHelper.validateUsername(username));
    }

    @Test
    void testValidateUsernameErrorNotExist() {
        String username = "prueba";
        when(userRepository.existsByName(username)).thenReturn(false);
        assertDoesNotThrow(() -> userValidationHelper.validateUsername(username));
    }

    @Test
    void testValidateEmail() {
        String email = "prueba@correo.com";
        userValidationHelper.validateEmail(email);
        verify(userRepository).existByEmail(email);
    }

    @Test
    void testValidateEmailErrorExist() {
        String email = "prueba@correo.com";

        when(userRepository.existByEmail(email)).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> {
            userValidationHelper.validateEmail(email);
        });
    }

    @Test
    void testIsEnabledErrorDisabled() {
        String email = "prueba@correo.com";
        when(userRepository.isUserEnabled(email)).thenReturn(false);
        assertThrows(DisabledException.class,
                () -> userValidationHelper.isEnabled(email));
        verify(userRepository).isUserEnabled(email);
    }

    @Test
    void testIsEnabledEnabled() {
        String email = "prueba@correo.com";
        when(userRepository.isUserEnabled(email)).thenReturn(true);
        assertDoesNotThrow(() -> userValidationHelper.isEnabled(email));
    }

}