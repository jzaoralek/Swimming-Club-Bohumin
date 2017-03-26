package com.jzaoralek.scb.ui.pages.security.vm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import com.jzaoralek.scb.dataservice.domain.ScbUser;
import com.jzaoralek.scb.dataservice.service.ScbUserService;
import com.jzaoralek.scb.dataservice.utils.SecurityUtils;
import com.jzaoralek.scb.dataservice.utils.vo.Cover;
import com.jzaoralek.scb.ui.common.vm.BaseVM;

public class ForgottenPasswordVM extends BaseVM {

	private static final Logger LOG = LoggerFactory.getLogger(ForgottenPasswordVM.class);

	@WireVariable
	private ScbUserService scbUserService;
	
	private String username;
	private boolean showNotification;
	private String confirmText;

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
		
		this.showNotification = true;
		this.confirmText = Labels.getLabel("msg.ui.info.passwordResetAndSendToEmail2", new Object[] {user.getUsername()});
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public boolean isShowNotification() {
		return showNotification;
	}
	
	public String getConfirmText() {
		return confirmText;
	}
}