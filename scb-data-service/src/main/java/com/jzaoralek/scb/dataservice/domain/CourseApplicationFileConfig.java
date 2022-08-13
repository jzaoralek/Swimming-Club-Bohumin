package com.jzaoralek.scb.dataservice.domain;

import java.util.Date;
import java.util.EnumSet;
import java.util.UUID;

import com.sportologic.common.model.domain.IdentEntity;

public class CourseApplicationFileConfig implements IdentEntity {

	public enum CourseApplicationFileType {
		GDPR,
		HEALTH_INFO,
		HEALTH_EXAM,
		CLUB_RULES,
		OTHER;
	}
	private UUID uuid;
	private String modifBy;
	private Date modifAt;
	private CourseApplicationFileType type;
	private UUID attachmentUuid;
	private Attachment attachment;
	private boolean pageText;
	private boolean pageAttachment;
	private boolean emailAttachment;
	private String description;
	
	/**
	 * Page text is visible only for GDPR, HEALTH_INFO and CLUB_RULES types.
	 * @return
	 */
	public boolean isPageTextContext() {
		return EnumSet.of(CourseApplicationFileType.GDPR, 
						CourseApplicationFileType.HEALTH_INFO, 
						CourseApplicationFileType.CLUB_RULES).
					contains(this.type);
	}
	
	/**
	 * File config file can be deleted only for OTHER type.
	 * @return
	 */
	public boolean isCanDelete() {
		return this.type == CourseApplicationFileType.OTHER;
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
	public UUID getAttachmentUuid() {
		return attachmentUuid;
	}
	public void setAttachmentUuid(UUID attachmentUuid) {
		this.attachmentUuid = attachmentUuid;
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String descrion) {
		this.description = descrion;
	}
}
