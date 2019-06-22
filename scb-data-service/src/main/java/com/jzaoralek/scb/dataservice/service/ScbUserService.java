package com.jzaoralek.scb.dataservice.service;

import java.util.List;
import java.util.UUID;

import com.jzaoralek.scb.dataservice.domain.Contact;
import com.jzaoralek.scb.dataservice.domain.ScbUser;
import com.jzaoralek.scb.dataservice.exception.ScbValidationException;
import com.jzaoralek.scb.dataservice.utils.vo.Cover;

public interface ScbUserService {

	void changePassword(ScbUser scbUser, Cover<char[]> password);
	List<ScbUser> getAll();
	ScbUser getByUuid(UUID uuid);
	ScbUser store(ScbUser user) throws ScbValidationException;
	void delete(UUID uuid) throws ScbValidationException;
	ScbUser getByUsername(String username);
	List<Contact> getContactByEmail(String email);
	List<String> getEmailAll();
}
