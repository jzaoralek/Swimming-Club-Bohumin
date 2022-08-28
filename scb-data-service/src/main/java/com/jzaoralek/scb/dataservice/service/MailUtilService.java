package com.jzaoralek.scb.dataservice.service;

/**
 * Util service for email messaging.
 *
 */
public interface MailUtilService {

	/**
	 * Building email signature from configuration data.
	 */
	String buildSignatureHtml();
}
