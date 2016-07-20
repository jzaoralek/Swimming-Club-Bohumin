package com.jzaoralek.scb.dataservice.domain;

import java.util.Date;
import java.util.UUID;

public class CourseApplication implements IdentEntity {

	private UUID uuid;
	private String modifBy;
	private Date modifAt;
	private int yearFrom;
	private int yearTo;
	private CourseParticipant courseParticipant;
	private ScbUser courseParticRepresentative;

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
	public int getYearFrom() {
		return yearFrom;
	}
	public void setYearFrom(int yearFrom) {
		this.yearFrom = yearFrom;
	}
	public int getYearTo() {
		return yearTo;
	}
	public void setYearTo(int yearTo) {
		this.yearTo = yearTo;
	}
	public CourseParticipant getCourseParticipant() {
		return courseParticipant;
	}
	public void setCourseParticipant(CourseParticipant courseParticipant) {
		this.courseParticipant = courseParticipant;
	}
	public ScbUser getCourseParticRepresentative() {
		return courseParticRepresentative;
	}
	public void setCourseParticRepresentative(ScbUser courseParticRepresentative) {
		this.courseParticRepresentative = courseParticRepresentative;
	}

	@Override
	public String toString() {
		return "CourseApplication [uuid=" + uuid + ", modifBy=" + modifBy + ", modifAt=" + modifAt + ", yearFrom="
				+ yearFrom + ", yearTo=" + yearTo + ", courseParticipant=" + courseParticipant
				+ ", courseParticRepresentative=" + courseParticRepresentative + "]";
	}
}