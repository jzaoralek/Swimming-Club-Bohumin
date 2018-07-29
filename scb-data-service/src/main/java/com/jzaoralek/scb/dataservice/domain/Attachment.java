package com.jzaoralek.scb.dataservice.domain;

public class Attachment {
	private byte[] file;
	private String name;
	
	public Attachment(byte[] file, String name) {
		super();
		this.file = file;
		this.name = name;
	}
	
	public byte[] getFile() {
		return file;
	}
	public void setFile(byte[] file) {
		this.file = file;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
