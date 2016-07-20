package com.jzaoralek.scb.dataservice.service;

import java.util.Calendar;
import java.util.UUID;

import org.springframework.util.StringUtils;

import com.jzaoralek.scb.dataservice.domain.IdentEntity;

public abstract class BaseAbstractService {

	private static final String ANONYM_USER_UUID = "ANONYM";

	protected void fillIdentEntity(IdentEntity identEntity, String userUuid) {
		if (identEntity == null) {
			return;
		}

		if (identEntity != null) {
			identEntity.setUuid(UUID.randomUUID());
		}

		identEntity.setModifAt(Calendar.getInstance().getTime());
		identEntity.setModifBy(StringUtils.hasText(userUuid) ? userUuid : ANONYM_USER_UUID);
	}
}
