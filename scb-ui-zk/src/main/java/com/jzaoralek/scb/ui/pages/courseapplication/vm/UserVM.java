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
import org.zkoss.zul.Listitem;

import com.jzaoralek.scb.dataservice.domain.ScbUser;
import com.jzaoralek.scb.dataservice.domain.ScbUserRole;
import com.jzaoralek.scb.dataservice.exception.ScbValidationException;
import com.jzaoralek.scb.dataservice.service.ScbUserService;
import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.template.SideMenuComposer.ScbMenuItem;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.BaseVM;

public class UserVM extends BaseVM {

	private static final Logger LOG = LoggerFactory.getLogger(UserVM.class);

	@WireVariable
	private ScbUserService scbUserService;

	private ScbUser user;
	private String pageHeadline;
	private boolean updateMode;
	private Listitem roleSelected;

	@Init
	public void init(@QueryParam(WebConstants.UUID_PARAM) final String uuid, @QueryParam(WebConstants.FROM_PAGE_PARAM) String fromPage) {
        setMenuSelected(ScbMenuItem.SENAM_UZIVATELU);
        if (StringUtils.hasText(uuid)) {
			this.user = scbUserService.getByUuid(UUID.fromString(uuid));
			this.roleSelected = getRoleListItem(this.user.getRole());
			this.updateMode = true;
		} else {
			this.user = new ScbUser();
			this.roleSelected = getRoleListItem(ScbUserRole.ADMIN);
			this.updateMode = false;
		}
		buildPageHeadline();
		setReturnPage(fromPage);
	}

	private void buildPageHeadline() {
		this.pageHeadline = this.user.getUuid() == null ? Labels.getLabel("txt.ui.common.NewUser") : Labels.getLabel("txt.ui.common.UserDetail") + " " + user.getUsername();
	}

	@NotifyChange("*")
	@Command
    public void submit() {
		try {
			this.user.setRole((ScbUserRole)this.roleSelected.getValue());
			this.user = scbUserService.store(this.user);
			if (!this.updateMode) {
				// create new user, notification about send mail with password
				sendMailToNewUser(this.user);
				WebUtils.showNotificationInfo(Labels.getLabel("msg.ui.info.userCreatedAdnSendMail", new Object[] {user.getUsername(), user.getContact().getEmail1()}));
			} else {
				// update user, just notification that changes saved
				WebUtils.showNotificationInfo(Labels.getLabel("msg.ui.info.changesSaved"));
			}
			buildPageHeadline();
			this.updateMode = true;
		} catch (ScbValidationException e) {
			LOG.warn("ScbValidationException caught during storing user: " + this.user, e);
			WebUtils.showNotificationError(e.getMessage());
		}
	}

	public ScbUser getUser() {
		return user;
	}

	public void setUser(ScbUser user) {
		this.user = user;
	}

	public boolean isUpdateMode() {
		return updateMode;
	}

	public String getPageHeadline() {
		return pageHeadline;
	}

	public Listitem getRoleSelected() {
		return roleSelected;
	}

	public void setRoleSelected(Listitem roleSelected) {
		this.roleSelected = roleSelected;
	}
}
