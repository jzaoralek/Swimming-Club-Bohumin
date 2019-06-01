package com.jzaoralek.scb.ui.common.utils;

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
		String validEmailAddr2 = "a.a@seznam.cz";
		String validEmailAddr3 = " a.a@seznam.cz ";
		String invalidEmailAddr1 = "a.seznam.cz";
		String invalidEmailAddr2 = "aasasas";
		String invalidEmailAddr3 = " aasasas ";
		
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
}
