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
		
		Course courseNew = null;
		try {
			courseNew = courseService.copy(uuid, configurationService.getCourseApplicationYear());
			
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
}
