package com.jzaoralek.scb.dataservice.domain;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.util.CollectionUtils;

/**
 * Send email, contains additional attributes for status etc.
 *
 */
public class MailSend extends Mail implements IdentEntity {

	private UUID uuid;
	private String modifBy;
	private Date modifAt;
	private boolean success;
	private boolean attachments;
	private String description;
	
	public MailSend(String to, 
				String cc, 
				String subject, 
				String text, 
				List<Attachment> attachmentList) {
		super(to, cc, subject, text, attachmentList);
	}

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
	
	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public boolean isAttachments() {
		return attachments;
	}

	public void setAttachments(boolean attachments) {
		this.attachments = attachments;
	}

	@Override
	public String toString() {
		return "MailSent [uuid=" + uuid + ", modifBy=" + modifBy + ", modifAt=" + modifAt + ", success=" + success
				+ ", description=" + description + ", getTo()=" + getTo() + ", getCc()=" + getCc() + ", getSubject()="
				+ getSubject() + ", getText()=" + getText()
				+ ", isHtml()=" + isHtml() + ", toString()=" + super.toString() + ", getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + "]";
	}
}
