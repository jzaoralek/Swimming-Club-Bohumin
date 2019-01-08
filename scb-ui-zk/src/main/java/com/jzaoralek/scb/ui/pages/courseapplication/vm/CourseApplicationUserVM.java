package com.jzaoralek.scb.ui.pages.courseapplication.vm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.bind.annotation.QueryParam;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import com.jzaoralek.scb.dataservice.domain.Course;
import com.jzaoralek.scb.dataservice.domain.CourseApplication;
import com.jzaoralek.scb.dataservice.domain.CourseLocation;
import com.jzaoralek.scb.dataservice.domain.CourseParticipant;
import com.jzaoralek.scb.dataservice.exception.ScbValidationException;
import com.jzaoralek.scb.dataservice.service.CourseApplicationService;
import com.jzaoralek.scb.dataservice.service.CourseService;
import com.jzaoralek.scb.dataservice.utils.SecurityUtils;
import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.WebPages;
import com.jzaoralek.scb.ui.common.template.SideMenuComposer.ScbMenuItem;
import com.jzaoralek.scb.ui.common.utils.JasperUtil;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.BaseVM;
import com.jzaoralek.scb.ui.pages.courseparticipant.vm.CourseParticipantListVM;

/**
 * VM pro prihhlaseni ucastnika na kurz.
 *
 */
public class CourseApplicationUserVM extends BaseVM {
	
	private static final Logger LOG = LoggerFactory.getLogger(CourseParticipantListVM.class);
	
	@WireVariable
	private CourseApplicationService courseApplicationService;
	
	@WireVariable
	private CourseService courseService;
	
	private CourseParticipant courseParticipant;
	private CourseApplication application;
	private List<CourseLocation> courseLocationList;
	private List<Course> courseListAll;
	private List<Course> courseList;
	private boolean courseSelectionRequired;
	private Set<Course> courseSelected;
	private boolean editMode = true;
	private boolean securedMode = false;
	private CourseLocation courseLocationSelected;

	@Init
	public void init(@QueryParam(WebConstants.UUID_PARAM) String uuid, @QueryParam(WebConstants.FROM_PAGE_PARAM) String fromPage) {
		setMenuSelected(ScbMenuItem.SEZNAM_UCASTNIKU_U);
		setReturnPage(fromPage);
		
		if (!StringUtils.hasText(uuid)) {
			// na vstupu neni uuid ucastnika
			showInvalidRequest();
			return;
		}
		
		this.courseParticipant = courseService.getCourseParticipantByUuid(UUID.fromString(uuid));
		
		if (this.courseParticipant == null) {
			// na zaklade uuid nebyl nalezen zadny ucastnik
			showInvalidRequest();
			return;
		}
		
		this.courseSelectionRequired = configurationService.isCourseSelectionRequired();
		if (!this.courseSelectionRequired) {
			// vyber kurzu neni povolen
			showInvalidRequest();
			return;
		}
		
		this.application = new CourseApplication();
		this.application.fillYearFromTo(configurationService.getCourseApplicationYear());
		
		// seznam mist konani
		this.courseLocationList = courseService.getCourseLocationAll();
		// seznam vsech kurzu
		this.courseListAll = courseService.getAll(this.application.getYearFrom(), this.application.getYearTo(), true);
		
		this.pageHeadline = this.courseParticipant.getContact().getCompleteName() + " - " + Labels.getLabel("txt.ui.loginToCourse");	
	}
	
	@NotifyChange("courseList")
	@Command
	public void courseLocationSelectCmd() {
		if (this.courseListAll == null || this.courseListAll.isEmpty() || this.courseLocationSelected == null) {
			return;
		}
		
		if (this.courseList == null) {
			this.courseList = new ArrayList<>();
		}
		
		this.courseList.clear();
		
		for (Course courseItem : this.courseListAll) {
			if (courseItem.getCourseLocation().getUuid().toString().equals(this.courseLocationSelected.getUuid().toString())) {
				this.courseList.add(courseItem);
			}
		}
	}
	
	@NotifyChange("*")
	@Command
	public void submit() {
		Course courseSelected = this.courseSelected.iterator().next();
		if (courseSelected == null) {
			return;
		}
		// kontrola zda-li jit neni ucastnik zarazen do kurzu
		List<CourseParticipant> courseParticipantInCourseList = courseService.getByCourseParticListByCourseUuid(courseSelected.getUuid(), true);
		for (CourseParticipant item : courseParticipantInCourseList) {
			if (item.getUuid().toString().equals(this.courseParticipant.getUuid().toString())) {
				WebUtils.showNotificationWarning(Labels.getLabel("msg.ui.warn.participantAlreadyInCourse", new Object[] {courseSelected.getName()}));
				return;
			}
		}
		createNewCourseApplication(courseService.getCourseParticipantByUuid(courseParticipant.getUuid()));	
		WebUtils.showNotificationInfoAfterRedirect(Labels.getLabel("msg.ui.warn.participantLoggedToCourse", new Object[] {courseParticipant.getContact().getCompleteName(), courseSelected.getName()}));
		detail(courseParticipant.getUuid());
	}
	
