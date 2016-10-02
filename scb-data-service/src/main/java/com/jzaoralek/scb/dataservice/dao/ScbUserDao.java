package com.jzaoralek.scb.dataservice.dao;

import java.util.List;
import java.util.UUID;

import com.jzaoralek.scb.dataservice.domain.ScbUser;
import com.jzaoralek.scb.dataservice.utils.vo.Cover;

public interface ScbUserDao {

	List<ScbUser> getAll();
	ScbUser getByUuid(UUID uuid);
	ScbUser getByUsername(String username);
	void insert(ScbUser scbUser);
	void update(ScbUser scbUser);
	void updatePassword(ScbUser scbUser, Cover<char[]> password);
	void delete(ScbUser scbUser);
	Boolean userWithSameUsernameExists(String username);
}
