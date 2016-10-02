package com.jzaoralek.scb.ui.pages.security.vm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Window;

import com.jzaoralek.scb.dataservice.domain.ScbUser;
import com.jzaoralek.scb.dataservice.service.ScbUserService;
import com.jzaoralek.scb.dataservice.utils.SecurityUtils;
import com.jzaoralek.scb.dataservice.utils.vo.Cover;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.BaseVM;

public class ChangePasswordWinVM extends BaseVM {

	private static final Logger LOG = LoggerFactory.getLogger(ChangePasswordWinVM.class);

	@WireVariable
	private ScbUserService scbUserService;

	private String actualPassword;
	private String newPassword;
	private String newPassword2;

	@Command
	public void submitCmd(@BindingParam("window") Window window) {
		String username = SecurityUtils.getLoggedUserUsername();
		LOG.info("Changing password for : " + username);
		ScbUser user = SecurityUtils.getLoggedUser();
		user.setPasswordGenerated(false);
		scbUserService.changePassword(user, new Cover<>(this.newPassword.toCharArray()));
		this.newPassword = null;
		this.newPassword2 = null;
		WebUtils.showNotificationInfo(Labels.getLabel("msg.ui.info.passwordChanged"));
		LOG.info("Password changed for : " + username);
		window.detach();
	}

	public String getActualPassword() {
		return actualPassword;
	}

	public void setActualPassword(String actualPassword) {
		this.actualPassword = actualPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getNewPassword2() {
		return newPassword2;
	}

	public void setNewPassword2(String newPassword2) {
		this.newPassword2 = newPassword2;
	}
}
