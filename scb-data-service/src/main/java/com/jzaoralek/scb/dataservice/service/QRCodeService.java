package com.jzaoralek.scb.dataservice.service;

import java.util.Date;

import com.jzaoralek.scb.dataservice.domain.PaymentInstruction;

/**
 * Service for generating QR Codes.
 *
 */
public interface QRCodeService {

	/**
	 * Generate QR Code for payment instruction.
	 * @param pamentInstr
	 * @param dueDate
	 * @return
	 */
	byte[] getPaymentQRCode(PaymentInstruction paymentInstruction, Date dueDate);
	
	/**
	 * Generate QR Code URL for payment instruction.
	 * @param paymentInstruction
	 * @param dueDate
	 * @return
	 */
	String getPaymentQRCodeUrl(PaymentInstruction paymentInstruction, Date dueDate);
}
