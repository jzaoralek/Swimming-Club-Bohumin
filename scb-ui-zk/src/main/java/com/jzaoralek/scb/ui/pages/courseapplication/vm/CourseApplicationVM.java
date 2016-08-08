package com.jzaoralek.scb.ui.pages.courseapplication.vm;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import com.jzaoralek.scb.dataservice.domain.CourseApplication;
import com.jzaoralek.scb.dataservice.exception.ScbValidationException;
import com.jzaoralek.scb.dataservice.service.CourseApplicationService;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.BaseVM;

public class CourseApplicationVM extends BaseVM {

	private CourseApplication application;
	private boolean healthInfoAgreement;
	private boolean personalInfoProcessAgreement;
	private boolean editMode;
	private String confirmText;

	@WireVariable
	private CourseApplicationService courseApplicationService;

	@Init
	public void init() {
		initItem();
		this.editMode = true;
	}

	@NotifyChange("*")
	@Command
    public void submit() {
		if (!this.healthInfoAgreement || !this.personalInfoProcessAgreement) {
			WebUtils.showNotificationWarning(Labels.getLabel("msg.ui.warn.agreementWithHealtAndDataInfo"));
			return;
		}

		try {
			courseApplicationService.store(application);
			//WebUtils.showNotificationInfo(Labels.getLabel("msg.ui.info.applicationSend"));
			initItem();
			this.editMode = false;
			this.confirmText = Labels.getLabel("msg.ui.info.applicationSend");
		} catch (ScbValidationException e) {
			WebUtils.showNotificationError(e.getMessage());
		}
    }

	private void initItem() {
		this.application = new CourseApplication();
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
	public String getConfirmText() {
		return confirmText;
	}
}