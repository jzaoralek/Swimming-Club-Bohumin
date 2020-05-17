package com.jzaoralek.scb.dataservice.domain;

import java.util.Date;
import java.util.UUID;

/**
 * Used for onfiguration of dynamic attributest on course application.
 *
 */
public class CourseApplDynAttrConfig implements IdentEntity {

	public enum CourseApplDynAttrConfigType {
		TEXT,
		DATE,
		INT,
		DOUBLE,
		BOOLEAN;
	}
	
	private UUID uuid;
	private String name;
	private String description;
	private boolean required;
	private CourseApplDynAttrConfigType type;
	private Date createdAt;
	private Date terminatedAt;
	private String modifBy;
	private Date modifAt;
	
	public UUID getUuid() {
		return uuid;
	}
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
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
	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}
	public CourseApplDynAttrConfigType getType() {
		return type;
	}
	public void setType(CourseApplDynAttrConfigType type) {
		this.type = type;
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
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public Date getTerminatedAt() {
		return terminatedAt;
	}
	public void setTerminatedAt(Date terminatedAt) {
		this.terminatedAt = terminatedAt;
	}
	
	public boolean isActive() {
		return this.terminatedAt == null;
	}
	
	@Override
	public String toString() {
		return "CourseApplDynAttrConfig [uuid=" + uuid + ", name=" + name + ", description=" + description
				+ ", required=" + required + ", type=" + type + ", createdAt=" + createdAt + ", terminatedAt="
				+ terminatedAt + ", modifBy=" + modifBy + ", modifAt=" + modifAt + "]";
	}
}
