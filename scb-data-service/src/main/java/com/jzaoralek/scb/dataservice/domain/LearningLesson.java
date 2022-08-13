package com.jzaoralek.scb.dataservice.domain;

import java.sql.Time;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.jzaoralek.scb.dataservice.utils.DateUtils;
import com.sportologic.common.model.domain.IdentEntity;

public class LearningLesson implements IdentEntity {

	public static final Comparator<LearningLesson> LESSON_DATE_TIME_COMPARATOR = new Comparator<LearningLesson>() {
		@Override
		public int compare(LearningLesson o1, LearningLesson o2) {
			int c = o1.lessonDate.compareTo(o2.lessonDate);
			if (c == 0) {
				c = o1.timeFrom.compareTo(o2.timeFrom);
			}
			
			return c;
		}
	};
	
	private UUID uuid;
	private String modifBy;
	private Date modifAt;
	private Date lessonDate;
	private Time timeFrom;
	private Time timeTo;
	private String description;
	private Lesson lesson;
	private Integer additionalColumnInt;
	private List<CourseParticipant> participantList;
	private boolean firstInMonth;

	public LearningLesson() {
	}

	public LearningLesson(LearningLesson learningLesson) {
		super();
		this.uuid = learningLesson.uuid;
		this.modifBy = learningLesson.modifBy;
		this.modifAt = learningLesson.modifAt;
		this.lessonDate = learningLesson.lessonDate;
		this.timeFrom = learningLesson.timeFrom;
		this.timeTo = learningLesson.timeTo;
		this.description = learningLesson.description;
		this.lesson = learningLesson.lesson;
		this.additionalColumnInt = learningLesson.additionalColumnInt;
		this.participantList = learningLesson.participantList;
	}

	public LearningLesson(Lesson lesson, Date date) {
		this.setLesson(lesson);
		this.setTimeFrom(lesson.getTimeFrom());
		this.setTimeTo(lesson.getTimeTo());
		this.setLessonDate(date);
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
	public boolean isFirstInMonth() {
		return firstInMonth;
	}
	public void setFirstInMonth(boolean firstInMonth) {
		this.firstInMonth = firstInMonth;
	}
	public Integer getAdditionalColumnInt() {
		return additionalColumnInt;
	}

	public void setAdditionalColumnInt(Integer additionalColumnInt) {
		this.additionalColumnInt = additionalColumnInt;
	}
	
	/**
	 * Datum lekce v budoucnu.
	 * @return
	 */
	public boolean isInFuture() {
		return DateUtils.normalizeToDay(this.lessonDate).after(DateUtils.normalizeToDay(Calendar.getInstance().getTime()));
	}

	@Override
	public String toString() {
		return "LearningLesson [uuid=" + uuid + ", modifBy=" + modifBy + ", modifAt=" + modifAt + ", lessonDate="
				+ lessonDate + ", timeFrom=" + timeFrom + ", timeTo=" + timeTo + ", description=" + description
				+ ", lesson=" + lesson + ", additionalColumnInt=" + additionalColumnInt + ", participantList="
				+ participantList + ", firstInMonth=" + firstInMonth + "]";
	}
}
