package com.jzaoralek.scb.dataservice.utils;

import org.junit.Assert;
import org.junit.Test;

public class PaymentUtilsTest {

	private static final int VARSYMBOL_YEAR = 2018;
	private static final int VARSYMBOL_CORE = 154;
	private static final int VARSYMBOL_SEMESTER = 1;
	
	@Test
	public void testBuildCoursePaymentVarsymbol() {
		String varsymbol = PaymentUtils.buildCoursePaymentVarsymbol(VARSYMBOL_YEAR, VARSYMBOL_SEMESTER, VARSYMBOL_CORE);
		Assert.assertNotNull(varsymbol);
		Assert.assertTrue(varsymbol.equals(VARSYMBOL_YEAR+""+VARSYMBOL_SEMESTER+""+VARSYMBOL_CORE));
	}
	
	
	@Test
	public void testGetVarsymbolCore() {
		String varsymbol = PaymentUtils.buildCoursePaymentVarsymbol(VARSYMBOL_YEAR, VARSYMBOL_SEMESTER, VARSYMBOL_CORE);
		String varsymbolCore = PaymentUtils.getVarsymbolCore(varsymbol);
		Assert.assertNotNull(varsymbolCore);
		Assert.assertTrue(varsymbolCore.equals(String.valueOf(VARSYMBOL_CORE)));
	}
}
