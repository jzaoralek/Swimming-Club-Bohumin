package com.jzaoralek.scb.dataservice.domain;

import java.util.Date;
import java.util.UUID;

public interface IdentEntity {

	UUID getUuid();
	void setUuid(UUID uuid);
	String getModifBy();
	void setModifBy(String modifBy);
	Date getModifAt();
	void setModifAt(Date modifAt);
}