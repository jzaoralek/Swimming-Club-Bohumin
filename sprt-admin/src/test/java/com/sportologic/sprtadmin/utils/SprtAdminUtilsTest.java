package com.sportologic.sprtadmin.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SprtAdminUtilsTest {

    @Test
    public void testNormToLowerCaseWithoutCZChars() {
        String testStr = "+=''()?:$≤~≥^&*{}'!`¨ů.,-@<>≤ěščřřžžýááíéCORE";
        String result = SprtAdminUtils.normToLowerCaseWithoutCZChars(testStr);
        Assertions.assertNotNull(result);
        Assertions.assertEquals("uescrrzzyaaiecore", result);
    }
}
