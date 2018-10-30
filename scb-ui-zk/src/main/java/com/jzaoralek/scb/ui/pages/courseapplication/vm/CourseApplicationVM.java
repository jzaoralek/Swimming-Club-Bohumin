package com.jzaoralek.scb.ui.pages.courseapplication.vm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.bind.annotation.QueryParam;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Messagebox;

import com.jzaoralek.scb.dataservice.domain.Attachment;
import com.jzaoralek.scb.dataservice.domain.Course;
import com.jzaoralek.scb.dataservice.domain.CourseApplication;
import com.jzaoralek.scb.dataservice.domain.CourseApplicationFileConfig;
import com.jzaoralek.scb.dataservice.domain.CourseApplicationFileConfig.CourseApplicationFileType;
import com.jzaoralek.scb.dataservice.domain.CourseLocation;
import com.jzaoralek.scb.dataservice.domain.ScbUser;
import com.jzaoralek.scb.dataservice.domain.ScbUserRole;
import com.jzaoralek.scb.dataservice.exception.ScbValidationException;
import com.jzaoralek.scb.dataservice.service.CourseApplicationService;
import com.jzaoralek.scb.dataservice.service.CourseService;
import com.jzaoralek.scb.dataservice.service.ScbUserService;
import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.BaseVM;

public class CourseApplicationVM extends BaseVM {

	private static final Logger LOG = LoggerFactory.getLogger(CourseApplicationVM.class);

	private CourseApplication application;
	private boolean healthInfoAgreement;
	private boolean personalInfoProcessAgreement;
	private boolean clubRulesAgreement;
	private boolean editMode;
	private boolean securedMode;
	private boolean showNotification;
	private String confirmText;
	private String errotText;
	private String captcha;
	private List<Course> courseList;
	private List<Course> courseListAll;
	private Set<Course> courseSelected;
	private boolean courseSelectionRequired;
	private List<CourseLocation> courseLocationList;
	private CourseLocation courseLocationSelected;
	private CourseApplicationFileConfig clubRulesAgreementConfig;
	private CourseApplicationFileConfig healthInfoAgreementConfig;
	private CourseApplicationFileConfig gdprAgreementConfig;
	
	@WireVariable
	private CourseApplicationService courseApplicationService;

	@WireVariable
	private CourseService courseService;
	
	@WireVariable
	private ScbUserService scbUserService;

	@Init
	public void init(@QueryParam(WebConstants.UUID_PARAM) String uuid, @QueryParam(WebConstants.FROM_PAGE_PARAM) String fromPage) {
		super.init();
		// kontrola zda-li prihlasky povolene
		if (!isSecuredPage() && !isCourseApplicationAllowed()) {
			this.editMode = false;
			this.showNotification = true;
			this.errotText = Labels.getLabel("msg.ui.warn.courseApplicationsNotAllowed");
			return;
		}

		CourseApplication courseApplication = null;
		if (StringUtils.hasText(uuid)) {
			courseApplication = courseApplicationService.getByUuid(UUID.fromString(uuid));
		}
		initItem(courseApplication);
		this.editMode = true;
		this.securedMode = isSecuredPage();
		this.showNotification = false;

		setReturnPage(fromPage);
		
		if (!isSecuredPage()) {
			this.application.fillYearFromTo(configurationService.getCourseApplicationYear());			
		}
		
		this.courseSelectionRequired = configurationService.isCourseSelectionRequired();
		
		if (this.courseSelectionRequired) {
			if (!this.securedMode) {
				// seznam mist konani
				this.courseLocationList = courseService.getCourseLocationAll();
				// seznam vsech kurzu
				this.courseListAll = courseService.getAll(this.application.getYearFrom(), this.application.getYearTo(), true);
			} else {
				// seznam vybranych kurzu
				this.courseList = courseService.getByCourseParticipantUuid(this.application.getCourseParticipant().getUuid(), this.application.getYearFrom(), this.application.getYearTo());
			}			
		}
		
		if (!this.securedMode) {
			// konfigurace souhlasu týkajici se zdravotniho stavu, zpracování informací, pravidel klubu
			initAgreementFileConfig();			
		}

		if (courseApplication == null) {
			this.pageHeadline = getNewCourseApplicationTitle();
		} else {
			this.pageHeadline = Labels.getLabel("txt.ui.menu.applicationWithYear", new Object[] {String.valueOf(courseApplication.getYearFrom())});
		}
	}
	
