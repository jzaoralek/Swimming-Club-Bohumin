package com.jzaoralek.scb.dataservice.dao;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.jzaoralek.scb.dataservice.domain.MailSend;

public interface MailSendDao {

	void insert(MailSend mail);
	List<MailSend> getByDateInterval(Date from, Date to);
	MailSend getByUuid(UUID uuid);
	void delete(List<MailSend> mailSendList);
}
