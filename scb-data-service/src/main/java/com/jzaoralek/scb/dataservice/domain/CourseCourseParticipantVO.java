package com.jzaoralek.scb.dataservice.domain;

import java.util.Date;
import java.util.UUID;

public class CourseCourseParticipantVO {
	
	private UUID uuid;
	private UUID courseParticUuid;
	private UUID courseUuid;
	private int varsymbolCore;
	private Date notifiedSemester1PaymentAt;
	private Date notifiedSemester2PaymentAt;
	private Date courseParticipationInterruptedAt;
	
	public UUID getCourseParticUuid() {
		return courseParticUuid;
	}
	public void setCourseParticUuid(UUID courseParticUuid) {
		this.courseParticUuid = courseParticUuid;
	}
	public UUID getCourseUuid() {
		return courseUuid;
	}
	public void setCourseUuid(UUID courseUuid) {
		this.courseUuid = courseUuid;
	}
	public int getVarsymbolCore() {
		return varsymbolCore;
	}
	public void setVarsymbolCore(int varsymbolCore) {
		this.varsymbolCore = varsymbolCore;
	}
	public Date getNotifiedSemester1PaymentAt() {
		return notifiedSemester1PaymentAt;
	}
	public void setNotifiedSemester1PaymentAt(Date notifiedSemester1PaymentAt) {
		this.notifiedSemester1PaymentAt = notifiedSemester1PaymentAt;
	}
	public Date getNotifiedSemester2PaymentAt() {
		return notifiedSemester2PaymentAt;
	}
	public void setNotifiedSemester2PaymentAt(Date notifiedSemester2PaymentAt) {
		this.notifiedSemester2PaymentAt = notifiedSemester2PaymentAt;
	}
	public Date getCourseParticipationInterruptedAt() {
		return courseParticipationInterruptedAt;
	}
	public void setCourseParticipationInterruptedAt(Date courseParticipationInterruptedAt) {
		this.courseParticipationInterruptedAt = courseParticipationInterruptedAt;
	}
	public UUID getUuid() {
		return uuid;
	}
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}
	
	@Override
	public String toString() {
		return "CourseCourseParticipantVO [uuid=" + uuid + ", courseParticUuid=" + courseParticUuid + ", courseUuid="
				+ courseUuid + ", varsymbolCore=" + varsymbolCore + ", notifiedSemester1PaymentAt="
				+ notifiedSemester1PaymentAt + ", notifiedSemester2PaymentAt=" + notifiedSemester2PaymentAt
				+ ", courseParticipationInterruptedAt=" + courseParticipationInterruptedAt + "]";
	}
}