	/**
	 * Init agreemnts of gdpr, health info and club rules and connected files to download.
	 */
	private void initAgreementFileConfig() {
		// konfigurace souhlasu týkajici se zdravotniho stavu, zpracování informací, pravidel klubu
		List<CourseApplicationFileConfig> cafcPageList = courseApplicationFileConfigService.getListForPage();
		LOG.info("initAgreementFileConfig():: cafcPageList: " + cafcPageList);
		if (cafcPageList != null) {
			this.clubRulesAgreementConfig = getByType(cafcPageList, CourseApplicationFileType.CLUB_RULES);
			LOG.info("initAgreementFileConfig():: clubRulesAgreementConfig: " + clubRulesAgreementConfig);
			this.healthInfoAgreementConfig = getByType(cafcPageList, CourseApplicationFileType.HEALTH_INFO);
			LOG.info("initAgreementFileConfig():: healthInfoAgreementConfig: " + healthInfoAgreementConfig);
			this.gdprAgreementConfig = getByType(cafcPageList, CourseApplicationFileType.GDPR);
			LOG.info("initAgreementFileConfig():: gdprAgreementConfig: " + gdprAgreementConfig);
		}
	}
	
	@NotifyChange("*")
	@Command
    public void submit() {
		try {
			if (this.securedMode) {
				// update
				if (LOG.isDebugEnabled()) {
					LOG.debug("Updating application: " + this.application);
				}
				courseApplicationService.store(application);
				WebUtils.showNotificationInfo(Labels.getLabel("msg.ui.info.changesSaved"));
				this.editMode = true;
				this.confirmText = Labels.getLabel("msg.ui.info.changesSaved");
			} else {
				// create
				if (LOG.isDebugEnabled()) {
					LOG.debug("Creating application: " + this.application);
				}
				if ((isHealthInfoConfirmRequired() && !this.healthInfoAgreement) 
						|| (isGdprConfirmRequired() && !this.personalInfoProcessAgreement)
						|| (isClubRulesConfirmRequired() && !this.clubRulesAgreement)) {
					WebUtils.showNotificationWarning(Labels.getLabel("msg.ui.warn.agreementWithHealtAndDataInfo"));
					return;
				}
				
				// pokud byl vybran kurz, potreba zkontrolovat zda-li uz neni zaplnen
				if (this.courseSelectionRequired && this.courseSelected != null && !this.courseSelected.isEmpty()) {
					Course selectedCourseDb = courseService.getByUuid(this.courseSelected.iterator().next().getUuid());
					if (selectedCourseDb == null) {
						WebUtils.showNotificationWarning(Labels.getLabel("msg.ui.warn.courseIsDeleted", new Object[] {this.courseSelected.iterator().next().getName()}));
						// reload seznamu kurzu
						this.courseList = courseService.getAll(this.application.getYearFrom(), this.application.getYearTo(), true);
						this.courseSelected.clear();
						return;
					}
					if (selectedCourseDb.isFullOccupancy()) {
						WebUtils.showNotificationWarning(Labels.getLabel("msg.ui.warn.courseIsFull", new Object[] {this.courseSelected.iterator().next().getName()}));
						// reload seznamu kurzu
						this.courseList = courseService.getAll(this.application.getYearFrom(), this.application.getYearTo(), true);
						this.courseSelected.clear();
						return;
					}
				}
				
				// zjistit zda-li pred zalozenim objednavky uz uzivatel v aplikaci existoval
				ScbUser scbUserBeforeApplicationSave = scbUserService.getByUsername(application.getCourseParticRepresentative().getContact().getEmail1());
				
				CourseApplication courseApplication = courseApplicationService.store(application);
				this.editMode = false;
				this.confirmText = Labels.getLabel("msg.ui.info.applicationSend");
				this.showNotification = true;
				
				// prihlaseni rovnou do kurzu
				if (this.courseSelected != null && !this.courseSelected.isEmpty()) {
					courseService.storeCourseParticipants(Arrays.asList(courseApplication.getCourseParticipant()), this.courseSelected.iterator().next().getUuid());
					this.application.getCourseParticipant().setCourseList(new ArrayList<>(this.courseSelected));
				}

				sendMail(this.application, this.pageHeadline);
				
				// pokud se jedna o noveho uzivatele poslat mail o pristupu do aplikace
				if (scbUserBeforeApplicationSave == null) {
					ScbUser user = scbUserService.getByUsername(application.getCourseParticRepresentative().getContact().getEmail1());
					sendMailToNewUser(user);
				}
			}
		} catch (ScbValidationException e) {
			LOG.warn("ScbValidationException caught for application: " + this.application);
			WebUtils.showNotificationError(e.getMessage());
		} catch (Exception e) {
			LOG.error("Unexpected exception caught for application: " + this.application, e);
			throw new RuntimeException(e);
		}
    }

