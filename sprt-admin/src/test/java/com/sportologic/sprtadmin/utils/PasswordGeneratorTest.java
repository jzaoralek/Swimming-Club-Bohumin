package com.sportologic.sprtadmin.utils;

import com.sportologic.sprtadmin.utils.shell.PasswordGenerator;
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
