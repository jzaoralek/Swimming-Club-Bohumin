package com.jzaoralek.scb.web.vo;

import java.util.List;

public class Mail {
	
	private String to;
	private String subject;
	private String text;
	private List<Attachment> attachmentList;
	
	public Mail(String to, String subject, String text, List<Attachment> attachmentList) {
		super();
		this.to = to;
		this.subject = subject;
		this.text = text;
		this.attachmentList = attachmentList;
	}
	
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public List<Attachment> getAttachmentList() {
		return attachmentList;
	}
	public void setAttachmentList(List<Attachment> attachmentList) {
		this.attachmentList = attachmentList;
	}
	
	@Override
	public String toString() {
		return "Mail [to=" + to + ", subject=" + subject + ", text=" + text + ", attachmentList=" + attachmentList
				+ "]";
	}
}
