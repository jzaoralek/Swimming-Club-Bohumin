package com.jzaoralek.scb.dataservice.domain;

import java.sql.Time;
import java.util.Date;
import java.util.UUID;

import org.springframework.util.StringUtils;

public class Lesson implements IdentEntity {

	public enum DayOfWeek {
		SUNDAY,
		MONDAY,
		TUESDAY,
		WEDNESDAY,
		THURSDAY,
		FRIDAY,
		SATURDAY;

		public static DayOfWeek fromString(String value) {
			if (!StringUtils.hasText(value)) {
				return null;
			}

			return DayOfWeek.valueOf(value);
		}
	}

	private UUID uuid;
	private String modifBy;
	private Date modifAt;
	private Time timeFrom;
	private Time timeTo;
	private DayOfWeek dayOfWeek;
	private UUID courseUuid;
	private Course course;

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
	public Time getTimeFrom() {
		return timeFrom;
	}
	public void setTimeFrom(Time timeFrom) {
		this.timeFrom = timeFrom;
	}
	public Time getTimeTo() {
		return timeTo;
	}
	public void setTimeTo(Time timeTo) {
		this.timeTo = timeTo;
	}
	public DayOfWeek getDayOfWeek() {
		return dayOfWeek;
	}
	public void setDayOfWeek(DayOfWeek dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}
	public Course getCourse() {
		return course;
	}
	public void setCourse(Course course) {
		this.course = course;
	}
	public UUID getCourseUuid() {
		return courseUuid;
	}
	public void setCourseUuid(UUID courseUuid) {
		this.courseUuid = courseUuid;
	}

	@Override
	public String toString() {
		return "Lesson [uuid=" + uuid + ", modifBy=" + modifBy + ", modifAt=" + modifAt + ", timeFrom=" + timeFrom
				+ ", timeTo=" + timeTo + ", dayOfWeek=" + dayOfWeek + ", courseUuid=" + courseUuid + ", course="
				+ course + "]";
	}
}