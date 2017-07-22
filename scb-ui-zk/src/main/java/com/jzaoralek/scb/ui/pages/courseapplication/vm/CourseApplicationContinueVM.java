package com.jzaoralek.scb.ui.pages.courseapplication.vm;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.QueryParam;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import com.jzaoralek.scb.dataservice.domain.CourseApplication;
import com.jzaoralek.scb.dataservice.domain.CourseParticipant;
import com.jzaoralek.scb.dataservice.domain.ScbUser;
import com.jzaoralek.scb.dataservice.exception.ScbValidationException;
import com.jzaoralek.scb.dataservice.service.CourseApplicationService;
import com.jzaoralek.scb.dataservice.service.CourseService;
import com.jzaoralek.scb.dataservice.service.ScbUserService;
import com.jzaoralek.scb.dataservice.utils.SecurityUtils;
import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.utils.JasperUtil;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.BaseVM;

/**
 * View model for create course application through link.
 * @author jakub.zaoralek
 *
 */
public class CourseApplicationContinueVM extends BaseVM {

	private static final Logger LOG = LoggerFactory.getLogger(CourseApplicationVM.class);
	
	@WireVariable
	private ScbUserService scbUserService;
	
	@WireVariable
	private CourseService courseService;
	
	@WireVariable
	private CourseApplicationService courseApplicationService;
	
	private boolean showErrMessage;
	private boolean showWarnMessage;
	private boolean showSuccessMessage;
	private String message;
	private Pair<Integer, Integer> yearFromTo;
	
	@Init
	public void init(@QueryParam(WebConstants.COURSE_PARTICIPANT_UUID_PARAM) String particUuid
			, @QueryParam(WebConstants.COURSE_PARTICIPANT_REPRESENTATIVE_UUID_PARAM) String particRepresentativeUuid) {
		
		LOG.info("Creating application through link, participantUuid: {}, representativeUuid: {}", particRepresentativeUuid);
		
		if (!StringUtils.hasText(particUuid) || !StringUtils.hasText(particRepresentativeUuid)) {
			this.showErrMessage = true;
			return;
		}
		
		// check if participant from prev year exists
		CourseParticipant courseParticipant = courseService.getCourseParticipantByUuid(UUID.fromString(particUuid));
		if (courseParticipant == null) {
			LOG.warn("Invalid course participant: {}", particUuid);
			this.showErrMessage = true;
			return;
		}
		
		// check if participant representative exists
		ScbUser particRepresentative = scbUserService.getByUuid(UUID.fromString(particRepresentativeUuid));
		if (particRepresentative == null) {
			LOG.warn("Invalid course participant representative: {}", particRepresentativeUuid);
			this.showErrMessage = true;
			return;
		}
		
		// check if user belongs to participant
		Collection<CourseParticipant> courseParticipantList = courseService.getCourseParticListByRepresentativeUuid(particRepresentative.getUuid());
		boolean found = false;
		for (CourseParticipant item : courseParticipantList) {
			if (item.getUuid().toString().equals(courseParticipant.getUuid().toString())) {
				found = true;
				break;
			}
		}
		
		if (!found) {
			LOG.warn("Course participant: {} doesn't belong to course representative: {}", "particUuid", particRepresentativeUuid);
			this.showErrMessage = true;
			return;
		}
		
		// check if course participant isn't already registered to course
		List<CourseApplication> courseApplicationList = courseApplicationService.getByCourseParticipantUuid(UUID.fromString(particUuid));
		this.yearFromTo = configurationService.getYearFromTo();
		Integer yearFrom = this.yearFromTo.getValue0();
		Integer yearTo = this.yearFromTo.getValue1();
		for (CourseApplication item : courseApplicationList) {
			if (item.getYearFrom() == yearFrom && item.getYearTo() == yearTo) {
				LOG.warn("Course participant: {} already has registered to course to year from: {} to: {}", particUuid, yearFrom, yearTo);
				this.message = Labels.getLabel("msg.ui.warn.userHaveRegisteredToCourse", new Object[] {courseParticipant.getContact().getCompleteName(), String.valueOf(yearFrom), String.valueOf(yearTo)});
				this.showWarnMessage = true;
				return;
			}
		}
		
		createNewCourseApplication(courseParticipant, particRepresentative);
		this.message = Labels.getLabel("msg.ui.info.applicationForParticipantCreated", new Object[] {courseParticipant.getContact().getCompleteName(), String.valueOf(yearFrom), String.valueOf(yearTo)});
		this.showSuccessMessage = true;		
	}
	
	private void createNewCourseApplication(CourseParticipant courseParticipant, ScbUser courseParticipantRepresentative) {
		CourseApplication courseApplication = new CourseApplication();
		courseApplication.setCourseParticipant(courseParticipant);
		courseApplication.setCourseParticRepresentative(courseParticipantRepresentative);
		courseApplication.setYearFrom(this.yearFromTo.getValue0());
		courseApplication.setYearTo(this.yearFromTo.getValue1());
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("Creating application: " + courseApplication);
		}
		
		try {
			courseApplicationService.store(courseApplication);
			byte[] byteArray = JasperUtil.getReport(courseApplication, Labels.getLabel("txt.ui.menu.applicationWithYear", new Object[] {courseApplication.getYearFrom()}));
			this.attachment = buildCourseApplicationAttachment(courseApplication, byteArray);			
			sendMail(courseApplication, Labels.getLabel("txt.ui.menu.applicationWithYear", new Object[] {String.valueOf(courseApplication.getYearFrom())+"/"+String.valueOf(courseApplication.getYearTo())}));
		} catch (ScbValidationException e) {
			LOG.error("ScbValidationException caught for application: " + courseApplication, e);
			WebUtils.showNotificationError(e.getMessage());
		}
	}
	
	public String getBorderLayoutCenterStyle() {
		return "background:#dfe8f6 url('"+Executions.getCurrent().getContextPath()+"/resources/img/background2014Full.jpg') no-repeat center center;";
	}
	
	public boolean isShowErrMessage() {
		return showErrMessage;
	}
	
	public boolean isShowWarnMessage() {
		return showWarnMessage;
	}

	public boolean isShowSuccessMessage() {
		return showSuccessMessage;
	}
	
	public String getMessage() {
		return message;
	}
}
