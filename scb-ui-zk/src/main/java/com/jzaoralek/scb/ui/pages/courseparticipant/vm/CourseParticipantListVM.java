package com.jzaoralek.scb.ui.pages.courseparticipant.vm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Popup;

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
import com.jzaoralek.scb.ui.common.utils.JasperUtil;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.BaseVM;
import com.jzaoralek.scb.ui.pages.courseapplication.vm.CourseApplicationVM;

public class CourseParticipantListVM extends BaseVM {

	private static final Logger LOG = LoggerFactory.getLogger(CourseApplicationVM.class);
	
	@WireVariable
	private CourseService courseService;
	
	@WireVariable
	private CourseApplicationService courseApplicationService;
	
	private List<CourseParticipant> courseParticipantList;
	private Pair<Integer, Integer> yearFromTo;
	private String newCourseApplicationButtonTooltipText;
	private String newCourseApplicationButtonText;
	private String newCourseParticipantButtonText;
	private boolean courseApplicationAllowed;
	private CourseParticipant newCourseParticipant;
	private List<Course> courseList;
	private List<Course> courseListAll;
	private Set<Course> courseSelected;
	private boolean courseSelectionRequired;
	private List<CourseLocation> courseLocationList;
	private CourseLocation courseLocationSelected;

	@Init
	public void init() {
		this.courseParticipantList = courseService.getCourseParticListByRepresentativeUuid(SecurityUtils.getLoggedUser().getUuid());
		this.yearFromTo = configurationService.getYearFromTo();
		this.courseApplicationAllowed = configurationService.isCourseApplicationsAllowed();
		this.newCourseApplicationButtonTooltipText = buildNewCourseApplicationButtonTooltipText();
		this.newCourseApplicationButtonText = buildNewCourseApplicationButtonText();
		this.newCourseParticipantButtonText = buildNewCourseParticipantButtonText();
		this.newCourseParticipant = new CourseParticipant();
		this.pageHeadline = getNewCourseApplicationTitle();
		
		this.courseSelectionRequired = configurationService.isCourseSelectionRequired();
		
		if (this.courseSelectionRequired) {
			// seznam mist konani
			this.courseLocationList = courseService.getCourseLocationAll();
			// seznam kurzu
			this.courseListAll = courseService.getAll(this.yearFromTo.getValue0(), this.yearFromTo.getValue1(), true);	
		}
	}
	
	@Command
    public void detailCmd(@BindingParam(WebConstants.UUID_PARAM) final UUID uuid) {
		if (uuid ==  null) {
			throw new IllegalArgumentException("uuid is null");
		}
		String targetPage = WebPages.USER_PARTICIPANT_DETAIL.getUrl();
		WebPages fromPage = WebPages.USER_PARTICIPANT_LIST;
		Executions.sendRedirect(targetPage + "?"+WebConstants.UUID_PARAM+"="+uuid.toString() + "&" + WebConstants.FROM_PAGE_PARAM + "=" + fromPage);
	}
	
	@NotifyChange("*")
	@Command
    public void createNewCourseApplicationCmd(@BindingParam(WebConstants.UUID_PARAM) final UUID uuid) {
		if (this.courseSelectionRequired) {
			
		} else {
			CourseParticipant courseParticipant = courseService.getCourseParticipantByUuid(uuid);
			createNewCourseApplication(courseParticipant);			
		}
	}
	
	@NotifyChange("*")
	@Command
	public void submit(@BindingParam("popup") Popup popup) {
		createNewCourseApplication(this.newCourseParticipant);
		this.newCourseParticipant = new CourseParticipant();
		if (this.courseSelectionRequired) {
			if (this.courseList != null) {
				this.courseList.clear();			
			}
			if (this.courseSelected != null) {
				this.courseSelected.clear();			
			}
			this.courseLocationSelected = null;
		}
		popup.close();
	}
	
