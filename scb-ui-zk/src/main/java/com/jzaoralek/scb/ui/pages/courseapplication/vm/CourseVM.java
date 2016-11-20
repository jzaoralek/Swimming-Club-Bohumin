package com.jzaoralek.scb.ui.pages.courseapplication.vm;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.bind.annotation.QueryParam;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import com.jzaoralek.scb.dataservice.domain.Course;
import com.jzaoralek.scb.dataservice.domain.CourseParticipant;
import com.jzaoralek.scb.dataservice.domain.Lesson;
import com.jzaoralek.scb.dataservice.exception.ScbValidationException;
import com.jzaoralek.scb.dataservice.service.ConfigurationService;
import com.jzaoralek.scb.dataservice.service.CourseService;
import com.jzaoralek.scb.dataservice.service.LessonService;
import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEvent;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEventQueues;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.BaseVM;

public class CourseVM extends BaseVM {

	private static final Logger LOG = LoggerFactory.getLogger(CourseVM.class);

	private Course course;
	private List<String> courseYearList;
	private String courseYearSelected;
	private Boolean updateMode;
	private List<CourseParticipant> participantSelectedList;

	@WireVariable
	private CourseService courseService;

	@WireVariable
	private LessonService lessonService;

	@WireVariable
	private ConfigurationService configurationService;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Init
	public void init(@QueryParam(WebConstants.UUID_PARAM) final String uuid, @QueryParam(WebConstants.FROM_PAGE_PARAM) String fromPage) {
		Course course = null;
		if (StringUtils.hasText(uuid)) {
			course = courseService.getByUuid(UUID.fromString(uuid));
		}
		this.courseYearList = configurationService.getCourseYearFromActualYearList();
		if (course != null) {
			this.course = course;
			this.pageHeadline = Labels.getLabel("txt.ui.menu.courseDetail");
			this.updateMode = true;
			this.courseYearSelected = course.getYear();
		} else {
			this.course = new Course();
			this.pageHeadline = Labels.getLabel("txt.ui.menu.courseNew");
			this.updateMode = false;
			this.courseYearSelected = configurationService.getCourseApplicationYear();
		}

		setReturnPage(fromPage);

		final EventQueue eq = EventQueues.lookup(ScbEventQueues.COURSE_APPLICATION_QUEUE.name() , EventQueues.DESKTOP, true);
		eq.subscribe(new EventListener<Event>() {
			@Override
			public void onEvent(Event event) {
				if (event.getName().equals(ScbEvent.RELOAD_COURSE_PARTICIPANT_DATA_EVENT.name())) {
					loadData((UUID)event.getData());
				}
			}
		});
	}

	public void loadData(UUID uuid) {
		this.course = courseService.getByUuid(uuid);
		BindUtils.postNotifyChange(null, null, this, "course");
	}

	@NotifyChange("*")
	@Command
    public void submit() {
		try {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Storing course: " + this.course);
			}
			course.fillYearFromTo(this.courseYearSelected);
			this.course = courseService.store(course);
			WebUtils.showNotificationInfo(Labels.getLabel("msg.ui.info.changesSaved"));
			this.updateMode = true;
		} catch (ScbValidationException e) {
			LOG.warn("ScbValidationException caught for course: " + this.course);
			WebUtils.showNotificationError(e.getMessage());
		} catch (Exception e) {
			LOG.error("Unexpected exception caught for course: " + this.course, e);
			throw new RuntimeException(e);
		}
    }

	@NotifyChange("*")
	@Command
    public void lessonDeleteCmd(@BindingParam(WebConstants.UUID_PARAM) final UUID uuid) {
		try {
			lessonService.delete(uuid);
			loadData(this.course.getUuid());
		} catch (ScbValidationException e) {
			LOG.warn("ScbValidationException caught during deleting lesson uuid: "+ uuid+" for course: " + this.course);
			WebUtils.showNotificationError(e.getMessage());
		}
	}

	@Command
    public void courseParticipantDetailCmd(@BindingParam(WebConstants.UUID_PARAM) final UUID uuid) {
		if (uuid ==  null) {
			throw new IllegalArgumentException("uuid is null");
		}

//		String targetPage = WebPages.PARTICIPANT_DETAIL.getUrl();
//		WebPages fromPage = WebPages.COURSE_DETAIL;
//		Executions.sendRedirect(targetPage + "?"+WebConstants.UUID_PARAM+"="+uuid.toString() + "&" + WebConstants.FROM_PAGE_PARAM + "=" + fromPage);
	}

	@NotifyChange("*")
	@Command
    public void courseParticipantsDeleteCmd() {
		if (CollectionUtils.isEmpty(this.participantSelectedList)) {
			return;
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("Deleting courseParticipant course participants: " + participantSelectedList + " for course uuid:" + this.course.getUuid());
		}

		for (CourseParticipant item : this.participantSelectedList) {
			courseService.deleteParticipantFromCourse(item.getUuid(), this.course.getUuid());
		}

		loadData(this.course.getUuid());
	}

	@NotifyChange("*")
	@Command
    public void courseParticipantDeleteCmd(@BindingParam(WebConstants.UUID_PARAM) final UUID uuid) {
		if (uuid ==  null) {
			throw new IllegalArgumentException("uuid is null");
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("Deleting courseParticipants with uuid: " + uuid + " for course uuid:" + this.course.getUuid());
		}

		courseService.deleteParticipantFromCourse(uuid, this.course.getUuid());
		loadData(this.course.getUuid());
	}

	@Command
	public void addCourseParticipantsFromApplicationCmd() {
		 EventQueueHelper.publish(ScbEventQueues.COURSE_APPLICATION_QUEUE, ScbEvent.COURSE_UUID_FROM_APPLICATION_DATA_EVENT, null, this.course);
		 WebUtils.openModal("/pages/secured/participant-to-course-window.zul");
	}

	@Command
	public void addCourseParticipantsFromCourseCmd() {
		EventQueueHelper.publish(ScbEventQueues.COURSE_APPLICATION_QUEUE, ScbEvent.COURSE_UUID_FROM_COURSE_DATA_EVENT, null, this.course);
		WebUtils.openModal("/pages/secured/participant-to-course-window.zul");
	}

	@Command
	public void newLessonCmd() {
		Lesson lesson = new Lesson();
		lesson.setCourseUuid(this.course.getUuid());
		EventQueueHelper.publish(ScbEventQueues.COURSE_APPLICATION_QUEUE, ScbEvent.LESSON_NEW_DATA_EVENT, null, lesson);
		WebUtils.openModal("/pages/secured/lesson-to-course-window.zul");
	}

	@Command
	public void detailCmd(@BindingParam("lesson") final Lesson lesson) {
		EventQueueHelper.publish(ScbEventQueues.COURSE_APPLICATION_QUEUE, ScbEvent.LESSON_DETAIL_DATA_EVENT, null, lesson);
		WebUtils.openModal("/pages/secured/lesson-to-course-window.zul");
	}

	public Course getCourse() {
		return course;
	}

	public List<String> getCourseYearList() {
		return courseYearList;
	}

	public String getCourseYearSelected() {
		return courseYearSelected;
	}

	public void setCourseYearSelected(String courseYearSelected) {
		this.courseYearSelected = courseYearSelected;
	}

	public Boolean getUpdateMode() {
		return updateMode;
	}

	public List<CourseParticipant> getParticipantSelectedList() {
		return participantSelectedList;
	}

	public void setParticipantSelectedList(List<CourseParticipant> participantSelectedList) {
		this.participantSelectedList = participantSelectedList;
	}
}