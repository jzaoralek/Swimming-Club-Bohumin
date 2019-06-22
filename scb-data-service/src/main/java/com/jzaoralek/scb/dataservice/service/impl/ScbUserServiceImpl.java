package com.jzaoralek.scb.dataservice.service.impl;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.jzaoralek.scb.dataservice.dao.ContactDao;
import com.jzaoralek.scb.dataservice.dao.ScbUserDao;
import com.jzaoralek.scb.dataservice.domain.Contact;
import com.jzaoralek.scb.dataservice.domain.ScbUser;
import com.jzaoralek.scb.dataservice.exception.ScbValidationException;
import com.jzaoralek.scb.dataservice.service.BaseAbstractService;
import com.jzaoralek.scb.dataservice.service.ScbUserService;
import com.jzaoralek.scb.dataservice.utils.SecurityUtils;
import com.jzaoralek.scb.dataservice.utils.vo.Cover;

@Service("scbUserService")
public class ScbUserServiceImpl extends BaseAbstractService implements ScbUserService {

	private static final Logger LOG = LoggerFactory.getLogger(ScbUserServiceImpl.class);

	@Autowired
	private ScbUserDao scbUserDao;

	@Autowired
	private ContactDao contactDao;

	@Override
	public List<ScbUser> getAll() {
		return scbUserDao.getAll();
	}

	@Override
	public ScbUser getByUuid(UUID uuid) {
		return scbUserDao.getByUuid(uuid);
	}
	
	@Override
	public ScbUser getByUsername(String username) {
		return scbUserDao.getByUsername(username);
	}

	@Override
	public ScbUser store(ScbUser user) throws ScbValidationException {
		if (user == null) {
			throw new IllegalArgumentException("user is null");
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("Storing user: " + user);
		}


		boolean insert = user.getUuid() == null;
		fillIdentEntity(user);

		// check if exist user with same username
		ScbUser scbUser = scbUserDao.getByUsername(user.getUsername());
		if ((scbUser == null || !scbUser.getUuid().toString().equals(user.getUuid().toString())) && scbUserDao.userWithSameUsernameExists(user.getUsername())) {
			LOG.warn("User with same username exists, username: " + user.getUsername());
			if (insert) {
				user.setUuid(null);
			}
			throw new ScbValidationException(messageSource.getMessage("msg.validation.warn.usernameWithSameUsernameExists", new Object[] {user.getUsername()}, Locale.getDefault()));
		}

		storeContact(user.getContact());

		if (insert) {
			user.setPassword(SecurityUtils.generatePassword());
			user.setPasswordGenerated(true);
			scbUserDao.insert(user);
		} else {
			scbUserDao.update(user);
		}

		return user;
	}

	@Override
	public void changePassword(ScbUser scbUser, Cover<char[]> password) {
		scbUserDao.updatePassword(scbUser, password);
	}

	@Override
	public void delete(UUID uuid) throws ScbValidationException {
		ScbUser user = scbUserDao.getByUuid(uuid);
		if (user == null) {
			LOG.warn("ScbUser not found, uuid: " + uuid);
			throw new ScbValidationException(messageSource.getMessage("msg.validation.warn.recordNotExistsInDB", null, Locale.getDefault()));
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("Deleting ScbUser: " + user);
		}

		scbUserDao.delete(user);
	}

	private void storeContact(Contact contact) {
		if (contact == null) {
			throw new IllegalArgumentException("contact is null");
		}

		boolean insert = (contact.getUuid() == null);
		fillIdentEntity(contact);
		if (insert) {
			// insert
			contactDao.insert(contact);
		} else {
			// update
			contactDao.update(contact);
		}
	}

	@Override
	public List<Contact> getContactByEmail(String email) {
		if (!StringUtils.hasText(email)) {
			return null;
		}
		return contactDao.getByEmail(email);
	}
	
	@Override
	public List<String> getEmailAll() {
		return contactDao.getEmailAll();
	}
}