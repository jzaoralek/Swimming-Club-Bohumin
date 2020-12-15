package com.jzaoralek.scb.ui.common.security;

import org.springframework.util.StringUtils;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.QueryParam;
import org.zkoss.zk.ui.Executions;

import com.jzaoralek.scb.dataservice.utils.SecurityUtils;
import com.jzaoralek.scb.ui.common.utils.ConfigUtil;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.BaseVM;

public class SecurityVM extends BaseVM {

	private static final String CHANGE_PASSWORD_WINDOW= "/pages/secured/change-password-window.zul";

	private String username;
	private String password;

	private Boolean loginFailure;

	@Init
	public void init(@QueryParam("loginFailure") String loginFailure) {
		// zobrazeni alertu o neuspesnem prihlaseni
		this.loginFailure = StringUtils.hasText(loginFailure);
	}

    @Command
	public void changePasswordCmd() {
		WebUtils.openModal(CHANGE_PASSWORD_WINDOW);
	}

    public boolean isPaymentsAvailable() {
		return ConfigUtil.isPaymentsAvailable(configurationService);
	}
    
    public String getLoggedUser() {
		return SecurityUtils.getLoggedUserUsername();
	}

    public String getLoggedUserCompleteName() {
		return SecurityUtils.getLoggedUserCompleteName();
	}

    public Boolean getLoginFailure() {
		return this.loginFailure;
	}

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}