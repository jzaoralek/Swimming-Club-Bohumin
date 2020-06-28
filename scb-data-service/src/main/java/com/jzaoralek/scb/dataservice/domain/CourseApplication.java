package com.jzaoralek.scb.dataservice.domain;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.util.StringUtils;

import com.jzaoralek.scb.dataservice.service.impl.ConfigurationServiceImpl;

public class CourseApplication implements IdentEntity {

	private UUID uuid;
	private String modifBy;
	private Date modifAt;
	private int yearFrom;
	private int yearTo;
	private boolean payed;
	private CourseParticipant courseParticipant;
	private ScbUser courseParticRepresentative;
	// current participant from previous year
	private boolean currentParticipant;

	public CourseApplication() {
		this.courseParticipant = new CourseParticipant();
		this.courseParticRepresentative = new ScbUser();
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
	public boolean isPayed() {
		return payed;
	}
	public void setPayed(boolean payed) {
		this.payed = payed;
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
	public boolean isCurrentParticipant() {
		return currentParticipant;
	}
	public void setCurrentParticipant(boolean currentParticipant) {
		this.currentParticipant = currentParticipant;
	}
	public void fillYearFromTo(String value) {
		if (!StringUtils.hasText(value)) {
			return;
		}
		String[] years = value.split(ConfigurationServiceImpl.COURSE_YEAR_DELIMITER);
		if (years.length < 2) {
			return;
		}
		this.yearFrom = Integer.valueOf(years[0]);
		this.yearTo = Integer.valueOf(years[1]);
	}

	@Override
	public String toString() {
		return "CourseApplication [uuid=" + uuid + ", modifBy=" + modifBy + ", modifAt=" + modifAt + ", yearFrom="
				+ yearFrom + ", yearTo=" + yearTo + ", payed=" + payed + ", courseParticipant=" + courseParticipant
				+ ", courseParticRepresentative=" + courseParticRepresentative + ", currentParticipant="
				+ currentParticipant + "]";
	}
}