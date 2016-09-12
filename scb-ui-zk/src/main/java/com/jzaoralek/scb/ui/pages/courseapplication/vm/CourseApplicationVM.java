package com.jzaoralek.scb.ui.pages.courseapplication.vm;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.bind.annotation.QueryParam;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import com.jzaoralek.scb.dataservice.domain.CourseApplication;
import com.jzaoralek.scb.dataservice.exception.ScbValidationException;
import com.jzaoralek.scb.dataservice.service.CourseApplicationService;
import com.jzaoralek.scb.dataservice.service.MailService;
import com.jzaoralek.scb.ui.common.WebConstants;
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

	private String pageHeadline;
	private String captcha;
	private Attachment attachment;

	@WireVariable
	private CourseApplicationService courseApplicationService;

	@WireVariable
	private MailService mailService;

	@Init
	public void init(@QueryParam(WebConstants.UUID_PARAM) String uuid, @QueryParam(WebConstants.FROM_PAGE_PARAM) String fromPage) {

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

	private Boolean isSecuredPage() {
		return WebUtils.getCurrentUrl().contains(WebConstants.SECURED_PAGE_URL);
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
//		Executions.sendRedirect(FileDownloadServlet.URL);
//		Filedownload.save(this.applicationFile, JasperUtil.REPORT_MIME, "prihlaska.pdf");
		WebUtils.downloadAttachment(attachment);
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
	public String getPageHeadline() {
		return pageHeadline;
	}
	public boolean isShowNotification() {
		return showNotification;
	}
	public String getCaptcha() {
		return captcha;
	}
	public void setCaptcha(String captcha) {
		this.captcha = captcha;
	}
}