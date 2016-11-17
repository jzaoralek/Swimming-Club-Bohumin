package com.jzaoralek.scb.dataservice.domain;

import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class LearningLesson implements IdentEntity {

	private UUID uuid;
	private String modifBy;
	private Date modifAt;
	private Date lessonDate;
	private Time timeFrom;
	private Time timeTo;
	private String description;
	private Lesson lesson;
	private List<CourseParticipant> participantList;

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
	public Date getLessonDate() {
		return lessonDate;
	}
	public void setLessonDate(Date lessonDate) {
		this.lessonDate = lessonDate;
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Lesson getLesson() {
		return lesson;
	}
	public void setLesson(Lesson lesson) {
		this.lesson = lesson;
	}
	public List<CourseParticipant> getParticipantList() {
		return participantList;
	}
	public void setParticipantList(List<CourseParticipant> participantList) {
		this.participantList = participantList;
	}

	@Override
	public String toString() {
		return "LearningLesson [uuid=" + uuid + ", modifBy=" + modifBy + ", modifAt=" + modifAt + ", lessonDate="
				+ lessonDate + ", timeFrom=" + timeFrom + ", timeTo=" + timeTo + ", description=" + description
				+ ", lesson=" + lesson + ", participantList=" + participantList + "]";
	}
}
