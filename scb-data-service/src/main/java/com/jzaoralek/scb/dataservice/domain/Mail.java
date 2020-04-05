package com.jzaoralek.scb.dataservice.domain;

import java.util.List;

public class Mail {
	
	private String to;
	private String cc;
	private String subject;
	private String text;
	private List<Attachment> attachmentList;
	private boolean html;

	public Mail(String to, String cc, String subject, String text, List<Attachment> attachmentList) {
		super();
		this.to = to;
		this.cc = cc;
		this.subject = subject;
		this.text = text;
		this.attachmentList = attachmentList;
	}
	
	/**
	 * Factory method for create of html email.
	 * @param to
	 * @param cc
	 * @param subject
	 * @param text
	 * @param attachmentList
	 * @return
	 */
	public static Mail ofHtml(String to, String cc, String subject, String text, List<Attachment> attachmentList) {
		Mail ret = new Mail(to, cc, subject, text, attachmentList);
		ret.setHtml(true);
		
		return  ret;
	}
	
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getCc() {
		return cc;
	}
	public void setCc(String cc) {
		this.cc = cc;
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
	public boolean isHtml() {
		return html;
	}
	public void setHtml(boolean html) {
		this.html = html;
	}
	
	@Override
	public String toString() {
		return "Mail [to=" + to + ", subject=" + subject + ", text=" + text + ", attachmentList=" + attachmentList
				+ "]";
	}
}
