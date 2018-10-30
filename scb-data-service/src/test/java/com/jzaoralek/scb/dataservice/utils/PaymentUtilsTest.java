package com.jzaoralek.scb.dataservice.utils;

import org.junit.Assert;
import org.junit.Test;

public class PaymentUtilsTest {

	private static final int VARSYMBOL_YEAR = 2018;
	private static final int VARSYMBOL_YEAR_WRONG = 2016;
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
		String varsymbolCore = PaymentUtils.getVarsymbolCore(varsymbol, String.valueOf(VARSYMBOL_YEAR));
		Assert.assertNotNull(varsymbolCore);
		Assert.assertTrue(varsymbolCore.equals(String.valueOf(VARSYMBOL_CORE)));
	}
	
	@Test
	public void testGetVarsymbolCore2() {
		String varsymbol = PaymentUtils.buildCoursePaymentVarsymbol(VARSYMBOL_YEAR, VARSYMBOL_SEMESTER, VARSYMBOL_CORE);
		String varsymbolCore = PaymentUtils.getVarsymbolCore(varsymbol, String.valueOf(VARSYMBOL_YEAR_WRONG));
		Assert.assertNull(varsymbolCore);
	}
	
	@Test
	public void testGetVarsymbolCore3() {
		String varsymbol154 = PaymentUtils.buildCoursePaymentVarsymbol(VARSYMBOL_YEAR, VARSYMBOL_SEMESTER, VARSYMBOL_CORE);
		String varsymbolCore154 = PaymentUtils.getVarsymbolCore(varsymbol154, String.valueOf(VARSYMBOL_YEAR));
		Assert.assertNotNull(varsymbolCore154);
		Assert.assertTrue(varsymbolCore154.equals(String.valueOf(VARSYMBOL_CORE)));
		
		String varsymbol54 = PaymentUtils.buildCoursePaymentVarsymbol(VARSYMBOL_YEAR, VARSYMBOL_SEMESTER, 54);
		String varsymbolCore54 = PaymentUtils.getVarsymbolCore(varsymbol54, String.valueOf(VARSYMBOL_YEAR));
		Assert.assertNotNull(varsymbolCore54);
		Assert.assertTrue(varsymbolCore54.equals(String.valueOf(54)));
		
		String varsymbol4 = PaymentUtils.buildCoursePaymentVarsymbol(VARSYMBOL_YEAR, VARSYMBOL_SEMESTER, 4);
		String varsymbolCore4 = PaymentUtils.getVarsymbolCore(varsymbol4, String.valueOf(VARSYMBOL_YEAR));
		Assert.assertNotNull(varsymbolCore4);
		Assert.assertTrue(varsymbolCore4.equals(String.valueOf(4)));
		
		String varsymbol1 = "20181";
		String varsymbolCore1 = PaymentUtils.getVarsymbolCore(varsymbol1, String.valueOf(VARSYMBOL_YEAR));
		Assert.assertNull(varsymbolCore1);
	}
}
