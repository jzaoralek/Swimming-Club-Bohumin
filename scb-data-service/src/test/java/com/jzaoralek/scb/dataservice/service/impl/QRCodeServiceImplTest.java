package com.jzaoralek.scb.dataservice.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.jzaoralek.scb.dataservice.BaseTestCase;
import com.jzaoralek.scb.dataservice.domain.PaymentInstruction;
import com.jzaoralek.scb.dataservice.domain.Course.CourseType;
import com.jzaoralek.scb.dataservice.service.QRCodeService;

public class QRCodeServiceImplTest extends BaseTestCase  {
	
	@Autowired
	private QRCodeService qrCodeService;
	
	@Test
	public void testGetPaymentQRCode() {
		PaymentInstruction paymentInstr = new PaymentInstruction("Dustin Henderson"
				, null
				, "Kurz Stranger Things"
				, 1000
				, 1
				, "34567"
				, "670100-2213791191/6210" // 1472527163/0800 
				, UUID.randomUUID()
				, CourseType.STANDARD);
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date()); 
		cal.add(Calendar.DATE, 1);

		byte[] qrCodeBytes = qrCodeService.getPaymentQRCode(paymentInstr, cal.getTime());
		
		Assert.assertNotNull(qrCodeBytes);
		
	}
}
