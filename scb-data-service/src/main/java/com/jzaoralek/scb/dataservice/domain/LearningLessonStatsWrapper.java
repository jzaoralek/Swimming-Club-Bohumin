package com.jzaoralek.scb.dataservice.domain;

import java.util.List;

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
			ret[i] = this.courseParticipantList.get(i).getContact().getCompleteName();
		}
		return ret;
	}

	@Override
	public String toString() {
		return "LearningLessonStatsWrapper [courseParticipantList=" + courseParticipantList + ", learnLessonsStatsList="
				+ learnLessonsStatsList + "]";
	}
}