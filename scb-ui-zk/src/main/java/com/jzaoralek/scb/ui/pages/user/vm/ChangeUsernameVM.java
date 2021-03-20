package com.jzaoralek.scb.ui.pages.user.vm;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.QueryParam;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import com.jzaoralek.scb.dataservice.domain.ScbUser;
import com.jzaoralek.scb.dataservice.exception.ScbValidationException;
import com.jzaoralek.scb.dataservice.service.ScbUserService;
import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.WebPages;
import com.jzaoralek.scb.ui.common.template.SideMenuComposer.ScbMenuItem;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.BaseVM;

public class ChangeUsernameVM extends BaseVM {
	
	private static final Logger LOG = LoggerFactory.getLogger(ChangeUsernameVM.class);
	
	@WireVariable
	private ScbUserService scbUserService;

	private ScbUser user;
	private String usernameRepeat;
	private boolean updateEmail1;

	@Init
	public void init(@QueryParam(WebConstants.UUID_PARAM) final String uuid, 
			@QueryParam(WebConstants.FROM_PAGE_PARAM) String fromPage) {
		super.init();
        setMenuSelected(ScbMenuItem.SENAM_UZIVATELU);
        this.user = scbUserService.getByUuid(UUID.fromString(uuid));
        buildPageHeadline();
        buildReturnToPage(fromPage);
	}
	
	@Command
	public void submitCmd() {
		try {
			if (this.updateEmail1) {
				this.user.getContact().setEmail1(this.user.getUsername());
			}
			scbUserService.store(this.user);
			
			sendMailWithChangedUsername(this.user);
			
			if (isLoggedUserAdmin()) {
				// admin - redirect na detail uzivatele
				WebUtils.showNotificationInfoAfterRedirect(Labels.getLabel("msg.ui.info.usernameChanged", new Object[] {this.user.getUsername()}));
				Executions.sendRedirect(this.returnToPage);
			} else {
				// user as course participant representative - logout
				logoutCmd();
			}
			
		} catch (ScbValidationException e) {
			LOG.warn("ScbValidationException caught during storing user: " + this.user, e);
			WebUtils.showNotificationError(e.getMessage());
		}
	}
	
	protected void buildReturnToPage(String fromPage) {
		if (isLoggedUserAdmin()) {
			// admin
			this.returnToPage = WebPages.USER_DETAIL.getUrl() + 
					"?" + WebConstants.UUID_PARAM + "=" + this.user.getUuid() + 
					"&" + WebConstants.FROM_PAGE_PARAM + "=" + fromPage;			
		} else {
			// user as course participant representative
			setReturnPage(fromPage);
		}
	}
	
	private void sendMailWithChangedUsername(ScbUser user) {
		StringBuilder mailToUser = new StringBuilder();
		mailToUser.append(Labels.getLabel("msg.ui.mail.text.usernameChange.text0"));
		mailToUser.append(WebConstants.LINE_SEPARATOR);
		mailToUser.append(WebConstants.LINE_SEPARATOR);
		if (this.updateEmail1) {
			mailToUser.append(Labels.getLabel("msg.ui.mail.text.usernameChange.text1.inclEmail", new Object[] {configurationService.getBaseURL()}));
		} else {
			mailToUser.append(Labels.getLabel("msg.ui.mail.text.usernameChange.text1", new Object[] {configurationService.getBaseURL()}));			
		}
		mailToUser.append(WebConstants.LINE_SEPARATOR);
		mailToUser.append(WebConstants.LINE_SEPARATOR);
		mailToUser.append(Labels.getLabel("msg.ui.mail.text.usernameChange.text2", new Object[] {user.getUsername()}));
		mailToUser.append(WebConstants.LINE_SEPARATOR);
		mailToUser.append(Labels.getLabel("msg.ui.mail.text.usernameChange.text3", new Object[] {user.getPassword()}));
		if (this.updateEmail1) {
			mailToUser.append(WebConstants.LINE_SEPARATOR);
			mailToUser.append(Labels.getLabel("msg.ui.mail.text.usernameChange.text4", new Object[] {user.getContact().getEmail1()}));
		}
		mailToUser.append(WebConstants.LINE_SEPARATOR);
		mailToUser.append(WebConstants.LINE_SEPARATOR);
		mailToUser.append(buildMailSignature());

		mailService.sendMail(user.getContact().getEmail1(), null, Labels.getLabel("msg.ui.mail.subject.changeUsername"), mailToUser.toString(), null, false, false, null);
	}
	
	private void buildPageHeadline() {
		this.pageHeadline = Labels.getLabel("txt.ui.common.changeUsername") + " | " + user.getUsername();
	}
	
	public ScbUser getUser() {
		return user;
	}
	public void setUser(ScbUser user) {
		this.user = user;
	}
	public String getUsernameRepeat() {
		return usernameRepeat;
	}
	public void setUsernameRepeat(String usernameRepeat) {
		this.usernameRepeat = usernameRepeat;
	}
	public boolean isUpdateEmail1() {
		return updateEmail1;
	}
	public void setUpdateEmail1(boolean updateEmail1) {
		this.updateEmail1 = updateEmail1;
	}
}