	/**
	 * Kontroluje pouziti emailu jako defaultniho prihlasovaciho jmena, pokud je jiz evidovano, nabidne predvyplneni hodnot zakonneho zastupce.
	 * @param email
	 * @param fx
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@NotifyChange("*")
	@Command
	public void validateUniqueUsernameCmd(@BindingParam("email") String email, @BindingParam("fx") final CourseApplicationVM fx) {
		// pokud existuje uzivatel se stejnym username jako je zadany email, zobrazit upozorneni a nepovolit vyplnit
		final ScbUser scbUser = scbUserService.getByUsername(email);
		if (scbUser != null) {
			String question = Labels.getLabel("msg.ui.quest.participantRepresentativeExists",new Object[] {email, scbUser.getContact().getCompleteName()});			
			Messagebox.show(question, Labels.getLabel("txt.ui.common.warning"), Messagebox.OK, Messagebox.EXCLAMATION, new org.zkoss.zk.ui.event.EventListener() {
			    public void onEvent(Event evt) throws InterruptedException {
			        // vymazat email
			        fx.getApplication().getCourseParticRepresentative().getContact().setEmail1("");
			        BindUtils.postNotifyChange(null, null, fx, "*");
			    }
			});
		}
	}
	
	/**
	 * Kontroluje pouziti emailu jako defaultniho prihlasovaciho jmena, pokud je jiz evidovano, nabidne predvyplneni hodnot zakonneho zastupce.
	 * @param personalNumber
	 * @param fx
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@NotifyChange("*")
	@Command
	public void validateUniquePersonalNumberCmd(@BindingParam("personal_number") String personalNumber, @BindingParam("fx") final CourseApplicationVM fx) {
		// pokud existuje ucastnik se stejnym rodnym cislem jako je zadane rodne cislo, zobrazit upozorneni a nepovolit vyplnit.
		if (courseApplicationService.existsByPersonalNumber(personalNumber)) {
			String question = Labels.getLabel("msg.ui.quest.participantPersonalNoExists",new Object[] {personalNumber});			
			Messagebox.show(question, Labels.getLabel("txt.ui.common.warning"), Messagebox.OK, Messagebox.EXCLAMATION, new org.zkoss.zk.ui.event.EventListener() {
			    public void onEvent(Event evt) throws InterruptedException {
			        // vymazat rodne cislo
			        fx.getApplication().getCourseParticipant().setPersonalNo("");
			        BindUtils.postNotifyChange(null, null, fx, "*");
			    }
			});
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
	
	@Command
	public void downloadHealthInfoCmd() {
		Attachment attachment = courseApplicationFileConfigService.getFileByUuid(this.healthInfoAgreementConfig.getAttachmentUuid());
		if (attachment != null && attachment.getByteArray() != null) {
			Executions.getCurrent().getSession().setAttribute(WebConstants.ATTACHMENT_PARAM, attachment);
			WebUtils.downloadAttachment(attachment);			
		}
	}
	
	@Command
	public void downloadGdprCmd() {
		Attachment attachment = courseApplicationFileConfigService.getFileByUuid(this.getGdprAgreementConfig().getAttachmentUuid());
		if (attachment != null && attachment.getByteArray() != null) {
			Executions.getCurrent().getSession().setAttribute(WebConstants.ATTACHMENT_PARAM, attachment);
			WebUtils.downloadAttachment(attachment);			
		}
	}
	
	@Command
	public void downloadClubRulesCmd() {
		Attachment attachment = courseApplicationFileConfigService.getFileByUuid(this.clubRulesAgreementConfig.getAttachmentUuid());
		if (attachment != null && attachment.getByteArray() != null) {
			Executions.getCurrent().getSession().setAttribute(WebConstants.ATTACHMENT_PARAM, attachment);
			WebUtils.downloadAttachment(attachment);			
		}
	}
	
	public String getHealthAgreement() {
		return configurationService.getHealthAgreement();
	}
	
	public String getPersDataProcessAgreement() {
		return configurationService.getPersDataProcessAgreement();
	}
	
	/**
	 * Udaj muze menit pouze prihlaseny user nebo neprihlaseny uzivatel.
	 * @return
	 */
	public boolean isItemReadOnly() {
		return isLoggedUserInRole(ScbUserRole.TRAINER.name()) || isLoggedUserInRole(ScbUserRole.ADMIN.name());
	}

	private void initItem(CourseApplication courseApplication) {
		this.application = courseApplication != null ? courseApplication : new CourseApplication();
		this.healthInfoAgreement = false;
		this.personalInfoProcessAgreement = false;
		this.clubRulesAgreement = false;
	}
	
	public String getClubRulesText() {
		return configurationService.getClubRulesAgreement();
	}
	
