package com.sportologic.sprtadmin.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StringUtilsTest {

    @Test
    public void testBuildCustId() {
        String customerName = "Plavecký klub Bohumín";
        String customerId = StringUtils.buildCustId(customerName);
        Assertions.assertEquals("plaveckyklubbohumin", customerId);

        customerName = "Kosatky Karviná";
        customerId = StringUtils.buildCustId(customerName);
        Assertions.assertEquals("kosatkykarvina", customerId);

        customerName = "Plavecký Kemp Květoslava Svobody";
        customerId = StringUtils.buildCustId(customerName);
        Assertions.assertEquals("plaveckykempkvetoslavasvobody", customerId);

        customerName = "Žraloci Znojmo";
        customerId = StringUtils.buildCustId(customerName);
        Assertions.assertEquals("zralociznojmo", customerId);
    }
}
