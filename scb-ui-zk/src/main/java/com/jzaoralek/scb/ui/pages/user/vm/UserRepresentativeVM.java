package com.jzaoralek.scb.ui.pages.user.vm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import com.jzaoralek.scb.dataservice.domain.ScbUser;
import com.jzaoralek.scb.dataservice.exception.ScbValidationException;
import com.jzaoralek.scb.dataservice.service.ScbUserService;
import com.jzaoralek.scb.dataservice.utils.SecurityUtils;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.BaseVM;

/**
 * Udaje o prihlasenem uzivateli / zakonnem zastupci
 *
 */
public class UserRepresentativeVM extends BaseVM {

	private static final Logger LOG = LoggerFactory.getLogger(UserRepresentativeVM.class);

	@WireVariable
	private ScbUserService scbUserService;
	
	private ScbUser user;

	@Init
	public void init() {
		this.user = SecurityUtils.getLoggedUser();
	}
	
	@NotifyChange("*")
	@Command
    public void submit() {
		try {
			scbUserService.store(this.user);
			this.user = SecurityUtils.getLoggedUser();
			WebUtils.showNotificationInfo(Labels.getLabel("msg.ui.info.changesSaved"));
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
}
