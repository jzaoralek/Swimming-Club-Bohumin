package com.jzaoralek.scb.dataservice.domain;

import java.util.Date;
import java.util.UUID;

import com.sportologic.common.model.domain.IdentEntity;

public class CodeListItem implements IdentEntity {

	public enum CodeListType {
		SWIMMING_STYLE;
	}

	private UUID uuid;
	private String modifBy;
	private Date modifAt;
	private String name;
	private String description;
	private CodeListType type;

	@Override
	public UUID getUuid() {
		return uuid;
	}
	@Override
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}
	@Override
	public String getModifBy() {
		return modifBy;
	}
	@Override
	public void setModifBy(String modifBy) {
		this.modifBy = modifBy;
	}
	@Override
	public Date getModifAt() {
		return modifAt;
	}
	@Override
	public void setModifAt(Date modifAt) {
		this.modifAt = modifAt;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public CodeListType getType() {
		return type;
	}
	public void setType(CodeListType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "CodeListItem [uuid=" + uuid + ", modifBy=" + modifBy + ", modifAt=" + modifAt + ", name=" + name
				+ ", description=" + description + ", type=" + type + "]";
	}
}
