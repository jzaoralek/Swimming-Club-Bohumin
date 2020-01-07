package com.jzaoralek.scb.ui.pages.courseapplication.vm;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import com.jzaoralek.scb.dataservice.domain.Course;
import com.jzaoralek.scb.dataservice.domain.ScbUserRole;
import com.jzaoralek.scb.dataservice.exception.ScbValidationException;
import com.jzaoralek.scb.dataservice.service.CourseService;
import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.WebPages;
import com.jzaoralek.scb.ui.common.events.SzpEventListener;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper;
import com.jzaoralek.scb.ui.common.utils.MessageBoxUtils;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEvent;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEventQueues;
import com.jzaoralek.scb.ui.common.vm.BaseContextVM;

/**
 * Parent for CourseListVM and CourseVM.
 *
 */
public abstract class CourseAbstractVM extends BaseContextVM {

	protected static final Logger LOG = LoggerFactory.getLogger(CourseVM.class);
	
	@WireVariable
	protected CourseService courseService;
	
	@Command
	public void newItemCmd() {
		WebUtils.redirectToNewCourse();
	}
	
	@Command
	public void copyItemCmd(@BindingParam(WebConstants.UUID_PARAM) UUID uuid, 
			@BindingParam(WebConstants.NAME_PARAM) String courseName) {
		// check user role
		if (!isLoggedUserInRole(ScbUserRole.ADMIN.name())) {
			return;
		}
				
		if (LOG.isDebugEnabled()) {
			LOG.debug("Copying course uuid: " + uuid);
		}
		
		final String actualYear = configurationService.getCourseApplicationYear();
		final Object[] msgParams = new Object[] {courseName, actualYear};
		
		MessageBoxUtils.showDefaultConfirmDialog(
			"msg.ui.quest.copyCourse",
			"msg.ui.title.copyRecord",
			new SzpEventListener() {
				@Override
				public void onOkEvent() {
					Course courseNew = null;
					try {
						// copying course
						courseNew = courseService.copy(uuid, actualYear);
						// redirect to new course
						Executions.sendRedirect("/pages/secured/ADMIN/kurz.zul?"+WebConstants.UUID_PARAM+"="+courseNew.getUuid().toString() + "&" + WebConstants.FROM_PAGE_PARAM + "=" + WebPages.COURSE_LIST);
						WebUtils.showNotificationInfoAfterRedirect(Labels.getLabel("msg.ui.info.courseCopiedFromCourse", new Object[] {courseNew.getName(), courseName}));
					} catch (ScbValidationException e) {
						LOG.warn("ScbValidationException caught for course: " + courseNew, e);
						WebUtils.showNotificationError(e.getMessage());
					} catch (Exception e) {
						LOG.error("Unexpected exception caught for course: " + courseNew, e);
						throw new RuntimeException(e);
					}
				}
			},
			msgParams
		);
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
}
