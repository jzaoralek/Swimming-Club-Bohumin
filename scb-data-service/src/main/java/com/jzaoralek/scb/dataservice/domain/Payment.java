package com.jzaoralek.scb.dataservice.domain;

import java.util.Date;
import java.util.UUID;

public class Payment implements IdentEntity {

	public enum PaymentType {
		CASH,
		BANK_TRANS,
		DONATE,
		OTHER;
	}
	
	private UUID uuid;
	private String modifBy;
	private Date modifAt;
	private Long amount;
	private String description;
	private PaymentType type;
	private UUID courseCourseParticipantUuid;
	
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
	public Long getAmount() {
		return amount;
	}
	public void setAmount(Long amount) {
		this.amount = amount;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public PaymentType getType() {
		return type;
	}
	public void setType(PaymentType type) {
		this.type = type;
	}
	public UUID getCourseCourseParticipantUuid() {
		return courseCourseParticipantUuid;
	}
	public void setCourseCourseParticipantUuid(UUID courseCourseParticipantUuid) {
		this.courseCourseParticipantUuid = courseCourseParticipantUuid;
	}
	
	@Override
	public String toString() {
		return "Payment [uuid=" + uuid + ", modifBy=" + modifBy + ", modifAt=" + modifAt + ", amount=" + amount
				+ ", description=" + description + ", type=" + type + ", courseCourseParticipantUuid="
				+ courseCourseParticipantUuid + "]";
	}
}
