package com.jzaoralek.scb.ui.pages.courseparticipant.vm;

import java.util.List;
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

import com.jzaoralek.scb.dataservice.domain.CourseApplication;
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
		CourseParticipant courseParticipant = courseService.getCourseParticipantByUuid(uuid);
		createNewCourseApplication(courseParticipant);
	}
	
	@NotifyChange("*")
	@Command
	public void submit(@BindingParam("popup") Popup popup) {
		createNewCourseApplication(this.newCourseParticipant);
		this.newCourseParticipant = new CourseParticipant();
		popup.close();
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
	
	
	private void createNewCourseApplication(CourseParticipant courseParticipant) {
		CourseApplication courseApplication = new CourseApplication();
		courseApplication.setCourseParticipant(courseParticipant);
		courseApplication.setCourseParticRepresentative(SecurityUtils.getLoggedUser());
		courseApplication.setYearFrom(this.yearFromTo.getValue0());
		courseApplication.setYearTo(this.yearFromTo.getValue1());
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("Creating application: " + courseApplication);
		}
		
		try {
			courseApplicationService.store(courseApplication);
			WebUtils.showNotificationInfo(Labels.getLabel("msg.ui.info.applicationSend"));
			this.courseParticipantList = courseService.getCourseParticListByRepresentativeUuid(SecurityUtils.getLoggedUser().getUuid());
			byte[] byteArray = JasperUtil.getReport(courseApplication, Labels.getLabel("txt.ui.menu.applicationWithYear", new Object[] {courseApplication.getYearFrom()}));
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
		List<CourseApplication> courseApplicationList = courseApplicationService.getByCourseParticipantUuid(courseParticipant.getUuid());
		for (CourseApplication item : courseApplicationList) {
			if (item.getYearFrom() == this.yearFromTo.getValue0()) {
				return false;
			}
		}
		return this.courseApplicationAllowed;
	}
	
	private String buildNewCourseApplicationButtonTooltipText() {
		return Labels.getLabel("txt.ui.menu.applicationWithYear", new Object[] {String.valueOf(this.yearFromTo.getValue0()) + "/" + String.valueOf(this.yearFromTo.getValue1())});
	}

	private String buildNewCourseApplicationButtonText() {
		return Labels.getLabel("txt.ui.menu.application") + " - " + String.valueOf(this.yearFromTo.getValue0()) + "/" + String.valueOf(this.yearFromTo.getValue1());
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
}
