package com.sportologic.sprtadmin.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PasswordGeneratorTest {

    @Test
    public void testGenerate() {
        String password = PasswordGenerator.generate();
        Assertions.assertNotNull(password);
        Assertions.assertTrue(PasswordGenerator.PASSWORD_LENGTH == password.length());
    }
}
