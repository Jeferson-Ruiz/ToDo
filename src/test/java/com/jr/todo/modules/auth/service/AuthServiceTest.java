package com.jr.todo.modules.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.jr.todo.DataProviderAuth;
import com.jr.todo.dto.AuthRequest;
import com.jr.todo.dto.AuthResponse;
import com.jr.todo.dto.UserCreateDto;
import com.jr.todo.modules.auth.entity.AccountActivationToken;
import com.jr.todo.modules.auth.helpers.SendActivationEmail;
import com.jr.todo.modules.auth.repository.AccountActivationTokenRepository;
import com.jr.todo.modules.user.entity.User;
import com.jr.todo.modules.user.repository.UserRepository;
import com.jr.todo.util.UserSearchMethods;
import com.jr.todo.util.UserValidationHelper;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private IJwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ITokenBlacklistService tokenBlacklistService;

    @Mock
    private UserValidationHelper userValidation;

    @Mock
    private UserSearchMethods userSearchMethods;

    @Mock
    private AccountActivationTokenRepository accountActivationTokenRepository;

    @Mock
    private SendActivationEmail sendActivationEmail;

    @InjectMocks
    private AuthService authService;

    @Test
    void testLogin() {
        AuthRequest authRequest = DataProviderAuth.AuthRequesDtoMock();
        User user = DataProviderAuth.userMock();

        when(userSearchMethods.findByEmail(anyString())).thenReturn(user);
        when(passwordEncoder.matches("password", "password")).thenReturn(true);
        when(jwtService.getToken(user)).thenReturn("jwt.prueba");

        AuthResponse result = authService.login(authRequest);

        assertEquals("jwt.prueba", result.token());
        verify(userValidation).isEnabled("correo@correo.com");
    }

    @Test
    void testLoginUserNotFound() {
        AuthRequest request = DataProviderAuth.AuthRequesDtoMock();
        when(userSearchMethods.findByEmail(anyString()))
                .thenThrow(new EntityNotFoundException("usuario no encontrado"));

        assertThrows(EntityNotFoundException.class, () -> {
            authService.login(request);
        });
    }

    @Test
    void testLoginErroPassword() {
        AuthRequest authRequest = DataProviderAuth.AuthRequesDtoMock();
        when(userSearchMethods.findByEmail(anyString())).thenReturn(DataProviderAuth.userMock());
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authService.login(authRequest));
    }

    @Test
    void testLoginIsDisable() {
        AuthRequest authRequest = DataProviderAuth.AuthRequesDtoMock();
        when(userSearchMethods.findByEmail(anyString())).thenReturn(DataProviderAuth.userMock());
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        doThrow(new IllegalArgumentException("Usuario desactivado")).when(userValidation)
                .isEnabled(anyString());

        assertThrows(IllegalArgumentException.class, () -> authService.login(authRequest));
        verify(jwtService, never()).getToken(any());
    }

    @Test
    void testRegister() {
        UserCreateDto userDto = DataProviderAuth.userCreateDtoMock();
        when(passwordEncoder.encode(anyString())).thenReturn("password123");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        String result = authService.register(userDto);

        assertEquals("Registro exitoso, revisa tu email para activar tu cuenta", result);
        verify(userValidation).validateUsername(userDto.username());
        verify(userValidation).validateEmail(userDto.email());
        verify(passwordEncoder).encode(userDto.password());
        verify(userRepository).save(any(User.class));
        verify(sendActivationEmail).sendActivationEmail(any(User.class));
    }

    @Test
    void testRegisterUsernameExist() {
        UserCreateDto userDto = DataProviderAuth.userCreateDtoMock();

        doThrow(new IllegalArgumentException("username ya registrado")).when(userValidation)
                .validateUsername(anyString());

        assertThrows(IllegalArgumentException.class, () -> authService.register(userDto));

    }

    @Test
    void testRegisterValidateEmail() {
        UserCreateDto userDto = DataProviderAuth.userCreateDtoMock();

        doThrow(new IllegalArgumentException("email ya registrado")).when(userValidation).validateEmail(anyString());

        assertThrows(IllegalArgumentException.class, () -> authService.register(userDto));
    }

    @Test
    void testLogout() {
        String authHeader = "Bearer token.prueba";
        authService.logout(authHeader);
        verify(tokenBlacklistService).blackListToken("token.prueba");
    }

    @Test
    void testLogoutAuthNull() {
        authService.logout(null);
        verify(tokenBlacklistService, never()).blackListToken(anyString());
    }

    @Test
    void testLogoutNotBearer() {
        String authHeader = "token.prueba";
        authService.logout(authHeader);
        verify(tokenBlacklistService, never()).blackListToken(anyString());
    }

    @Test
    void testActivateAccountExitoso() {
        AccountActivationToken activationToken = DataProviderAuth.activationToken();
        when(accountActivationTokenRepository.findByToken("prueba.token"))
                .thenReturn(Optional.of(activationToken));

        authService.activateAccount("prueba.token");

        assertTrue(activationToken.getUser().isEnabled());
        assertTrue(activationToken.isUsed());
        verify(userRepository).save(activationToken.getUser());
        verify(accountActivationTokenRepository).save(activationToken);
    }

    @Test
    void testActivateAccountTokenNoEncontrado() {
        when(accountActivationTokenRepository.findByToken(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> authService.activateAccount("invalido"));
    }

    @Test
    void testActivateAccountTokenYaUsado() {
        AccountActivationToken activationToken = DataProviderAuth.activationToken();
        activationToken.setUsed(true);
        when(accountActivationTokenRepository.findByToken(anyString()))
                .thenReturn(Optional.of(activationToken));

        assertThrows(IllegalStateException.class, () -> authService.activateAccount("prueba.token"));
    }

    @Test
    void testActivateAccountTokenExpirado() {
        AccountActivationToken activationToken = DataProviderAuth.activationToken();
        activationToken.setExpiresAt(java.time.LocalDateTime.now().minusHours(1));
        when(accountActivationTokenRepository.findByToken(anyString()))
                .thenReturn(Optional.of(activationToken));

        assertThrows(IllegalStateException.class, () -> authService.activateAccount("prueba.token"));
    }

    @Test
    void testResendActivationEmail() {
        UserCreateDto userDto = DataProviderAuth.userCreateDtoMock();
        User user = DataProviderAuth.userMock();

        when(userSearchMethods.findByEmail(anyString())).thenReturn(user);
        when(userRepository.isUserEnabled(anyString())).thenReturn(false);
        when(accountActivationTokenRepository.findByUser(any())).thenReturn(Optional.empty());

        String result = authService.resendActivationEmail(userDto);

        verify(accountActivationTokenRepository).findByUser(user);
        assertEquals("Email enviado con exito", result);
        verify(accountActivationTokenRepository, never()).delete(any());
        verify(sendActivationEmail).sendActivationEmail(user);
    }

    @Test
    void testResendActivationEmailTokenDelete() {
        UserCreateDto userDto = DataProviderAuth.userCreateDtoMock();
        User user = DataProviderAuth.userMock();
        AccountActivationToken token = DataProviderAuth.activationToken();

        when(userSearchMethods.findByEmail(anyString())).thenReturn(user);
        when(userRepository.isUserEnabled(anyString())).thenReturn(false);
        when(accountActivationTokenRepository.findByUser(any())).thenReturn(Optional.of(token));

        String result = authService.resendActivationEmail(userDto);

        assertEquals("Email enviado con exito", result);
        verify(accountActivationTokenRepository).delete(token);
        verify(sendActivationEmail).sendActivationEmail(user);
    }

    @Test
    void testResendActivationEmailUserAlreadyAcived() {
        UserCreateDto userDto = DataProviderAuth.userCreateDtoMock();
        User user = DataProviderAuth.userMock();
        when(userSearchMethods.findByEmail(anyString())).thenReturn(user);
        when(userRepository.isUserEnabled(anyString())).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> authService.resendActivationEmail(userDto));

        verify(accountActivationTokenRepository, never()).findByUser(any());
        verify(sendActivationEmail, never()).sendActivationEmail(any());
    }

    @Test
    void resendActivationEmailUserNotFound() {
        UserCreateDto userDto = DataProviderAuth.userCreateDtoMock();
        User user = DataProviderAuth.userMock();

        when(userSearchMethods.findByEmail(anyString())).thenThrow(new EntityNotFoundException());

        assertThrows(EntityNotFoundException.class, () -> authService.resendActivationEmail(userDto));

        verify(accountActivationTokenRepository, never()).findByUser(user);
        verify(sendActivationEmail, never()).sendActivationEmail(user);
    }
}
