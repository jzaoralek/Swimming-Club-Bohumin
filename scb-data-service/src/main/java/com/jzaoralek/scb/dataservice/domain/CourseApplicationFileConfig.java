package com.jzaoralek.scb.dataservice.domain;

import java.util.Date;
import java.util.UUID;

public class CourseApplicationFileConfig implements IdentEntity {

	public enum CourseApplicationFileType {
		GDPR,
		HEALTH_INFO,
		HEALTH_EXAM,
		CLUB_RULES;
	}
	private UUID uuid;
	private String modifBy;
	private Date modifAt;
	private CourseApplicationFileType type;
	private Attachment attachment;
	private boolean pageText;
	private boolean pageAttachment;
	private boolean emailAttachment;
	private String descrion;
	
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
	public CourseApplicationFileType getType() {
		return type;
	}
	public void setType(CourseApplicationFileType type) {
		this.type = type;
	}
	public Attachment getAttachment() {
		return attachment;
	}
	public void setAttachment(Attachment attachment) {
		this.attachment = attachment;
	}
	public boolean isPageText() {
		return pageText;
	}
	public void setPageText(boolean pageText) {
		this.pageText = pageText;
	}
	public boolean isPageAttachment() {
		return pageAttachment;
	}
	public void setPageAttachment(boolean pageAttachment) {
		this.pageAttachment = pageAttachment;
	}
	public boolean isEmailAttachment() {
		return emailAttachment;
	}
	public void setEmailAttachment(boolean emailAttachment) {
		this.emailAttachment = emailAttachment;
	}
	public String getDescrion() {
		return descrion;
	}
	public void setDescrion(String descrion) {
		this.descrion = descrion;
	}
}
