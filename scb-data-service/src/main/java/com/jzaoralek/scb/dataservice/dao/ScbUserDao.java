package com.jzaoralek.scb.dataservice.dao;

import java.util.List;
import java.util.UUID;

import com.jzaoralek.scb.dataservice.domain.ScbUser;

public interface ScbUserDao {

	List<ScbUser> getAll();
	ScbUser getByUuid(UUID uuid);
	void insert(ScbUser scbUser);
	void update(ScbUser scbUser);
	void delete(ScbUser scbUser);
}
