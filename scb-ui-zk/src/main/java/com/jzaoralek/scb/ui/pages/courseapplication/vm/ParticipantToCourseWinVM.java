package com.jzaoralek.scb.ui.pages.courseapplication.vm;

import java.util.ArrayList;
import java.util.Collections;
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
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Window;

import com.jzaoralek.scb.dataservice.domain.Course;
import com.jzaoralek.scb.dataservice.domain.CourseApplication;
import com.jzaoralek.scb.dataservice.domain.CourseParticipant;
import com.jzaoralek.scb.dataservice.exception.ScbValidationException;
import com.jzaoralek.scb.dataservice.service.CourseApplicationService;
import com.jzaoralek.scb.dataservice.service.CourseService;
import com.jzaoralek.scb.ui.common.events.SzpEventListener;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEvent;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEventQueues;
import com.jzaoralek.scb.ui.common.utils.MessageBoxUtils;
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
	private List<CourseApplication> courseApplicationListBase;
	private List<CourseApplication> courseApplicationSelectedList;
	private boolean courseApplicationListVisible;
	private boolean selectCourseVisible;
	private boolean fromApplication;
	private CourseApplicationFilter filter = new CourseApplicationFilter();

	@Init
	public void init() {

		final EventQueue eq = EventQueues.lookup(ScbEventQueues.SDAT_COURSE_APPLICATION_QUEUE.name() , EventQueues.DESKTOP, true);
		eq.subscribe(new EventListener<Event>() {
			@Override
			public void onEvent(Event event) {
				if (event.getName().equals(ScbEvent.COURSE_UUID_FROM_APPLICATION_DATA_EVENT.name())) {
					initData((Course)event.getData(), true);
					eq.unsubscribe(this);
				} else if (event.getName().equals(ScbEvent.COURSE_UUID_FROM_COURSE_DATA_EVENT.name())) {
					initData((Course)event.getData(), false);
					eq.unsubscribe(this);
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

		this.courseApplicationListBase = this.courseApplicationList;

		BindUtils.postNotifyChange(null, null, this, "courseApplicationListVisible");
		BindUtils.postNotifyChange(null, null, this, "selectCourseVisible");
	}

	@NotifyChange("*")
	@Command
	public void refreshDataCmd() {
		initData(this.course, this.fromApplication);
		filter.setEmptyValues();
	}

	@Command
	@NotifyChange("courseApplicationList")
	public void filterDomCmd() {
		this.courseApplicationList = filter.getApplicationListFiltered(this.courseApplicationListBase);
	}

	@NotifyChange("*")
	@Command
	public void changeCourseCmd() {
		this.courseApplicationList = courseApplicationService.getInCourse(this.courseSelected.getUuid(), course.getYearFrom(), course.getYearTo());
		this.courseApplicationListVisible = true;
	}

	@Command
	public void submitCmd(@BindingParam("window") final Window window) {
		if (!CollectionUtils.isEmpty(this.courseApplicationSelectedList)) {
			final List<CourseParticipant> courseParticipantList = new ArrayList<CourseParticipant>();
			for (CourseApplication item : this.courseApplicationSelectedList) {
				courseParticipantList.add(item.getCourseParticipant());
			}

			final boolean fromApplication = this.fromApplication;
			final UUID courseSelectedUuid = this.courseSelected != null ? this.courseSelected.getUuid() : null;
			final UUID courseUuid = this.course.getUuid();

			// upozorneni pokud je jeden z pridavanych ucastniku soucasti jineho kurzu
			if (fromApplication && isParticipantInCourse(courseParticipantList)) {
				MessageBoxUtils.showDefaultConfirmDialog(
					"msg.ui.quest.assignInCourseParticipant",
					"txt.ui.common.assignParticipantToCourse",
					new SzpEventListener() {
						@Override
						public void onOkEvent() {
							saveParticipantList(window, courseParticipantList, fromApplication, courseUuid, courseSelectedUuid);
						}
					}
				);
			} else {
				saveParticipantList(window, courseParticipantList, fromApplication, courseUuid, courseSelectedUuid);
			}
		}
	}

	private void saveParticipantList(Window window, List<CourseParticipant> courseParticipantList, boolean fromApplication, UUID courseUuid, UUID courseSelectedUuid) {
		try {
			if (!fromApplication) {
				for (CourseParticipant item : courseParticipantList) {
					courseService.deleteParticipantFromCourse(item.getUuid(), courseSelectedUuid);
				}
			}
			// ulozit do databaze
			courseService.storeCourseParticipants(courseParticipantList, courseUuid);
			EventQueueHelper.publish(ScbEventQueues.SDAT_COURSE_APPLICATION_QUEUE, ScbEvent.RELOAD_COURSE_PARTICIPANT_DATA_EVENT, null, courseUuid);
			window.detach();
		} catch (ScbValidationException e) {
			LOG.warn("ScbValidationException caught during addin courseParticipantList to course uuid: " + courseUuid);
			WebUtils.showNotificationError(e.getMessage());
		}
	}

	private boolean isParticipantInCourse(List<CourseParticipant> courseParticipantList) {
		if (CollectionUtils.isEmpty(courseParticipantList)) {
			return false;
		}
		boolean ret = false;
		for (CourseParticipant item : courseParticipantList) {
			if (item.inCourse()) {
				ret = true;
				break;
			}
		}

		return ret;
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

	public CourseApplicationFilter getFilter() {
		return filter;
	}

	public static class CourseApplicationFilter {
		private String courseParticName;
		private String courseParticNameLc;
		private String birthNo;
		private String courseParticRepresentative;
		private String courseParticRepresentativeLc;
		private Boolean inCourse;

		public boolean matches(String courseParticNameIn, String birthNoIn, String courseParticRepresentativeIn, boolean inCourseIn, boolean emptyMatch) {
			if (courseParticName == null && birthNo == null && courseParticRepresentative == null && inCourse == null) {
				return emptyMatch;
			}
			if (courseParticName != null && !courseParticNameIn.toLowerCase().contains(courseParticNameLc)) {
				return false;
			}
			if (birthNo != null && !birthNoIn.contains(birthNo)) {
				return false;
			}
			if (courseParticRepresentative != null && !courseParticRepresentativeIn.toLowerCase().contains(courseParticRepresentativeLc)) {
				return false;
			}
			if (inCourse != null && (inCourse != inCourseIn)) {
				return false;
			}
			return true;
		}

		public List<CourseApplication> getApplicationListFiltered(List<CourseApplication> codelistModelList) {
			if (codelistModelList == null || codelistModelList.isEmpty()) {
				return Collections.<CourseApplication>emptyList();
			}
			List<CourseApplication> ret = new ArrayList<CourseApplication>();
			for (CourseApplication item : codelistModelList) {
				if (matches(item.getCourseParticipant().getContact().getSurname() + " " + item.getCourseParticipant().getContact().getFirstname()
						, item.getCourseParticipant().getPersonalNo()
						, item.getCourseParticRepresentative().getContact().getSurname() + " " + item.getCourseParticRepresentative().getContact().getFirstname()
						, item.getCourseParticipant().inCourse()
						, true)) {
					ret.add(item);
				}
			}
			return ret;
		}

		public String getCourseParticName() {
			return courseParticName == null ? "" : courseParticName;
		}
		public void setCourseParticName(String name) {
			this.courseParticName = StringUtils.hasText(name) ? name.trim() : null;
			this.courseParticNameLc = this.courseParticName == null ? null : this.courseParticName.toLowerCase();
		}

		public String getCourseParticRepresentative() {
			return courseParticRepresentative == null ? "" : courseParticRepresentative;
		}

		public void setCourseParticRepresentative(String courseParticRepresentative) {
			this.courseParticRepresentative = StringUtils.hasText(courseParticRepresentative) ? courseParticRepresentative.trim() : null;
			this.courseParticRepresentativeLc = this.courseParticRepresentative == null ? null : this.courseParticRepresentative.toLowerCase();
		}

		public String getBirthNo() {
			return birthNo == null ? "" : birthNo;
		}

		public void setBirthNo(String birthNo) {
			this.birthNo = StringUtils.hasText(birthNo) ? birthNo.trim() : null;
		}

		public Boolean getInCourse() {
			return inCourse;
		}

		public void setInCourse(Boolean inCourse) {
			this.inCourse = inCourse;
		}

		public void setEmptyValues() {
			courseParticName = null;
			courseParticNameLc = null;
			birthNo = null;
			courseParticRepresentative = null;
			inCourse = null;
		}
	}
}