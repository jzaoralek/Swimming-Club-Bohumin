package com.jzaoralek.scb.web.service;

import java.util.List;

import com.jzaoralek.scb.web.vo.Attachment;
import com.jzaoralek.scb.web.vo.Mail;


public interface MailService {
	void sendMail(String to, String subject, String text, List<Attachment> attachmentList);
	void sendMail(Mail mail);
}