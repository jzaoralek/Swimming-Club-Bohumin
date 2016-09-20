package com.jzaoralek.scb.ui.common.vm;

import java.util.Arrays;

public class Attachment {

	private byte[] byteArray;
	private String contentType;
	private String name;

	public byte[] getByteArray() {
		return byteArray;
	}
	public void setByteArray(byte[] byteArray) {
		this.byteArray = byteArray;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Attachment [byteArray=" + Arrays.toString(byteArray) + ", contentType=" + contentType + ", name=" + name
				+ "]";
	}
}
