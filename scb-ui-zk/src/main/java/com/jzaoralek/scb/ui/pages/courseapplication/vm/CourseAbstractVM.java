package com.jzaoralek.scb.ui.pages.courseapplication.vm;

import java.util.Objects;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Popup;

import com.jzaoralek.scb.dataservice.domain.Course;
import com.jzaoralek.scb.dataservice.domain.Course.CourseType;
import com.jzaoralek.scb.dataservice.domain.ScbUserRole;
import com.jzaoralek.scb.dataservice.exception.ScbValidationException;
import com.jzaoralek.scb.dataservice.service.CourseService;
import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.WebPages;
import com.jzaoralek.scb.ui.common.events.SzpEventListener;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEvent;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEventQueues;
import com.jzaoralek.scb.ui.common.utils.MessageBoxUtils;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.BaseContextVM;

/**
 * Parent for CourseListVM and CourseVM.
 *
 */
public abstract class CourseAbstractVM extends BaseContextVM {

	protected static final Logger LOG = LoggerFactory.getLogger(CourseVM.class);
	
	@WireVariable
	protected CourseService courseService;
	
	protected Course courseCopy;
	private String copyFrom;
	private UUID courseUuidFrom;
	protected boolean copyParticipants;
	protected boolean copyLessons;
	protected boolean copyTrainers;
	protected CourseType copyCourseType;
	protected String copyCourseName;
	protected String copyCourseYear;
	
	@Wire
	protected Popup courseCopyPopup;
	
	@AfterCompose
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
		Selectors.wireComponents(view, this, false);
	}

	@Command
	public void newItemCmd() {
		WebUtils.redirectToNewCourse();
	}
	
	@NotifyChange({"courseCopy","copyCourseType","copyCourseName","copyCourseYear"})
	@Command
	public void buildCourseCopyCmd(@BindingParam(WebConstants.UUID_PARAM) UUID uuid, 
			@BindingParam(WebConstants.NAME_PARAM) String courseName, 
			@BindingParam(WebConstants.COMPONENT_PARAM) Component component) {
		// check user role
		if (!isLoggedUserInRole(ScbUserRole.ADMIN.name())) {
			return;
		}
		
		this.courseUuidFrom = uuid;
		this.courseCopy = courseService.buildCopy(uuid, configurationService.getCourseApplicationYear(), true);
		this.copyCourseType = this.courseCopy.getCourseType();
		this.copyCourseName = this.courseCopy.getName();
		this.copyCourseYear = this.courseCopy.getYear();
		this.copyFrom = this.courseCopy.getName();
		
		courseCopyPopup.open(component);
	}
	
	@Command
	public void copyItemCmd() {
		Objects.requireNonNull(this.courseCopy, "courseCopy is null");
		
		// check user role
		if (!isLoggedUserInRole(ScbUserRole.ADMIN.name())) {
			return;
		}
				
		if (LOG.isDebugEnabled()) {
			LOG.debug("Copying course uuid: " + this.courseUuidFrom);
		}
		
		Course courseNew = null;
		try {
			// copying course
			this.courseCopy.setCourseType(this.copyCourseType);
			this.courseCopy.setName(this.copyCourseName);
			this.courseCopy.fillYearFromTo(this.copyCourseYear);
			courseNew = courseService.copy(this.courseUuidFrom, this.courseCopy, this.copyParticipants, this.copyLessons, this.copyTrainers);
			// redirect to new course
			Executions.sendRedirect("/pages/secured/ADMIN/kurz.zul?"+WebConstants.UUID_PARAM+"="+courseNew.getUuid().toString() + "&" + WebConstants.FROM_PAGE_PARAM + "=" + WebPages.COURSE_LIST);
			WebUtils.showNotificationInfoAfterRedirect(Labels.getLabel("msg.ui.info.courseCopiedFromCourse", new Object[] {courseNew.getName(), this.copyFrom}));
		} catch (ScbValidationException e) {
			LOG.warn("ScbValidationException caught for course: " + courseNew, e);
			WebUtils.showNotificationError(e.getMessage());
		} catch (Exception e) {
			LOG.error("Unexpected exception caught for course: " + courseNew, e);
			throw new RuntimeException(e);
		}
	}
	
	@Command
	public void closeCopyPopupCmd() {
		this.courseCopyPopup.close();
	}
	
	protected void deleteCore(Course course, boolean redirectAfterAction) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Deleting course with uuid: " + course.getUuid());
		}
		final Object[] msgParams = new Object[] {course.getName()};
		final UUID uuid = course.getUuid();
		MessageBoxUtils.showDefaultConfirmDialog(
			"msg.ui.quest.deleteCourse",
			"msg.ui.title.deleteRecord",
			new SzpEventListener() {
				@Override
				public void onOkEvent() {
					try {
						courseService.delete(uuid);
						if (redirectAfterAction) {
							WebUtils.showNotificationInfoAfterRedirect(Labels.getLabel("msg.ui.info.courseDeleted", msgParams));
							Executions.sendRedirect(WebPages.COURSE_LIST.getUrl());							
						} else {
							WebUtils.showNotificationInfo(Labels.getLabel("msg.ui.info.courseDeleted", msgParams));
							EventQueueHelper.publish(ScbEventQueues.COURSE_APPLICATION_QUEUE, ScbEvent.RELOAD_COURSE_DATA_EVENT, null, null);
						}
					} catch (ScbValidationException e) {
						LOG.warn("ScbValidationException caught during deleting course uuid: " + uuid, e);
						WebUtils.showNotificationError(e.getMessage());
					}
				}
			},
			msgParams
		);
	}
	
	public Course getCourseCopy() {
		return courseCopy;
	}
	public void setCourseCopy(Course courseCopy) {
		this.courseCopy = courseCopy;
	}
	public boolean isCopyParticipants() {
		return copyParticipants;
	}
	public void setCopyParticipants(boolean copyParticipants) {
		this.copyParticipants = copyParticipants;
	}
	public boolean isCopyLessons() {
		return copyLessons;
	}
	public void setCopyLessons(boolean copyLessons) {
		this.copyLessons = copyLessons;
	}
	public boolean isCopyTrainers() {
		return copyTrainers;
	}
	public void setCopyTrainers(boolean copyTrainers) {
		this.copyTrainers = copyTrainers;
	}
	public CourseType getCopyCourseType() {
		return copyCourseType;
	}
	public void setCopyCourseType(CourseType copyCourseType) {
		this.copyCourseType = copyCourseType;
	}
	public String getCopyCourseName() {
		return copyCourseName;
	}
	public void setCopyCourseName(String copyCourseName) {
		this.copyCourseName = copyCourseName;
	}
	public String getCopyCourseYear() {
		return copyCourseYear;
	}
	public void setCopyCourseYear(String copyCourseYear) {
		this.copyCourseYear = copyCourseYear;
	}
}
