package com.jzaoralek.scb.ui.pages.courseapplication.vm;

import java.util.UUID;

import org.springframework.util.StringUtils;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.bind.annotation.QueryParam;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import com.jzaoralek.scb.dataservice.domain.CourseApplication;
import com.jzaoralek.scb.dataservice.exception.ScbValidationException;
import com.jzaoralek.scb.dataservice.service.ConfigurationService;
import com.jzaoralek.scb.dataservice.service.CourseApplicationService;
import com.jzaoralek.scb.dataservice.service.MailService;
import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.WebPages;
import com.jzaoralek.scb.ui.common.utils.JasperUtil;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.BaseVM;

public class CourseApplicationVM extends BaseVM {

	private CourseApplication application;
	private boolean healthInfoAgreement;
	private boolean personalInfoProcessAgreement;
	private boolean editMode;
	private boolean securedMode;
	private boolean showNotification;
	private String confirmText;
	private String returnToPage;
	private String pageHeadline;

	@WireVariable
	private CourseApplicationService courseApplicationService;

	@WireVariable
	private ConfigurationService configurationService;

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

		this.returnToPage = StringUtils.hasText(fromPage) ? WebPages.valueOf(fromPage).getUrl() : null;

		String year = "";
		if (courseApplication == null) {
			year = configurationService.getCourseApplicationYear();
		} else {
			year = String.valueOf(courseApplication.getYearFrom());
		}
		this.pageHeadline = Labels.getLabel("txt.ui.menu.applicationWithYear", new Object[] {year});
	}

	private Boolean isSecuredPage() {
		// TODO: nahradit nactenim ze security context
		return WebUtils.getCurrentUrl().contains("/pages/secured");
	}

	@NotifyChange("*")
	@Command
    public void submit() {
		try {
			if (this.securedMode) {
				// update
				courseApplicationService.store(application);
				WebUtils.showNotificationInfo(Labels.getLabel("msg.ui.info.changesSaved"));
				this.editMode = true;
				this.confirmText = Labels.getLabel("msg.ui.info.changesSaved");
			} else {
				// create
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
			WebUtils.showNotificationError(e.getMessage());
		}
    }

	@Command
    public void backCmd() {
		if (StringUtils.hasText(this.returnToPage)) {
			Executions.sendRedirect(this.returnToPage);
		}
	}

    public void sendMail() {
		StringBuilder sb = new StringBuilder();
		sb.append(Labels.getLabel("msg.ui.mail.courseApplication.text0"));
		sb.append(System.getProperty("line.separator"));
		sb.append(System.getProperty("line.separator"));
		sb.append(Labels.getLabel("msg.ui.mail.courseApplication.text1"));
		sb.append(System.getProperty("line.separator"));
		sb.append(System.getProperty("line.separator"));
		sb.append(Labels.getLabel("msg.ui.mail.courseApplication.text2"));

//		this.application.getCourseParticipant().getContact().setFirstname("Jméno");
//		this.application.getCourseParticipant().getContact().setSurname("Příjmení");
//		this.application.getCourseParticipant().setBirthdate(Calendar.getInstance().getTime());
//		this.application.getCourseParticipant().setPersonalNo("111111/1111");
//		this.application.getCourseParticipant().setHealthInsurance("Všeobecná zdravotní pojišťovna ěěěě šššššš ččččč řřřřř žžžžž ýýýýýý ááááá ííííí ééé úúúú ůůů");
//		this.application.getCourseParticRepresentative().getContact().setFirstname("Zástupce jméno");
//		this.application.getCourseParticRepresentative().getContact().setSurname("Zástupce příjmení");
//		this.application.getCourseParticRepresentative().getContact().setPhone1("111111111");
//		this.application.getCourseParticRepresentative().getContact().setPhone2(null);
//		this.application.getCourseParticRepresentative().getContact().setEmail1("a.a@email.cz");
//		this.application.getCourseParticRepresentative().getContact().setEmail2(null);;
//		this.application.getCourseParticipant().setHealthInfo("Údaje o zdravotních obtížích");

		byte[] byteArray = JasperUtil.getReport(this.application, this.pageHeadline);
		//Filedownload.save(byteArray, JasperUtil.REPORT_MIME, "prihlaska.pdf");

		StringBuilder fileName = new StringBuilder();
		fileName.append("prihlaska_do_klubu");
		//fileName.append(this.application.getCourseParticipant().getContact().getFirstname() + "_" + this.application.getCourseParticipant().getContact().getSurname());
		fileName.append(".pdf");

		mailService.sendMail(this.application.getCourseParticRepresentative().getContact().getEmail1(), Labels.getLabel("txt.ui.menu.application"), sb.toString(), byteArray, fileName.toString().toLowerCase());
	}

	public Boolean isBackButtonVisible() {
		return StringUtils.hasText(this.returnToPage);
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
}