package com.sportologic.sprtadmin.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StringUtilsTest {

    @Test
    public void testBuildCustId() {
        String customerName = "Plavecký klub Bohumín";
        String customerId = SprtAdminUtils.normToLowerCaseWithoutCZChars(customerName);
        Assertions.assertEquals("plaveckyklubbohumin", customerId);

        customerName = "Kosatky Karviná";
        customerId = SprtAdminUtils.normToLowerCaseWithoutCZChars(customerName);
        Assertions.assertEquals("kosatkykarvina", customerId);

        customerName = "Plavecký Kemp Květoslava Svobody";
        customerId = SprtAdminUtils.normToLowerCaseWithoutCZChars(customerName);
        Assertions.assertEquals("plaveckykempkvetoslavasvobody", customerId);

        customerName = "Žraloci Znojmo";
        customerId = SprtAdminUtils.normToLowerCaseWithoutCZChars(customerName);
        Assertions.assertEquals("zralociznojmo", customerId);
    }
}
