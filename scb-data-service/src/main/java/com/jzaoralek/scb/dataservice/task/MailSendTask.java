package com.jzaoralek.scb.dataservice.task;

public interface MailSendTask {

	/**
	 * Deleting old send emails.
	 */
	void deleteHistMailSendList();
}