	/**
	 * Povinnost odsouhlaseni pravidel klubu.
	 * @return
	 */
	public boolean isClubRulesConfirmRequired() {
		return this.clubRulesAgreementConfig != null && this.clubRulesAgreementConfig.isPageText();
	}
	
	/**
	 * Povinnost odsouhlaseni gdpr.
	 * @return
	 */
	public boolean isGdprConfirmRequired() {
		return this.gdprAgreementConfig != null && this.gdprAgreementConfig.isPageText();
	}

	/**
	 * Povinnost odsouhlaseni zdravotniho stavu.
	 * @return
	 */
	public boolean isHealthInfoConfirmRequired() {
		return this.healthInfoAgreementConfig != null && this.healthInfoAgreementConfig.isPageText();
	}
	
	/**
	 * Dostupnost dokumentu pravidla klubu ke stazeni.
	 * @return
	 */
	public boolean isClubRulesDocEnabled() {
		return this.clubRulesAgreementConfig != null 
				&& this.clubRulesAgreementConfig.isPageAttachment()
				&& this.clubRulesAgreementConfig.getAttachmentUuid() != null;
	}
	
	/**
	 * Dostupnost dokumentu gdpr ke stazeni.
	 * @return
	 */
	public boolean isGdprDocEnabled() {
		return this.gdprAgreementConfig != null 
				&& this.gdprAgreementConfig.isPageAttachment()
				&& this.gdprAgreementConfig.getAttachmentUuid() != null;
	}

	/**
	 * Dostupnost dokumentu odsouhlaseni zdravotniho stavu ke stazeni.
	 * @return
	 */
	public boolean isHealthInfoDocEnabled() {
		return this.healthInfoAgreementConfig != null 
				&& this.healthInfoAgreementConfig.isPageAttachment()
				&& this.healthInfoAgreementConfig.getAttachmentUuid() != null;
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

	public CourseApplication getApplication() {
		return application;
	}
	public void setApplication(CourseApplication application) {
		this.application = application;
	}
	public boolean isHealthInfoAgreement() {
		return healthInfoAgreement;
	}
	public void setHealthInfoAgreement(boolean healthInfoAgreement) {
		this.healthInfoAgreement = healthInfoAgreement;
	}
	public boolean isPersonalInfoProcessAgreement() {
		return personalInfoProcessAgreement;
	}
	public void setPersonalInfoProcessAgreement(boolean personalInfoProcessAgreement) {
		this.personalInfoProcessAgreement = personalInfoProcessAgreement;
	}
	public boolean isClubRulesAgreement() {
		return clubRulesAgreement;
	}
	public void setClubRulesAgreement(boolean clubRulesAgreement) {
		this.clubRulesAgreement = clubRulesAgreement;
	}
	public boolean isEditMode() {
		return editMode;
	}
	public boolean isSecuredMode() {
		return securedMode;
	}
	public String getConfirmText() {
		return confirmText;
	}
	public String getErrotText() {
		return errotText;
	}
	public String getPageHeadline() {
		return pageHeadline;
	}
	public boolean isShowNotification() {
		return showNotification;
	}
	public String getBorderLayoutCenterStyle() {
		return !this.securedMode ? "background:#dfe8f6 url('"+Executions.getCurrent().getContextPath()+"/resources/img/background2014Full.jpg') no-repeat center center;" : "";
	}
	public String getCaptcha() {
		return captcha;
	}
	public void setCaptcha(String captcha) {
		this.captcha = captcha;
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
	public List<CourseLocation> getCourseLocationList() {
		return courseLocationList;
	}
	public CourseLocation getCourseLocationSelected() {
		return courseLocationSelected;
	}
	public void setCourseLocationSelected(CourseLocation courseLocationSelected) {
		this.courseLocationSelected = courseLocationSelected;
	}
	public CourseApplicationFileConfig getClubRulesAgreementConfig() {
		return clubRulesAgreementConfig;
	}
	public void setClubRulesAgreementConfig(CourseApplicationFileConfig clubRulesAgreementConfig) {
		this.clubRulesAgreementConfig = clubRulesAgreementConfig;
	}
	public CourseApplicationFileConfig getHealthInfoAgreementConfig() {
		return healthInfoAgreementConfig;
	}
	public void setHealthInfoAgreementConfig(CourseApplicationFileConfig healthInfoAgreementConfig) {
		this.healthInfoAgreementConfig = healthInfoAgreementConfig;
	}
	public CourseApplicationFileConfig getGdprAgreementConfig() {
		return gdprAgreementConfig;
	}
	public void setGdprAgreementConfig(CourseApplicationFileConfig gdprAgreementConfig) {
		this.gdprAgreementConfig = gdprAgreementConfig;
	}
	
}