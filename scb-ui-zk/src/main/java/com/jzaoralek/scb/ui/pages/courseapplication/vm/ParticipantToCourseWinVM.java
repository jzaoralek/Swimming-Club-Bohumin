package com.jzaoralek.scb.ui.pages.courseapplication.vm;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Window;

import com.jzaoralek.scb.dataservice.domain.Course;
import com.jzaoralek.scb.dataservice.domain.CourseApplication;
import com.jzaoralek.scb.dataservice.domain.CourseParticipant;
import com.jzaoralek.scb.dataservice.exception.ScbValidationException;
import com.jzaoralek.scb.dataservice.service.CourseApplicationService;
import com.jzaoralek.scb.dataservice.service.CourseService;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEvent;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEventQueues;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.BaseVM;

public class ParticipantToCourseWinVM extends BaseVM {

	private static final Logger LOG = LoggerFactory.getLogger(ParticipantToCourseWinVM.class);

	@WireVariable
	private CourseApplicationService courseApplicationService;

	@WireVariable
	private CourseService courseService;

	private Course course;
	private List<Course> courseList;
	private Course courseSelected;
	private List<CourseApplication> courseApplicationList;
	private List<CourseApplication> courseApplicationSelectedList;
	private boolean courseApplicationListVisible;
	private boolean selectCourseVisible;
	private boolean fromApplication;

	@Init
	public void init() {

		EventQueues.lookup(ScbEventQueues.SDAT_COURSE_APPLICATION_QUEUE.name() , EventQueues.DESKTOP, true).subscribe(new EventListener<Event>() {
			@Override
			public void onEvent(Event event) {
				if (event.getName().equals(ScbEvent.COURSE_UUID_FROM_APPLICATION_DATA_EVENT.name())) {
					initData((Course)event.getData(), true);
				} else if (event.getName().equals(ScbEvent.COURSE_UUID_FROM_COURSE_DATA_EVENT.name())) {
					initData((Course)event.getData(), false);
				}
			}
		});
	}

	private void initData(Course course, boolean fromApplication) {
		if (course == null) {
			return;
		}
		this.fromApplication = fromApplication;
		this.course = course;
		if (fromApplication) {
			this.courseApplicationList = courseApplicationService.getNotInCourse(this.course.getUuid(), course.getYearFrom(), course.getYearTo());
			this.courseApplicationListVisible = true;
			this.selectCourseVisible = false;
			BindUtils.postNotifyChange(null, null, this, "courseApplicationList");
		} else {
			this.courseApplicationList = null;
			this.courseList = courseService.getAllExceptCourse(this.course.getUuid());
			this.courseApplicationListVisible = false;
			this.selectCourseVisible = true;
			BindUtils.postNotifyChange(null, null, this, "courseList");
		}

		BindUtils.postNotifyChange(null, null, this, "courseApplicationListVisible");
		BindUtils.postNotifyChange(null, null, this, "selectCourseVisible");
	}

	@NotifyChange("*")
	@Command
	public void refreshDataCmd() {
		// initData(this.course, this.fromApplicationMode);
		//filter.setEmptyValues();
	}

	@NotifyChange("*")
	@Command
	public void changeCourseCmd() {
		this.courseApplicationList = courseApplicationService.getInCourse(this.courseSelected.getUuid(), course.getYearFrom(), course.getYearTo());
		this.courseApplicationListVisible = true;
	}

	@Command
	public void submitCmd(@BindingParam("window") Window window) {
		if (!CollectionUtils.isEmpty(this.courseApplicationSelectedList)) {
			try {
				List<CourseParticipant> courseParticipantList = new ArrayList<CourseParticipant>();
				for (CourseApplication item : this.courseApplicationSelectedList) {
					courseParticipantList.add(item.getCourseParticipant());
				}
				// TODO: v pripade preneseni z kurzu odebrat z puvodniho
				if (!this.fromApplication) {
					for (CourseParticipant item : courseParticipantList) {
						courseService.deleteParticipantFromCourse(item.getUuid(), this.courseSelected.getUuid());
					}
				}
				// ulozit do databaze
				courseService.storeCourseParticipants(courseParticipantList, this.course.getUuid());
				EventQueueHelper.publish(ScbEventQueues.SDAT_COURSE_APPLICATION_QUEUE, ScbEvent.RELOAD_COURSE_PARTICIPANT_DATA_EVENT, null, this.course.getUuid());
				window.detach();
			} catch (ScbValidationException e) {
				LOG.error("ScbValidationException caught during addin courseParticipantList to course uuid: " + this.course, e);
				WebUtils.showNotificationError(e.getMessage());
			}
		}
	}

	public List<CourseApplication> getCourseApplicationList() {
		return courseApplicationList;
	}

	public List<CourseApplication> getCourseApplicationSelectedList() {
		return courseApplicationSelectedList;
	}

	public void setCourseApplicationSelectedList(List<CourseApplication> courseApplicationSelectedList) {
		this.courseApplicationSelectedList = courseApplicationSelectedList;
	}

	public List<Course> getCourseList() {
		return courseList;
	}

	public Course getCourseSelected() {
		return courseSelected;
	}

	public void setCourseSelected(Course courseSelected) {
		this.courseSelected = courseSelected;
	}

	public boolean isCourseApplicationListVisible() {
		return courseApplicationListVisible;
	}

	public boolean isSelectCourseVisible() {
		return selectCourseVisible;
	}
}