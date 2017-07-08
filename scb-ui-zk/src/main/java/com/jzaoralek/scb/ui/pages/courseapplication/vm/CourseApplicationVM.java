package com.jzaoralek.scb.ui.pages.courseapplication.vm;

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

import com.jzaoralek.scb.dataservice.domain.CourseApplication;
import com.jzaoralek.scb.dataservice.domain.CourseParticipant;
import com.jzaoralek.scb.dataservice.domain.ScbUser;
import com.jzaoralek.scb.dataservice.domain.ScbUserRole;
import com.jzaoralek.scb.dataservice.exception.ScbValidationException;
import com.jzaoralek.scb.dataservice.service.CourseApplicationService;
import com.jzaoralek.scb.dataservice.service.ScbUserService;
import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.BaseVM;

public class CourseApplicationVM extends BaseVM {

	private static final Logger LOG = LoggerFactory.getLogger(CourseApplicationVM.class);

	private CourseApplication application;
	private boolean healthInfoAgreement;
	private boolean personalInfoProcessAgreement;
	private boolean editMode;
	private boolean securedMode;
	private boolean showNotification;
	private String confirmText;
	private String errotText;
	private String pageHeadline;
	private String captcha;

	@WireVariable
	private CourseApplicationService courseApplicationService;

	@WireVariable
	private ScbUserService scbUserService;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Init
	public void init(@QueryParam(WebConstants.UUID_PARAM) String uuid, @QueryParam(WebConstants.FROM_PAGE_PARAM) String fromPage) {
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

		if (courseApplication == null) {
			this.pageHeadline = getNewCourseApplicationTitle();
		} else {
			this.pageHeadline = Labels.getLabel("txt.ui.menu.applicationWithYear", new Object[] {String.valueOf(courseApplication.getYearFrom())});
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
				if (!this.healthInfoAgreement || !this.personalInfoProcessAgreement) {
					WebUtils.showNotificationWarning(Labels.getLabel("msg.ui.warn.agreementWithHealtAndDataInfo"));
					return;
				}
				
				// zjistit zda-li pred zalozenim objednavky uz uzivatel v aplikaci existoval
				ScbUser scbUserBeforeApplicationSave = scbUserService.getByUsername(application.getCourseParticRepresentative().getContact().getEmail1());
				
				courseApplicationService.store(application);
				//WebUtils.showNotificationInfo(Labels.getLabel("msg.ui.info.applicationSend"));
				this.editMode = false;
				this.confirmText = Labels.getLabel("msg.ui.info.applicationSend");
				this.showNotification = true;

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
}