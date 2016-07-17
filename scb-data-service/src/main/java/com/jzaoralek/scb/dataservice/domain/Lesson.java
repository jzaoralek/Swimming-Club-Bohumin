package com.jzaoralek.scb.dataservice.domain;

import java.sql.Time;
import java.time.DayOfWeek;
import java.util.UUID;

public class Lesson implements IdentEntity {

	private UUID uuid;
	private Time timeFrom;
	private Time timeTo;
	private DayOfWeek dayOfWeek;
	private Course course;
	
	public UUID getUuid() {
		return uuid;
	}
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
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
	
	@Override
	public String toString() {
		return "Lesson [uuid=" + uuid + ", timeFrom=" + timeFrom + ", timeTo=" + timeTo + ", dayOfWeek=" + dayOfWeek
				+ ", course=" + course + "]";
	}
}