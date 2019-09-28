package com.jzaoralek.scb.ui.pages.courseapplication.vm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.bind.annotation.QueryParam;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import com.jzaoralek.scb.dataservice.domain.Contact;
import com.jzaoralek.scb.dataservice.domain.Course;
import com.jzaoralek.scb.dataservice.domain.CourseLocation;
import com.jzaoralek.scb.dataservice.domain.CourseParticipant;
import com.jzaoralek.scb.dataservice.domain.Lesson;
import com.jzaoralek.scb.dataservice.exception.ScbValidationException;
import com.jzaoralek.scb.dataservice.service.ConfigurationService;
import com.jzaoralek.scb.dataservice.service.CourseService;
import com.jzaoralek.scb.dataservice.service.LessonService;
import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.WebPages;
import com.jzaoralek.scb.ui.common.events.SzpEventListener;
import com.jzaoralek.scb.ui.common.template.SideMenuComposer.ScbMenuItem;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEvent;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEventQueues;
import com.jzaoralek.scb.ui.common.utils.MessageBoxUtils;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.BaseVM;

public class CourseVM extends BaseVM {

	private static final Logger LOG = LoggerFactory.getLogger(CourseVM.class);

	private Course course;
	private List<String> courseYearList;
	private String courseYearSelected;
	private Boolean updateMode;
	private List<CourseParticipant> participantSelectedList;
	private List<CourseLocation> courseLocationList;

	@WireVariable
	private CourseService courseService;

	@WireVariable
	private LessonService lessonService;

	@WireVariable
	private ConfigurationService configurationService;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Init
	public void init(@QueryParam(WebConstants.UUID_PARAM) final String uuid, @QueryParam(WebConstants.FROM_PAGE_PARAM) String fromPage) {
        setMenuSelected(ScbMenuItem.SEZNAM_KURZU);
        Course course = null;
		if (StringUtils.hasText(uuid)) {
			course = courseService.getByUuid(UUID.fromString(uuid));
		}
		this.courseYearList = configurationService.getCourseYearFromActualYearList();
		this.courseLocationList = courseService.getCourseLocationAll();
		if (course != null) {
			this.course = course;
//			this.pageHeadline = Labels.getLabel("txt.ui.menu.courseDetail");
			this.updateMode = true;
			this.courseYearSelected = course.getYear();
			// misto kurzu
			if (this.course.getCourseLocation() != null && this.courseLocationList != null && !this.courseLocationList.isEmpty()) {
				for (CourseLocation item : this.courseLocationList) {
					if (item.getUuid().toString().equals(this.course.getCourseLocation().getUuid().toString())) {
						this.course.setCourseLocation(item);
					}
				}
			}			
		} else {
			this.course = new Course();
//			this.pageHeadline = Labels.getLabel("txt.ui.menu.courseNew");
			this.updateMode = false;
			this.courseYearSelected = configurationService.getCourseApplicationYear();
			// misto kurzu
			if (this.courseLocationList != null && !this.courseLocationList.isEmpty()) {
				this.course.setCourseLocation(this.courseLocationList.get(0));
			}
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
	
	@Override
	@DependsOn("course")
	public String getPageHeadline() {
		if (this.course == null || this.course.getUuid() == null) {
			return Labels.getLabel("txt.ui.menu.courseNew");
		} else {
			return this.course.getName();
		}
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
		 addCoursePartic(true);
	}

	@Command
	public void addCourseParticipantsFromCourseCmd() {
		addCoursePartic(false);
	}
	
	private void addCoursePartic(boolean fromApplication) {
		Map<String, Object> args = new HashMap<>();
		args.put(WebConstants.COURSE_PARAM, this.course);
		args.put(WebConstants.COURSE_APPLICATION_PARAM, fromApplication);
		WebUtils.openModal("/pages/secured/ADMIN/participant-to-course-window.zul", null, args);
	}

	@Command
	public void newLessonCmd() {
		Lesson lesson = new Lesson();
		lesson.setCourseUuid(this.course.getUuid());
		EventQueueHelper.publish(ScbEventQueues.COURSE_APPLICATION_QUEUE, ScbEvent.LESSON_NEW_DATA_EVENT, null, lesson);
		WebUtils.openModal("/pages/secured/ADMIN/lesson-to-course-window.zul");
	}

	@Command
	public void detailCmd(@BindingParam("lesson") final Lesson lesson) {
		EventQueueHelper.publish(ScbEventQueues.COURSE_APPLICATION_QUEUE, ScbEvent.LESSON_DETAIL_DATA_EVENT, null, lesson);
		WebUtils.openModal("/pages/secured/ADMIN/lesson-to-course-window.zul");
	}
	
	@Command
	public void newItemCmd() {
		WebUtils.redirectToNewCourse();
	}
	
	@Command
    public void deleteCmd() {
		if (!getUpdateMode()) {
			return;
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("Deleting course with uuid: " + this.course.getUuid());
		}
		final Object[] msgParams = new Object[] {this.course.getName()};
		final UUID uuid = this.course.getUuid();
		MessageBoxUtils.showDefaultConfirmDialog(
			"msg.ui.quest.deleteCourse",
			"msg.ui.title.deleteRecord",
			new SzpEventListener() {
				@Override
				public void onOkEvent() {
					try {
						courseService.delete(uuid);
						WebUtils.showNotificationInfoAfterRedirect(Labels.getLabel("msg.ui.info.courseDeleted", msgParams));
						Executions.sendRedirect(WebPages.COURSE_LIST.getUrl());
					} catch (ScbValidationException e) {
						LOG.warn("ScbValidationException caught for course with uuid: " + uuid);
						WebUtils.showNotificationError(e.getMessage());
					}
				}
			},
			msgParams
		);
	}
	
	/**
	 * Otevre stranku pro odeslani emailu na emailove adresy vybranych ucastniku.
	 */
	@Command
	public void goToSendEmailCmd() {
		final Set<Contact> contactList = new HashSet<>();
		contactList.addAll(WebUtils.getParticEmailAddressList(this.course, courseService, scbUserService));
		
		goToSendEmailCore(contactList);
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

	public List<CourseLocation> getCourseLocationList() {
		return courseLocationList;
	}
	
}