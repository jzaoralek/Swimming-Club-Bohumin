package com.jzaoralek.scb.dataservice.dao;

import java.util.List;
import java.util.UUID;

import com.jzaoralek.scb.dataservice.domain.Contact;

public interface ContactDao {

	Contact getByUuid(UUID uuid);
	Contact getByEmail(String email);
	boolean existsByEmail(String email);
	void insert(Contact contact);
	void update(Contact contact);
	void delete(Contact contact);
	List<String> getEmailAll();
}