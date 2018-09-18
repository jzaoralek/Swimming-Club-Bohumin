package com.jzaoralek.scb.dataservice.service;

import java.util.Calendar;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.StringUtils;

import com.jzaoralek.scb.dataservice.domain.IdentEntity;
import com.jzaoralek.scb.dataservice.utils.SecurityUtils;

public abstract class BaseAbstractService {

	protected static final String ANONYM_USER_UUID = "ANONYM";

	@Autowired
    protected MessageSource messageSource;

	protected void fillIdentEntity(IdentEntity identEntity) {
		if (identEntity == null) {
			return;
		}

		if (identEntity != null && identEntity.getUuid() == null) {
			identEntity.setUuid(UUID.randomUUID());
		}

		String loggedUserUsername = SecurityUtils.getLoggedUserUsername();
		identEntity.setModifAt(Calendar.getInstance().getTime());
		identEntity.setModifBy(StringUtils.hasText(loggedUserUsername) ? loggedUserUsername : ANONYM_USER_UUID);
	}
}
