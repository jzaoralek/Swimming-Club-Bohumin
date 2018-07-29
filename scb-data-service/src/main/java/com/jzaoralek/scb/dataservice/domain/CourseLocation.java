package com.jzaoralek.scb.dataservice.domain;

import java.util.UUID;

public class CourseLocation {

	private UUID uuid;
	private String name;
	private String description;
	
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
	
	@Override
	public String toString() {
		return "CourseLocation [uuid=" + uuid + ", name=" + name + ", description=" + description + "]";
	}
}
