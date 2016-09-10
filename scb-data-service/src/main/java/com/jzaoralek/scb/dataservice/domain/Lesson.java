package com.jzaoralek.scb.dataservice.domain;

import java.sql.Time;
import java.util.Comparator;
import java.util.Date;
import java.util.UUID;

import org.springframework.util.StringUtils;

public class Lesson implements IdentEntity {

	public enum DayOfWeek {
		MONDAY(0),
		TUESDAY(1),
		WEDNESDAY(2),
		THURSDAY(3),
		FRIDAY(4),
		SATURDAY(5),
		SUNDAY(6);

		private int index;

		private DayOfWeek(int index) {
			this.index = index;
		}

		public int getIndex() {
			return index;
		}

		public static DayOfWeek fromString(String value) {
			if (!StringUtils.hasText(value)) {
				return null;
			}

			return DayOfWeek.valueOf(value);
		}
	}

	public static final Comparator<Lesson> DAY_OF_WEEK_COMP =
			new Comparator<Lesson>() {
				@Override
				public int compare(Lesson o1, Lesson o2) {
					int poradi1 = o1.getDayOfWeek().getIndex();
					int poradi2 = o2.getDayOfWeek().getIndex();
					if(poradi1>poradi2){
						return 1;
					}
					else if(poradi1<poradi2){
						return -1;
					}
					else{
						return 0;
					}
				}
			};

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

	public static class DayOfWeekComparator implements Comparator<Lesson> {

		@Override
		public int compare(Lesson o1, Lesson o2) {
			int poradi1 = o1.getDayOfWeek().getIndex();
			int poradi2 = o2.getDayOfWeek().getIndex();
			if(poradi1>poradi2){
				return 1;
			}
			else if(poradi1<poradi2){
				return -1;
			}
			else{
				return 0;
			}
		}
	}


	@Override
	public String toString() {
		return "Lesson [uuid=" + uuid + ", modifBy=" + modifBy + ", modifAt=" + modifAt + ", timeFrom=" + timeFrom
				+ ", timeTo=" + timeTo + ", dayOfWeek=" + dayOfWeek + ", courseUuid=" + courseUuid + ", course="
				+ course + "]";
	}
}