	@NotifyChange("*")
	@Command
	public void logToCourseCmd(@BindingParam("courseParticipant") CourseParticipant courseParticipant, @BindingParam("popup") Popup popup) {
		Course courseSelected = this.courseSelected.iterator().next();
		if (courseSelected == null) {
			return;
		}
		// kontrola zda-li jit neni ucastnik zarazen do kurzu
		List<CourseParticipant> courseParticipantInCourseList = courseService.getByCourseParticListByCourseUuid(courseSelected.getUuid(), true);
		for (CourseParticipant item : courseParticipantInCourseList) {
			if (item.getUuid().toString().equals(courseParticipant.getUuid().toString())) {
				WebUtils.showNotificationWarning(Labels.getLabel("msg.ui.warn.participantAlreadyInCourse", new Object[] {courseSelected.getName()}));
				return;
			}
		}
		createNewCourseApplication(courseService.getCourseParticipantByUuid(courseParticipant.getUuid()));	
		WebUtils.setSessAtribute("notificationMessage", Labels.getLabel("msg.ui.warn.participantLoggedToCourse", new Object[] {courseParticipant.getContact().getCompleteName(), courseSelected.getName()}));
		detailCmd(courseParticipant.getUuid());
		
//		try {
//			Course courseSelected = this.courseSelected.iterator().next();
//			if (courseSelected == null) {
//				return;
//			}
//			// kontrola zda-li jit neni ucastnik zarazen do kurzu
//			List<CourseParticipant> courseParticipantInCourseList = courseService.getByCourseParticListByCourseUuid(courseSelected.getUuid(), true);
//			for (CourseParticipant item : courseParticipantInCourseList) {
//				if (item.getUuid().toString().equals(courseParticipant.getUuid().toString())) {
//					WebUtils.showNotificationWarning(Labels.getLabel("msg.ui.warn.participantAlreadyInCourse", new Object[] {courseSelected.getName()}));
//					return;
//				}
//			}
//			
//			// prihlaseni rovnou do kurzu
//			if (this.courseSelected != null && !this.courseSelected.isEmpty()) {
//				courseService.storeCourseParticipants(Arrays.asList(courseParticipant), courseSelected.getUuid());
//			}
//			popup.close();
//			// sendMail(courseApplication, this.pageHeadline);
//			WebUtils.setSessAtribute("notificationMessage", Labels.getLabel("msg.ui.warn.participantLoggedToCourse", new Object[] {courseParticipant.getContact().getCompleteName(), courseSelected.getName()}));
//			detailCmd(courseParticipant.getUuid());
//		} catch (ScbValidationException e) {
//			LOG.warn("ScbValidationException caught for courseParticipant: " + courseParticipant.getUuid(), e);
//			WebUtils.showNotificationError(e.getMessage());
//		}
	}
	
	/**
	 * Kontroluje pouziti emailu jako defaultniho prihlasovaciho jmena, pokud je jiz evidovano, nabidne predvyplneni hodnot zakonneho zastupce.
	 * @param personalNumber
	 * @param fx
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@NotifyChange("*")
	@Command
	public void validateUniquePersonalNumberCmd(@BindingParam("personal_number") String personalNumber, @BindingParam("fx") final CourseParticipant fx) {
		// pokud existuje ucastnik se stejnym rodnym cislem jako je zadane rodne cislo, zobrazit upozorneni a nepovolit vyplnit.
//		fx.setPersonalNo(personalNumber);
		this.newCourseParticipant.setPersonalNo(personalNumber);
		if (courseApplicationService.existsByPersonalNumber(personalNumber)) {
			String question = Labels.getLabel("msg.ui.quest.participantPersonalNoExists2",new Object[] {personalNumber});			
			Messagebox.show(question, Labels.getLabel("txt.ui.common.warning"), Messagebox.OK, Messagebox.EXCLAMATION, new org.zkoss.zk.ui.event.EventListener() {
			    public void onEvent(Event evt) throws InterruptedException {
			        // vymazat rodne cislo
			    	fx.setPersonalNo("");
			        BindUtils.postNotifyChange(null, null, fx, "*");
			    }
			});
		} else {
			BindUtils.postNotifyChange(null, null, this, "*");
		}
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
	
	public String getCourseRowColor(Course course) {
		if (course == null) {
			return null;
		}
		
		if (course.isFullOccupancy()) {
			return "background: #F0F0F0";
		}
		
		return "";
	}
	
	private void createNewCourseApplication(CourseParticipant courseParticipant) {
		CourseApplication courseApplication = new CourseApplication();
		courseApplication.setCourseParticipant(courseParticipant);
		courseApplication.setCourseParticRepresentative(SecurityUtils.getLoggedUser());
		courseApplication.setYearFrom(this.yearFromTo.getValue0());
		courseApplication.setYearTo(this.yearFromTo.getValue1());
		
		// pokud byl vybran kurz, potreba zkontrolovat zda-li uz neni zaplnen
		if (this.courseSelectionRequired && this.courseSelected != null && !this.courseSelected.isEmpty()) {
			Course selectedCourseDb = courseService.getByUuid(this.courseSelected.iterator().next().getUuid());
			if (selectedCourseDb == null) {
				WebUtils.showNotificationWarning(Labels.getLabel("msg.ui.warn.courseIsDeleted", new Object[] {this.courseSelected.iterator().next().getName()}));
				// reload seznamu kurzu
				this.courseList = courseService.getAll(courseApplication.getYearFrom(), courseApplication.getYearTo(), true);
				this.courseSelected.clear();
				return;
			}
			if (selectedCourseDb.isFullOccupancy()) {
				WebUtils.showNotificationWarning(Labels.getLabel("msg.ui.warn.courseIsFull", new Object[] {this.courseSelected.iterator().next().getName()}));
				// reload seznamu kurzu
				this.courseList = courseService.getAll(courseApplication.getYearFrom(), courseApplication.getYearTo(), true);
				this.courseSelected.clear();
				return;
			}
		}
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("Creating application: " + courseApplication);
		}
		
		try {
			courseApplicationService.store(courseApplication);
			WebUtils.showNotificationInfo(Labels.getLabel("msg.ui.info.applicationSend"));
			this.courseParticipantList = courseService.getCourseParticListByRepresentativeUuid(SecurityUtils.getLoggedUser().getUuid());
			
			// prihlaseni rovnou do kurzu
			if (this.courseSelected != null && !this.courseSelected.isEmpty()) {
				courseService.storeCourseParticipants(Arrays.asList(courseApplication.getCourseParticipant()), this.courseSelected.iterator().next().getUuid());
				courseApplication.getCourseParticipant().setCourseList(new ArrayList<>(this.courseSelected));
			}
						
			byte[] byteArray = JasperUtil.getReport(courseApplication, Labels.getLabel("txt.ui.menu.applicationWithYear", new Object[] {courseApplication.getYearFrom()}), configurationService);
			this.attachment = buildCourseApplicationAttachment(courseApplication, byteArray);
			
			sendMail(courseApplication, this.pageHeadline);
		} catch (ScbValidationException e) {
			LOG.warn("ScbValidationException caught for application: " + courseApplication);
			WebUtils.showNotificationError(e.getMessage());
		}
	}
	
	/**
	 * Vytvoreni nove prihlasky povoleno pokud
	 * - povoleno podavani prihlasek
	 * - ucastnik zatim nema v danem rocniku podanu prihlasku
	 * @return
	 */
	public boolean newCourseApplicationAllowed(CourseParticipant courseParticipant) {
		if (!this.courseSelectionRequired) {
			// kontrola zda-li neni uz prihlaseny jen pokud neni povolen vyber kurzu, pokud ano, muze se ucastnik prihlasit do vice kurzu
			List<CourseApplication> courseApplicationList = courseApplicationService.getByCourseParticipantUuid(courseParticipant.getUuid());
			for (CourseApplication item : courseApplicationList) {
				if (item.getYearFrom() == this.yearFromTo.getValue0()) {
					return false;
				}
			}			
		}
		return this.courseApplicationAllowed;
	}
	
