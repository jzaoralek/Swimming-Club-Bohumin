package com.jzaoralek.scb.dataservice.service;

public interface MailService {
	void sendMail(String to, String subject, String text, byte[] attachment, String attachmentName);
}