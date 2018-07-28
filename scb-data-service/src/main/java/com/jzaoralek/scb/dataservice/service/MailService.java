package com.jzaoralek.scb.dataservice.service;

import java.util.List;

import com.jzaoralek.scb.dataservice.domain.Attachment;

public interface MailService {
	void sendMail(String to, String subject, String text, List<Attachment> attachmentList);
}