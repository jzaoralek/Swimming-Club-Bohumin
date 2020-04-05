package com.jzaoralek.scb.ui.pages.courseapplication.vm;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Window;

import com.jzaoralek.scb.dataservice.domain.Course;
import com.jzaoralek.scb.dataservice.domain.CourseApplication;
import com.jzaoralek.scb.dataservice.domain.CourseCourseParticipantVO;
import com.jzaoralek.scb.dataservice.domain.CourseParticipant;
import com.jzaoralek.scb.dataservice.exception.ScbValidationException;
import com.jzaoralek.scb.dataservice.service.CourseApplicationService;
import com.jzaoralek.scb.dataservice.service.CourseService;
import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.events.SzpEventListener;
import com.jzaoralek.scb.ui.common.utils.DateUtil;
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
		this.course = (Course) WebUtils.getArg(WebConstants.COURSE_PARAM);
		boolean fromApplication = (Boolean)WebUtils.getArg(WebConstants.COURSE_APPLICATION_PARAM);
		initData(this.course, fromApplication);
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
			
			// get all courses for year and exclude target course
			this.courseList = courseService.getAll(this.course.getYearFrom(), this.course.getYearTo(), false);
			if (!CollectionUtils.isEmpty(this.courseList)) {
				this.courseList.removeIf(i -> i.getUuid().toString().equals(this.course.getUuid().toString()));				
			}
			
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
		if (CollectionUtils.isEmpty(courseParticipantList)) {
			return;
		}
		try {
			if (!fromApplication) {
				// Presunuti z jineho kurzu
				courseService.moveParticListToCourse(courseParticipantList, 
						this.course.getUuid(), 
						courseSelectedUuid, 
						new GregorianCalendar(this.course.getYearFrom(),6,1),
						new GregorianCalendar(this.course.getYearTo(),5,30));
			} else {
				// Prirazeni z prihlasky
				courseService.storeCourseParticipants(courseParticipantList, courseUuid);
			}
			EventQueueHelper.publish(ScbEventQueues.COURSE_APPLICATION_QUEUE, ScbEvent.RELOAD_COURSE_PARTICIPANT_DATA_EVENT, null, courseUuid);
			window.detach();
		} catch (ScbValidationException e) {
			LOG.warn("ScbValidationException caught during adding courseParticipantList to course uuid: " + courseUuid);
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