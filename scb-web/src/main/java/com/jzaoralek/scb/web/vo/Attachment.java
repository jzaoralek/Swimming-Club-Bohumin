package com.jzaoralek.scb.web.vo;

import java.util.UUID;

public class Attachment {
	private UUID uuid;
	private byte[] byteArray;
	private String name;
	private String contentType;
	private String description;

	public Attachment() {};
	
	public Attachment(byte[] file, String name) {
		super();
		this.byteArray = file;
		this.name = name;
	}
	
	public byte[] getByteArray() {
		return byteArray;
	}
	public void setByteArray(byte[] file) {
		this.byteArray = file;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public UUID getUuid() {
		return uuid;
	}
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}
}