	public String getCourseRowColor(Course course) {
		if (course == null) {
			return null;
		}
		
		if (this.securedMode) {
			return "";
		}
		
		if (course.isFullOccupancy()) {
			return "background: #F0F0F0";
		}
		
		return "";
	}
	
	private void detail(final UUID uuid) {
		if (uuid ==  null) {
			throw new IllegalArgumentException("uuid is null");
		}
		String targetPage = WebPages.USER_PARTICIPANT_DETAIL.getUrl();
		WebPages fromPage = WebPages.USER_PARTICIPANT_LIST;
		Executions.sendRedirect(targetPage + "?"+WebConstants.UUID_PARAM+"="+uuid.toString() + "&" + WebConstants.FROM_PAGE_PARAM + "=" + fromPage);
	}
	
	private void createNewCourseApplication(CourseParticipant courseParticipant) {
		this.application.setCourseParticRepresentative(SecurityUtils.getLoggedUser());
		this.application.setCourseParticipant(courseParticipant);
		
		// pokud byl vybran kurz, potreba zkontrolovat zda-li uz neni zaplnen
		if (this.courseSelectionRequired && this.courseSelected != null && !this.courseSelected.isEmpty()) {
			Course selectedCourseDb = courseService.getByUuid(this.courseSelected.iterator().next().getUuid());
			if (selectedCourseDb == null) {
				WebUtils.showNotificationWarning(Labels.getLabel("msg.ui.warn.courseIsDeleted", new Object[] {this.courseSelected.iterator().next().getName()}));
				// reload seznamu kurzu
				this.courseList = courseService.getAll(application.getYearFrom(), application.getYearTo(), true);
				this.courseSelected.clear();
				return;
			}
			if (selectedCourseDb.isFullOccupancy()) {
				WebUtils.showNotificationWarning(Labels.getLabel("msg.ui.warn.courseIsFull", new Object[] {this.courseSelected.iterator().next().getName()}));
				// reload seznamu kurzu
				this.courseList = courseService.getAll(application.getYearFrom(), application.getYearTo(), true);
				this.courseSelected.clear();
				return;
			}
		}
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("Creating application: " + application);
		}
		
		try {
			courseApplicationService.store(application);
			WebUtils.showNotificationInfo(Labels.getLabel("msg.ui.info.applicationSend"));
			
			// prihlaseni rovnou do kurzu
			if (this.courseSelected != null && !this.courseSelected.isEmpty()) {
				courseService.storeCourseParticipants(Arrays.asList(application.getCourseParticipant()), this.courseSelected.iterator().next().getUuid());
				application.getCourseParticipant().setCourseList(new ArrayList<>(this.courseSelected));
			}
						
			byte[] byteArray = JasperUtil.getReport(application, Labels.getLabel("txt.ui.menu.applicationWithYear", new Object[] {application.getYearFrom()}), configurationService);
			this.attachment = buildCourseApplicationAttachment(application, byteArray);
			
			sendMail(application, this.pageHeadline);
		} catch (ScbValidationException e) {
			LOG.warn("ScbValidationException caught for application: " + application);
			WebUtils.showNotificationError(e.getMessage());
		}
	}
	
	private void showInvalidRequest() {
		WebUtils.showNotificationError(Labels.getLabel("msg.ui.error.invalidRequest"));
	}
	
	public List<CourseLocation> getCourseLocationList() {
		return courseLocationList;
	}
	public List<Course> getCourseListAll() {
		return courseListAll;
	}
	public List<Course> getCourseList() {
		return courseList;
	}
	public boolean isCourseSelectionRequired() {
		return courseSelectionRequired;
	}
	public boolean isEditMode() {
		return editMode;
	}
	public boolean isSecuredMode() {
		return securedMode;
	}
	public Set<Course> getCourseSelected() {
		return courseSelected;
	}
	public void setCourseSelected(Set<Course> courseSelected) {
		this.courseSelected = courseSelected;
	}
	public CourseLocation getCourseLocationSelected() {
		return courseLocationSelected;
	}
	public void setCourseLocationSelected(CourseLocation courseLocationSelected) {
		this.courseLocationSelected = courseLocationSelected;
	}
}
