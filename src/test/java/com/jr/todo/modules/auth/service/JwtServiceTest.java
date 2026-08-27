package com.jr.todo.modules.auth.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Base64;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;
import com.jr.todo.DataProviderAuth;
import com.jr.todo.DataProviderUser;
import com.jr.todo.enums.Role;
import com.jr.todo.modules.user.entity.User;

public class JwtServiceTest {

    private JwtService jwtService;
    private static final String SECRET_KEY = Base64.getEncoder()
            .encodeToString("secretkeydepruenbaparalarealizaciondetest12122".getBytes());
    private static final Long EXPIRATION = 86400000L;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "SECRET_KEY", SECRET_KEY);
        ReflectionTestUtils.setField(jwtService, "EXPIRATION", EXPIRATION);
    }

    @Test
    void testGetToken() {
        UserDetails userDetails = DataProviderUser.UserMock();
        String result = jwtService.getToken(userDetails);

        assertNotNull(result);
        assertEquals("usuarioprueba@correo.com", jwtService.getUsernameFromToken(result));
        assertTrue(jwtService.isTokenValid(result, userDetails));
    }

    @Test
    void testGetUsernameFromToken() {
        UserDetails user = DataProviderAuth.userMock();
        String token = jwtService.getToken(user);

        String userName = jwtService.getUsernameFromToken(token);
        assertEquals("correo@correo.com", userName);
    }

    @Test
    void testGetJtiFromToken() {
        UserDetails user = DataProviderAuth.userMock();
        String token = jwtService.getToken(user);
        String result = jwtService.getJtiFromToken(token);
        assertNotNull(result);
        assertFalse(result.isBlank());
        assertDoesNotThrow(() -> UUID.fromString(result));
    }

    @Test
    void testIsTokenValid() {
        UserDetails userDetails = DataProviderUser.UserMock();
        String token = jwtService.getToken(userDetails);
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void testIsTokenValidUserDistint() {
        UserDetails userDetails1 = DataProviderUser.UserMock();
        String token = jwtService.getToken(userDetails1);
        UserDetails userDetails2 = DataProviderUser.UserMock();
        userDetails2 = new User(2L, "user", "user", "correo2@correo.com" , "user2", "pass", true, Role.USER, LocalDate.now(), null, null);
        assertFalse(jwtService.isTokenValid(token, userDetails2));
    }

}
