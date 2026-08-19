package com.jr.todo.utils;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.jr.todo.DataProviderCategory;
import com.jr.todo.modules.user.entity.User;
import com.jr.todo.modules.user.repository.UserRepository;
import com.jr.todo.util.UserSearchMethods;
import jakarta.persistence.EntityNotFoundException;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserSearchMethodsTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserSearchMethods userSearchMethods;

    @Test
    void testFindByEmail() {
        String email = "pedro@correo.com";
        User expect = DataProviderCategory.userMock();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(expect));
        User current = userSearchMethods.findByEmail(email);
        assertEquals(expect, current);
    }

    @Test
    void testFindByEmailError() {
        assertThrows(EntityNotFoundException.class, () -> {
            userSearchMethods.findByEmail("");
        });
    }

}
