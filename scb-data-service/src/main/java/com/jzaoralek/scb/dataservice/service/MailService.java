package com.jzaoralek.scb.dataservice.service;

import java.util.List;

import com.jzaoralek.scb.dataservice.domain.Attachment;
import com.jzaoralek.scb.dataservice.domain.Mail;

public interface MailService {
	void sendMail(String to, String cc, String subject, String text, List<Attachment> attachmentList, boolean html);
	void sendMail(Mail mail);
	void sendMailBatch(List<Mail> mailList);
}