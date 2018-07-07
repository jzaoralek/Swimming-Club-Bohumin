package com.jzaoralek.scb.dataservice.domain;

import java.util.Date;
import java.util.UUID;

/**
 * Represents configuration record.
 *
 */
public class Config implements IdentEntity {

	public enum ConfigName {
		COURSE_APPLICATION_ALLOWED,
		COURSE_APPLICATION_YEAR,
		ORGANIZATION_NAME,
		ORGANIZATION_PHONE,
		ORGANIZATION_EMAIl,
		WELCOME_INFO;
	}

	public enum ConfigType {
		STRING,
		INTEGER,
		BOOLEAN,
		ENUM;
	}

	private UUID uuid;
	private String modifBy;
	private Date modifAt;
	private String name;
	private String description;
	private String value;
	private ConfigType type;

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
	public ConfigType getType() {
		return type;
	}
	public void setType(ConfigType type) {
		this.type = type;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Config [uuid=" + uuid + ", modifBy=" + modifBy + ", modifAt=" + modifAt + ", name=" + name
				+ ", description=" + description + ", value=" + value + ", type=" + type + "]";
	}
}
