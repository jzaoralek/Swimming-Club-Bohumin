package com.jzaoralek.scb.ui.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.javatuples.Pair;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.CollectionUtils;

import com.jzaoralek.scb.ui.common.WebConstants;

public class WebUtilsTest {

	@Test
	public void testValidateEmailList() {
		String validEmailAddr1 = "a.a@seznam.cz";
		String validEmailAddr2 = "a.b@seznam.cz";
		String validEmailAddr3 = " a.c@seznam.cz ";
		String invalidEmailAddr1 = "a.seznam.cz";
		String invalidEmailAddr2 = "aasasas";
		String invalidEmailAddr3 = " aasasasasa ";
		
		String allValidEmailAddrList1 = validEmailAddr1 + WebConstants.EMAIL_LIST_SEPARATOR + validEmailAddr2 + WebConstants.EMAIL_LIST_SEPARATOR + validEmailAddr3;
		String oneInvalidEmailAddrList1 = validEmailAddr1 + WebConstants.EMAIL_LIST_SEPARATOR + validEmailAddr2 + WebConstants.EMAIL_LIST_SEPARATOR + invalidEmailAddr1;
		String oneValidEmailAddrList1 = validEmailAddr1 + WebConstants.EMAIL_LIST_SEPARATOR + invalidEmailAddr2 + WebConstants.EMAIL_LIST_SEPARATOR + invalidEmailAddr3;
		String allInvalidEmailAddrList1 = invalidEmailAddr1 + WebConstants.EMAIL_LIST_SEPARATOR + invalidEmailAddr2 + WebConstants.EMAIL_LIST_SEPARATOR + invalidEmailAddr3;
		
		// empty input
		Pair<List<String>,List<String>> validResult = WebUtils.validateEmailList(null);
		Assert.assertNull(validResult);
		
		// all email address valid
		validResult = WebUtils.validateEmailList(allValidEmailAddrList1);
		Assert.assertTrue(validResult != null 
				&& !CollectionUtils.isEmpty(validResult.getValue0()) 
				&& CollectionUtils.isEmpty(validResult.getValue1())
				&& validResult.getValue0().size() == 3);
		
		// one email address invalid
		validResult = WebUtils.validateEmailList(oneInvalidEmailAddrList1);
		Assert.assertTrue(validResult != null 
				&& !CollectionUtils.isEmpty(validResult.getValue0()) 
				&& !CollectionUtils.isEmpty(validResult.getValue1())
				&& validResult.getValue0().size() == 2
				&& validResult.getValue1().size() == 1);
		
		// all email address invalid
		validResult = WebUtils.validateEmailList(allInvalidEmailAddrList1);
		Assert.assertTrue(validResult != null 
				&& CollectionUtils.isEmpty(validResult.getValue0()) 
				&& !CollectionUtils.isEmpty(validResult.getValue1())
				&& validResult.getValue1().size() == 3);
		
		// one email address valid
		validResult = WebUtils.validateEmailList(oneValidEmailAddrList1);
		Assert.assertTrue(validResult != null 
				&& !CollectionUtils.isEmpty(validResult.getValue0()) 
				&& !CollectionUtils.isEmpty(validResult.getValue1())
				&& validResult.getValue0().size() == 1
				&& validResult.getValue1().size() == 2);
	}
	
	@Test
	public void testParseRcDatePart() {
		String TOO_LONG_RC = "11223344";
		try {
			WebUtils.parseRcDatePart(TOO_LONG_RC);			
			Assert.fail("Should fail on IllegalArgumentException.");
		} catch (Exception e) {
			// expected
		}
		
		String WRONG_FORMAT_RC = "11223b";
		try {
			WebUtils.parseRcDatePart(WRONG_FORMAT_RC);			
			Assert.fail("Should fail on IllegalArgumentException.");
		} catch (Exception e) {
			// expected
		}
		
		String WRONG_DATE_RC = "11223b";
		try {
			WebUtils.parseRcDatePart(WRONG_DATE_RC);			
			Assert.fail("Should fail on IllegalArgumentException.");
		} catch (Exception e) {
			// expected
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
        try {
        	// 1981-05-04
        	Date expDate = sdf.parse("1981-05-04");
			assertEqualsDates(expDate, WebUtils.parseRcDatePart("810504").getValue1());
			assertEqualsDates(expDate, WebUtils.parseRcDatePart("812504").getValue1());
			
			assertEqualsDates(expDate, WebUtils.parseRcDatePart("815504").getValue1());
			assertEqualsDates(expDate, WebUtils.parseRcDatePart("817504").getValue1());
			
			// 1981-12-04
			expDate = sdf.parse("1981-12-04");			
			assertEqualsDates(expDate, WebUtils.parseRcDatePart("811204").getValue1());
			assertEqualsDates(expDate, WebUtils.parseRcDatePart("813204").getValue1());
			assertEqualsDates(expDate, WebUtils.parseRcDatePart("811204").getValue1());
			assertEqualsDates(expDate, WebUtils.parseRcDatePart("816204").getValue1());
			assertEqualsDates(expDate, WebUtils.parseRcDatePart("818204").getValue1());
			
			// 1981-01-04
			expDate = sdf.parse("1981-01-04");
			assertEqualsDates(expDate, WebUtils.parseRcDatePart("815104").getValue1());
			assertEqualsDates(expDate, WebUtils.parseRcDatePart("817104").getValue1());
			
			// 2000-01-01
			expDate = sdf.parse("2000-01-01");
			assertEqualsDates(expDate, WebUtils.parseRcDatePart("000101").getValue1());
			assertEqualsDates(expDate, WebUtils.parseRcDatePart("002101").getValue1());
			assertEqualsDates(expDate, WebUtils.parseRcDatePart("005101").getValue1());
			assertEqualsDates(expDate, WebUtils.parseRcDatePart("007101").getValue1());
			
			// 2000-12-31
			expDate = sdf.parse("2000-12-31");
			assertEqualsDates(expDate, WebUtils.parseRcDatePart("001231").getValue1());
			assertEqualsDates(expDate, WebUtils.parseRcDatePart("003231").getValue1());
			assertEqualsDates(expDate, WebUtils.parseRcDatePart("006231").getValue1());
			assertEqualsDates(expDate, WebUtils.parseRcDatePart("008231").getValue1());
		} catch (ParseException e) {
			Assert.fail();
		}
	}
	
	protected void assertEqualsDates(Date date1, Date date2) {
		Assert.assertTrue(date1.compareTo(date1) == 0);
	}
}