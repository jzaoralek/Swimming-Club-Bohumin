package com.jzaoralek.scb.ui.pages.courseapplication.vm;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Window;

import com.jzaoralek.scb.dataservice.domain.CourseParticipant;
import com.jzaoralek.scb.dataservice.domain.LearningLesson;
import com.jzaoralek.scb.dataservice.service.CourseService;
import com.jzaoralek.scb.dataservice.service.LearningLessonService;
import com.jzaoralek.scb.ui.common.converter.Converters;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEvent;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEventQueues;
import com.jzaoralek.scb.ui.common.vm.BaseVM;

public class LearningLessonDetailWinVM extends BaseVM {

	@WireVariable
	private CourseService courseService;

	@WireVariable
	private LearningLessonService learningLessonService;

	private LearningLesson learningLesson;
	private List<CourseParticipant> allCourseParticList;
	private List<CourseParticipant> lessonParticipantList;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Init
	public void init() {
		final EventQueue eq = EventQueues.lookup(ScbEventQueues.LEARNING_LESSON_QUEUE.name() , EventQueues.DESKTOP, true);
		eq.subscribe(new EventListener<Event>() {
			@Override
			public void onEvent(Event event) {
				if (event.getName().equals(ScbEvent.LEARNIN_LESSON_DETAIL_DATA_EVENT.name())) {
					initData((LearningLesson)event.getData());
					eq.unsubscribe(this);
				}
			}
		});
	}

	@Command
	public void submitCmd(@BindingParam("window") Window window) {
		this.learningLesson.setParticipantList(this.lessonParticipantList);
		learningLessonService.store(this.learningLesson);

		EventQueueHelper.publish(ScbEventQueues.LEARNING_LESSON_QUEUE, ScbEvent.RELOAD_LEARNIN_LESSON_LIST_DATA_EVENT, null, this.learningLesson);

		window.detach();
	}

	private void initData(LearningLesson learningLesson) {
		if (learningLesson == null) {
			return;
		}

		this.learningLesson = learningLesson.getUuid() != null ? learningLessonService.getByUUID(learningLesson.getUuid()) : learningLesson;
		this.allCourseParticList = courseService.getByCourseParticListByCourseUuid(this.learningLesson.getLesson().getCourseUuid(), false);

		// selected vybrat zvsech ucastniku
		if (!CollectionUtils.isEmpty(this.learningLesson.getParticipantList())) {
			List<CourseParticipant> courseParticSelected = new ArrayList<CourseParticipant>();
			for  (CourseParticipant courseParticipant : this.allCourseParticList) {
				for (CourseParticipant lessonParticipant : this.learningLesson.getParticipantList()) {
					if (courseParticipant.getUuid().toString().equals(lessonParticipant.getUuid().toString())) {
						courseParticSelected.add(courseParticipant);
					}
				}
			}
			this.lessonParticipantList = courseParticSelected;
		}

		this.pageHeadline = buildPageHeadline(learningLesson);

		BindUtils.postNotifyChange(null, null, this, "pageHeadline");
		BindUtils.postNotifyChange(null, null, this, "learningLesson");
		BindUtils.postNotifyChange(null, null, this, "allCourseParticList");
		BindUtils.postNotifyChange(null, null, this, "lessonParticipantList");
	}

	private String buildPageHeadline(LearningLesson learningLesson) {
		return Converters.getEnumlabelconverter().coerceToUi(learningLesson.getLesson().getDayOfWeek(), null, null) + ", "
			+ Converters.getDateconverter().coerceToUi(learningLesson.getLessonDate(), null, null) + ", "
			+ Converters.getTimeconverter().coerceToUi(learningLesson.getTimeFrom(), null, null) + "-"
			+ Converters.getTimeconverter().coerceToUi(learningLesson.getTimeTo(), null, null);
	}

	public LearningLesson getLearningLesson() {
		return learningLesson;
	}

	public void setLearningLesson(LearningLesson learningLesson) {
		this.learningLesson = learningLesson;
	}

	public List<CourseParticipant> getLessonParticipantList() {
		return lessonParticipantList;
	}

	public void setLessonParticipantList(List<CourseParticipant> lessonParticipantList) {
		this.lessonParticipantList = lessonParticipantList;
	}

	public List<CourseParticipant> getAllCourseParticList() {
		return allCourseParticList;
	}

	public void setAllCourseParticList(List<CourseParticipant> allCourseParticList) {
		this.allCourseParticList = allCourseParticList;
	}
}
