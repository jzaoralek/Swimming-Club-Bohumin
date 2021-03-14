package com.jzaoralek.scb.dataservice.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.jzaoralek.scb.dataservice.domain.Attachment;
import com.jzaoralek.scb.dataservice.domain.Mail;
import com.jzaoralek.scb.dataservice.domain.MailSend;

public interface MailService {
	void sendMail(String to, 
			String cc, 
			String subject, 
			String text, 
			List<Attachment> attachmentList, 
			boolean html,
			boolean audit);
	void sendMail(Mail mail);
	void sendMailBatch(List<Mail> mailList);
	List<MailSend> getMailSendListByCriteria(Date dateFrom, 
						Date dateTo, 
						String mailTo,
						String mailSubject,
						String mailText);
	MailSend getByUuid(UUID uuid);
	void delete(List<MailSend> mailSendList);
	
}