	private String buildNewCourseApplicationButtonTooltipText() {
		return Labels.getLabel("txt.ui.menu.applicationWithYear", new Object[] {String.valueOf(this.yearFromTo.getValue0()) + "/" + String.valueOf(this.yearFromTo.getValue1())});
	}

	private String buildNewCourseApplicationButtonText() {
		if (configurationService.isCourseSelectionRequired()) {
			return Labels.getLabel("txt.ui.common.LogToCourse");
		} else {
			return Labels.getLabel("txt.ui.menu.application") + " - " + String.valueOf(this.yearFromTo.getValue0()) + "/" + String.valueOf(this.yearFromTo.getValue1());			
		}
	}
	
	private String buildNewCourseParticipantButtonText() {
		return Labels.getLabel("txt.ui.common.newCourseParticipant") + " - " + String.valueOf(this.yearFromTo.getValue0()) + "/" + String.valueOf(this.yearFromTo.getValue1());
	}
	
	public List<CourseParticipant> getCourseParticipantList() {
		return courseParticipantList;
	}
	
	public String getNewCourseApplicationButtonTooltipText() {
		return newCourseApplicationButtonTooltipText;
	}
	
	public String getNewCourseApplicationButtonText() {
		return newCourseApplicationButtonText;
	}
	
	public String getNewCourseParticipantButtonText() {
		return newCourseParticipantButtonText;
	}
	
	public CourseParticipant getNewCourseParticipant() {
		return newCourseParticipant;
	}

	public void setNewCourseParticipant(CourseParticipant newCourseParticipant) {
		this.newCourseParticipant = newCourseParticipant;
	}
	
	public Set<Course> getCourseSelected() {
		return courseSelected;
	}
	public void setCourseSelected(Set<Course> courseSelected) {
		this.courseSelected = courseSelected;
	}
	public List<Course> getCourseList() {
		return courseList;
	}
	public boolean isCourseSelectionRequired() {
		return courseSelectionRequired;
	}
	public CourseLocation getCourseLocationSelected() {
		return courseLocationSelected;
	}
	public void setCourseLocationSelected(CourseLocation courseLocationSelected) {
		this.courseLocationSelected = courseLocationSelected;
	}
	public List<CourseLocation> getCourseLocationList() {
		return courseLocationList;
	}
}
