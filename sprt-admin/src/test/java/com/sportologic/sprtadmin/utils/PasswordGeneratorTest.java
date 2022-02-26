package com.sportologic.sprtadmin.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

public class PasswordGeneratorTest {

    @Test
    public void testGenerate() {
        String password = PasswordGenerator.generate();
        Assertions.assertNotNull(password);
        Assertions.assertTrue(PasswordGenerator.PASSWORD_LENGTH == password.length());
    }
}
