package com.jzaoralek.scb.dataservice.domain;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Course implements IdentEntity {

	private UUID uuid;
	private String modifBy;
	private Date modifAt;
	private String name;
	private String description;
	private short yearFrom;
	private short yearTo;
	private List<CourseParticipant> participantList;
	private List<Lesson> lessonList;

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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public short getYearFrom() {
		return yearFrom;
	}
	public void setYearFrom(short yearFrom) {
		this.yearFrom = yearFrom;
	}
	public short getYearTo() {
		return yearTo;
	}
	public void setYearTo(short yearTo) {
		this.yearTo = yearTo;
	}
	public List<CourseParticipant> getParticipantList() {
		return participantList;
	}
	public void setParticipantList(List<CourseParticipant> participantList) {
		this.participantList = participantList;
	}
	public List<Lesson> getLessonList() {
		return lessonList;
	}
	public void setLessonList(List<Lesson> lessonList) {
		this.lessonList = lessonList;
	}

	@Override
	public String toString() {
		return "Course [uuid=" + uuid + ", modifBy=" + modifBy + ", modifAt=" + modifAt + ", name=" + name
				+ ", description=" + description + ", yearFrom=" + yearFrom + ", yearTo=" + yearTo
				+ ", participantList=" + participantList + ", lessonList=" + lessonList + "]";
	}
}