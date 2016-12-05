package com.jzaoralek.scb.dataservice.domain;

import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.util.CollectionUtils;

/**
 * Objekt pro statistiku dochazky daneho kurzu.
 *
 */
public class LearningLessonStatsWrapper {

	private final List<CourseParticipant> courseParticipantList;
	private final List<LearningLessonStats> learnLessonsStatsList;

	public LearningLessonStatsWrapper(List<CourseParticipant> courseParticipantList,
			List<LearningLessonStats> learnLessonsStatsList) {
		super();
		this.courseParticipantList = courseParticipantList;
		this.learnLessonsStatsList = learnLessonsStatsList;
	}
	
	public List<CourseParticipant> getCourseParticipantList() {
		return courseParticipantList;
	}

	public List<LearningLessonStats> getLearnLessonsStatsList() {
		return learnLessonsStatsList;
	}
	
	/**
	 * Vraci jmena ucastniku kurzu.
	 * @return
	 */
	public String[] getParticipantCompleteNameArr() {
		if (CollectionUtils.isEmpty(this.courseParticipantList)) {
			return ArrayUtils.EMPTY_STRING_ARRAY;
		}
		String[] ret = new String[this.courseParticipantList.size()];
		for (int i = 0; i < this.courseParticipantList.size(); i++) {
			// nazev a statistika dochazka
			ret[i] = this.courseParticipantList.get(i).getContact().getCompleteName() + "\n" + particAttendance(this.courseParticipantList.get(i).getUuid()) +"%";
		}
		return ret;
	}
	
	/**
	 * Statistika dochazka.
	 * Pocet learnLessonsStatsList, projit learnLessonsStatsList, vyhledat ucastnika a zjistit pocet lekci s ucasti
	 * @param particUuid
	 * @return
	 */
	private int particAttendance(UUID particUuid) {
		if (CollectionUtils.isEmpty(this.learnLessonsStatsList) || this.learnLessonsStatsList.size() == 0) {
			return 0;
		}
		int attendance = 0;
		for (LearningLessonStats learnLessonStatsItem : this.learnLessonsStatsList) {
			for (CourseParticipant participantItem : learnLessonStatsItem.getCourseParticipantList()) {
				if (participantItem.getUuid().toString().equals(particUuid.toString())) {
					if (participantItem.isLessonAttendance()) {
						attendance++;
					}
				}
			}
		}
		
		if (attendance == 0) {
			return 0;
		}
		
		float ret = (attendance * 100.0f) / this.learnLessonsStatsList.size();
		return Math.round(ret);
	}

	@Override
	public String toString() {
		return "LearningLessonStatsWrapper [courseParticipantList=" + courseParticipantList + ", learnLessonsStatsList="
				+ learnLessonsStatsList + "]";
	}
}