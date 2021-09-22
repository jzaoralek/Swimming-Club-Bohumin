package com.jzaoralek.scb.dataservice.domain;

import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

/**
 * Confirmation record for Covid-19.
 *
 */
public class Covid19Confirmation implements IdentEntity {

	public enum Covid19ConfType {
		VACCINATION,
		TEST_ANTIGEN, 
		TEST_PCR, 
		TEST_SELF;
	}
	
	private UUID uuid;
	private String modifBy;
	private Date modifAt;
	private Covid19ConfType type;
	private byte[] file;
	private String decription;
	private UUID courseParticUuid;
	
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
	public Covid19ConfType getType() {
		return type;
	}
	public void setType(Covid19ConfType type) {
		this.type = type;
	}
	public byte[] getFile() {
		return file;
	}
	public void setFile(byte[] file) {
		this.file = file;
	}
	public String getDecription() {
		return decription;
	}
	public void setDecription(String decription) {
		this.decription = decription;
	}
	public UUID getCourseParticUuid() {
		return courseParticUuid;
	}
	public void setCourseParticUuid(UUID courseParticUuid) {
		this.courseParticUuid = courseParticUuid;
	}
	
	@Override
	public String toString() {
		return "Covid19Confirmation [uuid=" + uuid + ", modifBy=" + modifBy + ", modifAt=" + modifAt + ", type=" + type
				+ ", file=" + Arrays.toString(file) + ", decription=" + decription + ", courseParticUuid="
				+ courseParticUuid + "]";
	}
}
