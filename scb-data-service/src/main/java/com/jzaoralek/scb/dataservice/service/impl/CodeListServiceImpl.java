package com.jzaoralek.scb.dataservice.service.impl;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jzaoralek.scb.dataservice.dao.CodeListDao;
import com.jzaoralek.scb.dataservice.domain.CodeListItem;
import com.jzaoralek.scb.dataservice.domain.CodeListItem.CodeListType;
import com.jzaoralek.scb.dataservice.exception.ScbValidationException;
import com.jzaoralek.scb.dataservice.service.BaseAbstractService;
import com.jzaoralek.scb.dataservice.service.CodeListService;

@Service("codeListService")
public class CodeListServiceImpl extends BaseAbstractService implements CodeListService {

	private static final Logger LOG = LoggerFactory.getLogger(CodeListServiceImpl.class);

	@Autowired
	private CodeListDao codeListDao;

	@Override
	public List<CodeListItem> getItemListByType(CodeListType type) {
		return codeListDao.getItemListByType(type);
	}

	@Override
	public CodeListItem store(CodeListItem item) throws ScbValidationException {
		if (item == null) {
			throw new IllegalArgumentException("item is null");
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("Storing CodeListItem: " + item);
		}

		boolean insert = item.getUuid() == null;

		// validation if exists another item with the same type and name
		CodeListItem itemWithSameTypeAndName = codeListDao.getByTypeAndName(item.getType(), item.getName());
		if (itemWithSameTypeAndName != null
				&& (insert || !itemWithSameTypeAndName.getUuid().toString().equals(item.getUuid().toString()))) {
			LOG.warn("CodeListItem with same type and name already exists, itemWithSameTypeAndName: " + itemWithSameTypeAndName);
			throw new ScbValidationException(messageSource.getMessage("msg.validation.warn.itemWithSameTypeAndNameAlreadyExists", null, Locale.getDefault()));
		}

		fillIdentEntity(item);


		if (insert) {
			codeListDao.insert(item);
		} else {
			codeListDao.update(item);
		}

		return item;
	}

	@Override
	public void delete(UUID uuid) throws ScbValidationException {
		CodeListItem item = codeListDao.getByUuid(uuid);
		if (item == null) {
			LOG.warn("CodeListItem not found, uuid: " + uuid);
			throw new ScbValidationException(messageSource.getMessage("msg.validation.warn.recordNotExistsInDB", null, Locale.getDefault()));
		}

		// TODO: pokud pouzita na nejakem vysledku, nelze odstranit

		if (LOG.isDebugEnabled()) {
			LOG.debug("Deleting codeListItem: " + item);
		}

		codeListDao.delete(item);
	}
}
