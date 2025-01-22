package nl.tudelft.sem.template.user.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InputCheckTest {

    @Test
    void isNull() {
        String s = null;
        assertTrue(InputCheck.isNullOrEmpty(s));
    }

    @Test
    void isEmpty() {
        String s = "";
        assertTrue(InputCheck.isNullOrEmpty(s));
    }

    @Test
    void validUsername() {
        String username = "username";
        assertTrue(InputCheck.isValidUsername(username));
    }

    @Test
    void invalidUsername() {
        String username = "1user";
        assertFalse(InputCheck.isValidUsername(username));
    }
}