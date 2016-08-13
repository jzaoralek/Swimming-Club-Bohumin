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
import com.jzaoralek.scb.dataservice.service.CourseApplicationService;
import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.WebPages;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.BaseVM;

public class CourseApplicationVM extends BaseVM {

	private CourseApplication application;
	private boolean healthInfoAgreement;
	private boolean personalInfoProcessAgreement;
	private boolean editMode;
	private boolean securedMode;
	private String confirmText;
	private String returnToPage;

	@WireVariable
	private CourseApplicationService courseApplicationService;

	@Init
	public void init(@QueryParam(WebConstants.UUID_PARAM) String uuid, @QueryParam(WebConstants.FROM_PAGE_PARAM) String fromPage) {
		CourseApplication courseApplication = null;
		if (StringUtils.hasText(uuid)) {
			courseApplication = courseApplicationService.getByUuid(UUID.fromString(uuid));
		}
		initItem(courseApplication);
		this.editMode = true;
		this.securedMode = isSecuredPage();

		this.returnToPage = StringUtils.hasText(fromPage) ? WebPages.valueOf(fromPage).getUrl() : null;
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
//				this.confirmText = Labels.getLabel("msg.ui.info.changesSaved");
			} else {
				// create
				if (!this.healthInfoAgreement || !this.personalInfoProcessAgreement) {
					WebUtils.showNotificationWarning(Labels.getLabel("msg.ui.warn.agreementWithHealtAndDataInfo"));
					return;
				}
				courseApplicationService.store(application);
				WebUtils.showNotificationInfo(Labels.getLabel("msg.ui.info.applicationSend"));
				this.editMode = false;
//				this.confirmText = Labels.getLabel("msg.ui.info.applicationSend");
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
}