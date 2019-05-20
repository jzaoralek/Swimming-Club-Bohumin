package com.jzaoralek.scb.ui.pages.security.vm;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;

import com.jzaoralek.scb.dataservice.domain.ScbUser;
import com.jzaoralek.scb.dataservice.utils.SecurityUtils;
import com.jzaoralek.scb.dataservice.utils.vo.Cover;
import com.jzaoralek.scb.ui.common.WebPages;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.BaseVM;

public class ForgottenPasswordVM extends BaseVM {
	
	private String username;
	private String confirmText;

	@Override
	@Init
	public void init() {
		super.init();
	}

	@NotifyChange("*")
	@Command
    public void submit() {
		ScbUser user = scbUserService.getByUsername(this.username);
		
		String newPassword = SecurityUtils.generatePassword();
		user.setPasswordGenerated(true);
		user.setPassword(newPassword);
		scbUserService.changePassword(user, new Cover<>(newPassword.toCharArray()));
		
		// send mail
		sendMailWithResetpassword(user);
		
		WebUtils.showNotificationInfoAfterRedirect(Labels.getLabel("msg.ui.info.passwordResetAndSendToEmail2", new Object[] {user.getUsername()}));
		Executions.sendRedirect(WebPages.LOGIN_PAGE.getUrl());
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getConfirmText() {
		return confirmText;
	}
}