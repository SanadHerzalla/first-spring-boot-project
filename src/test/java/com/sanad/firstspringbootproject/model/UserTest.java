package com.sanad.firstspringbootproject.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void shouldCreateUser() {
        User user = new User("sanad", "password", Role.USER);

        assertEquals("sanad", user.getUsername());
        assertEquals("password", user.getPassword());
        assertEquals(Role.USER, user.getRole());
    }

    @Test
    void shouldUpdateUser() {
        User user = new User("sanad", "password", Role.USER);

        user.setUsername("admin");
        user.setPassword("new-password");
        user.setRole(Role.ADMIN);

        assertEquals("admin", user.getUsername());
        assertEquals("new-password", user.getPassword());
        assertEquals(Role.ADMIN, user.getRole());
    }
}
