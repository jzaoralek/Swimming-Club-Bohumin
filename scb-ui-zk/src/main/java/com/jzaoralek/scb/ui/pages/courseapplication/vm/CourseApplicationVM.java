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
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Messagebox;

import com.jzaoralek.scb.dataservice.domain.CourseApplication;
import com.jzaoralek.scb.dataservice.domain.ScbUser;
import com.jzaoralek.scb.dataservice.exception.ScbValidationException;
import com.jzaoralek.scb.dataservice.service.CourseApplicationService;
import com.jzaoralek.scb.dataservice.service.MailService;
import com.jzaoralek.scb.dataservice.service.ScbUserService;
import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEvent;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEventQueues;
import com.jzaoralek.scb.ui.common.utils.JasperUtil;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.Attachment;
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
	private Attachment attachment;

	@WireVariable
	private CourseApplicationService courseApplicationService;

	@WireVariable
	private ScbUserService scbUserService;
	
	@WireVariable
	private MailService mailService;

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
				courseApplicationService.store(application);
				//WebUtils.showNotificationInfo(Labels.getLabel("msg.ui.info.applicationSend"));
				this.editMode = false;
				this.confirmText = Labels.getLabel("msg.ui.info.applicationSend");
				this.showNotification = true;

				sendMail();
			}
		} catch (ScbValidationException e) {
			LOG.warn("ScbValidationException caught for application: " + this.application);
			WebUtils.showNotificationError(e.getMessage());
		} catch (Exception e) {
			LOG.error("Unexpected exception caught for application: " + this.application, e);
			throw new RuntimeException(e);
		}
    }

	@Command
	public void downloadCmd() {
		WebUtils.downloadAttachment(attachment);
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
		// pokud existuje uzivatel se stejnym username jako je zadany email, zobrazit upozorneni, pokud uzivatel potvrdi, 
		// predvyplnit udaje a po ulozeni provest jen update udaju zastupce
		final ScbUser scbUser = scbUserService.getByUsername(email);
		if (scbUser != null) {
			String question = Labels.getLabel("msg.ui.quest.participantRepresentativeExists",new Object[] {email, scbUser.getContact().getCompleteName()});
			Messagebox.show(question, Labels.getLabel("txt.ui.common.warning"), Messagebox.YES | Messagebox.NO, Messagebox.EXCLAMATION, new org.zkoss.zk.ui.event.EventListener() {
			    public void onEvent(Event evt) throws InterruptedException {
			        if (evt.getName().equals("onYes")) {
			            // predvyplnit udaje zastupce, automaticky se diky vypplnenemu uuid bude provadet update
			        	fx.getApplication().setCourseParticRepresentative(scbUser);
			        } else {
			        	// vymazat email
			        	fx.getApplication().getCourseParticRepresentative().getContact().setEmail1("");
			        }
			        BindUtils.postNotifyChange(null, null, fx, "*");
			    }
			});
		}
	}

    public void sendMail() {
		StringBuilder mailToRepresentativeSb = new StringBuilder();
		mailToRepresentativeSb.append(Labels.getLabel("msg.ui.mail.courseApplication.text0"));
		mailToRepresentativeSb.append(System.getProperty("line.separator"));
		mailToRepresentativeSb.append(System.getProperty("line.separator"));
		mailToRepresentativeSb.append(Labels.getLabel("msg.ui.mail.courseApplication.text1"));
		mailToRepresentativeSb.append(System.getProperty("line.separator"));
		mailToRepresentativeSb.append(System.getProperty("line.separator"));
		mailToRepresentativeSb.append(Labels.getLabel("msg.ui.mail.courseApplication.text2"));

		byte[] byteArray = JasperUtil.getReport(this.application, this.pageHeadline);

		StringBuilder fileName = new StringBuilder();
		fileName.append("prihlaska_do_klubu");
		fileName.append("_" + this.application.getCourseParticRepresentative().getContact().getEmail1());
		fileName.append(".pdf");

		// create attachment for FileDownloadServlet
		Attachment attachment = new Attachment();
		attachment.setByteArray(byteArray);
		attachment.setContentType("application/pdf");
		attachment.setName(fileName.toString());
		this.attachment = attachment;

		// mail to course participant representative
		mailService.sendMail(this.application.getCourseParticRepresentative().getContact().getEmail1(), Labels.getLabel("txt.ui.menu.application"), mailToRepresentativeSb.toString(), byteArray, fileName.toString().toLowerCase());

		StringBuilder mailToClupSb = new StringBuilder();
		String courseApplicationYear = configurationService.getCourseApplicationYear();
		mailToClupSb.append(Labels.getLabel("msg.ui.mail.text.newApplication.text0", new Object[] {courseApplicationYear}));
		mailToClupSb.append(System.getProperty("line.separator"));
		String participantInfo = this.application.getCourseParticipant().getContact().getFirstname() + " " + this.application.getCourseParticipant().getContact().getSurname() + ", " + getDateConverter().coerceToUi(this.application.getCourseParticipant().getBirthdate(), null, null);
		mailToClupSb.append(Labels.getLabel("msg.ui.mail.text.newApplication.text1", new Object[] {participantInfo}));
		mailToClupSb.append(System.getProperty("line.separator"));
		String representativeInfo = this.application.getCourseParticRepresentative().getContact().getFirstname() + " " + this.application.getCourseParticRepresentative().getContact().getSurname() + ", " + this.application.getCourseParticRepresentative().getContact().getEmail1() + ", " + this.application.getCourseParticRepresentative().getContact().getPhone1();
		mailToClupSb.append(Labels.getLabel("msg.ui.mail.text.newApplication.text2", new Object[] {representativeInfo}));

		// mail to club
		mailService.sendMail(Labels.getLabel("txt.ui.organization.email"), Labels.getLabel("msg.ui.mail.subject.newApplication", new Object[] {courseApplicationYear}), mailToClupSb.toString(), null, null);
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