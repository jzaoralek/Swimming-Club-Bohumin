package com.jzaoralek.scb.dataservice.dao;

import java.util.List;
import java.util.UUID;

import com.jzaoralek.scb.dataservice.domain.CodeListItem;
import com.jzaoralek.scb.dataservice.domain.CodeListItem.CodeListType;

public interface CodeListDao {

	List<CodeListItem> getItemListByType(CodeListType type);
	CodeListItem getByTypeAndName(CodeListType type, String name);
	CodeListItem getByUuid(UUID uuid);
	void insert(CodeListItem item);
	void update(CodeListItem item);
	void delete(CodeListItem item);
}
