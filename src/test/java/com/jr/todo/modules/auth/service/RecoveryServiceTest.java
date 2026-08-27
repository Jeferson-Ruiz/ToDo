package com.jr.todo.modules.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.jr.todo.DataProviderUser;
import com.jr.todo.modules.auth.dto.ResetPasswordDto;
import com.jr.todo.modules.auth.entity.AccountRecoveryToken;
import com.jr.todo.modules.auth.helpers.SendRecoveryAccount;
import com.jr.todo.modules.auth.repository.AccountRecoveryTokenRepository;
import com.jr.todo.modules.user.entity.User;
import com.jr.todo.modules.user.repository.UserRepository;
import com.jr.todo.util.UserSearchMethods;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class RecoveryServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserSearchMethods userSearchMethods;

    @Mock
    private AccountRecoveryTokenRepository accountRecoveryTokenRepository;

    @Mock
    private SendRecoveryAccount sendRecoveryAccount;

    @InjectMocks
    private RecoveryService recoveryService;

    @Test
    void testInitiateRecovery() {
        String email = "correo@correo.com";
        User user = DataProviderUser.UserMock();
        when(userSearchMethods.findByEmail(anyString())).thenReturn(user);
        when(accountRecoveryTokenRepository.findByUser(any())).thenReturn(Optional.empty());

        String result = recoveryService.initiateRecovery(email);

        assertEquals("Email de recuperación enviado", result);
        verify(accountRecoveryTokenRepository).findByUser(user);
        verify(userSearchMethods).findByEmail(email);
        verify(accountRecoveryTokenRepository, never()).delete(any());
        verify(sendRecoveryAccount).sendEmailActivation(user);
    }

    @Test
    void testInitiateRecoveryTokenDelete() {
        String email = "correo@correo.com";
        User user = DataProviderUser.UserMock();

        AccountRecoveryToken token = new AccountRecoveryToken(1L, "tok", user, LocalDateTime.now().plusHours(2), false);

        when(userSearchMethods.findByEmail(anyString())).thenReturn(user);
        when(accountRecoveryTokenRepository.findByUser(any())).thenReturn(Optional.of(token));

        String result = recoveryService.initiateRecovery(email);

        assertEquals("Email de recuperación enviado", result);
        verify(accountRecoveryTokenRepository).findByUser(user);
        verify(accountRecoveryTokenRepository).delete(token);
        verify(sendRecoveryAccount).sendEmailActivation(user);
        verify(userSearchMethods).findByEmail(email);
    }

    @Test
    void testInitiateRecoveryUserNotFound() {
        String email = "correo@correo.com";
        when(userSearchMethods.findByEmail(anyString())).thenThrow(new EntityNotFoundException());

        assertThrows(EntityNotFoundException.class, () -> recoveryService.initiateRecovery(email));

        verify(accountRecoveryTokenRepository, never()).findByUser(any());
        verify(sendRecoveryAccount, never()).sendEmailActivation(any());
    }

    @Test
    void testUpdatePassword() {
        String tokenStr = "tok";
        User user = DataProviderUser.UserMock();
        AccountRecoveryToken token = new AccountRecoveryToken(1L, tokenStr, user,
                LocalDateTime.now().plusHours(2), false);
        ResetPasswordDto dto = new ResetPasswordDto("newPass", "newPass");

        when(accountRecoveryTokenRepository.findByToken(anyString())).thenReturn(Optional.of(token));
        when(passwordEncoder.encode("newPass")).thenReturn("encodedPass");

        String result = recoveryService.updatePassword(tokenStr, dto);

        assertEquals("Contraseña actualizada correctamente", result);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertEquals("encodedPass", userCaptor.getValue().getPassword());

        ArgumentCaptor<AccountRecoveryToken> tokenCaptor = ArgumentCaptor.forClass(AccountRecoveryToken.class);
        verify(accountRecoveryTokenRepository).save(tokenCaptor.capture());
        assertTrue(tokenCaptor.getValue().isUsed());

        verify(passwordEncoder).encode("newPass");
        verify(accountRecoveryTokenRepository).findByToken(tokenStr);
    }

    @Test
    void testUpdatePasswordTokenNotFound() {
        ResetPasswordDto dto = new ResetPasswordDto("newPass", "newPass");
        when(accountRecoveryTokenRepository.findByToken(anyString())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> recoveryService.updatePassword("invalido", dto));

        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any());
        verify(accountRecoveryTokenRepository, never()).save(any());
    }

    @Test
    void testUpdatePasswordTokenAlreadyUsed() {
        User user = DataProviderUser.UserMock();
        AccountRecoveryToken token = new AccountRecoveryToken(1L, "tok", user, LocalDateTime.now().plusHours(2), true);
        ResetPasswordDto dto = new ResetPasswordDto("newPass", "newPass");

        when(accountRecoveryTokenRepository.findByToken(anyString())).thenReturn(Optional.of(token));

        assertThrows(IllegalStateException.class, () -> recoveryService.updatePassword("tok", dto));

        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any());
        verify(accountRecoveryTokenRepository, never()).save(any());
    }

    @Test
    void testUpdatePasswordTokenExpired() {
        User user = DataProviderUser.UserMock();
        AccountRecoveryToken token = new AccountRecoveryToken(1L, "tok", user, LocalDateTime.now().minusHours(1),
                false);
        ResetPasswordDto dto = new ResetPasswordDto("newPass", "newPass");

        when(accountRecoveryTokenRepository.findByToken(anyString())).thenReturn(Optional.of(token));

        assertThrows(IllegalStateException.class, () -> recoveryService.updatePassword("tok", dto));

        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any());
        verify(accountRecoveryTokenRepository, never()).save(any());
    }

    @Test
    void testUpdatePasswordPasswordsDoNotMatch() {
        User user = DataProviderUser.UserMock();
        AccountRecoveryToken token = new AccountRecoveryToken(1L, "tok", user, LocalDateTime.now().plusHours(2), false);
        ResetPasswordDto dto = new ResetPasswordDto("pass1", "pass2");

        when(accountRecoveryTokenRepository.findByToken(anyString())).thenReturn(Optional.of(token));

        assertThrows(IllegalArgumentException.class, () -> recoveryService.updatePassword("tok", dto));

        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any());
        verify(accountRecoveryTokenRepository, never()).save(any());
    }

}
