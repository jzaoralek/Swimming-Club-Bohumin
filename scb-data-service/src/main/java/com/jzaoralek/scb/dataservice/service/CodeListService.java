package com.jzaoralek.scb.dataservice.service;

import java.util.List;
import java.util.UUID;

import com.jzaoralek.scb.dataservice.domain.CodeListItem;
import com.jzaoralek.scb.dataservice.domain.CodeListItem.CodeListType;
import com.jzaoralek.scb.dataservice.exception.ScbValidationException;

public interface CodeListService {

	List<CodeListItem> getItemListByType(CodeListType type);
	CodeListItem store(CodeListItem item) throws ScbValidationException;
	void delete(UUID uuid) throws ScbValidationException;
}
