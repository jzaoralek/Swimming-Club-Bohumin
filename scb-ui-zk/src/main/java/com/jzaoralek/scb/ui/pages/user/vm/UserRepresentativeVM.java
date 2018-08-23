package com.jzaoralek.scb.ui.pages.user.vm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Messagebox;

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
	
	/**
	 * Kontroluje pouziti emailu jako defaultniho prihlasovaciho jmena, pokud je jiz evidovano, nabidne predvyplneni hodnot zakonneho zastupce.
	 * @param email
	 * @param fx
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@NotifyChange("*")
	@Command
	public void validateUniqueUsernameCmd(@BindingParam("email") String email, @BindingParam("fx") final UserRepresentativeVM fx) {
		// pokud existuje uzivatel se stejnym username jako je zadany email, zobrazit upozorneni a nepovolit vyplnit
		final ScbUser scbUser = scbUserService.getByUsername(email);
		if (scbUser != null && !this.user.getUsername().equals(email)) {
			String question = Labels.getLabel("msg.ui.quest.participantRepresentativeExists",new Object[] {email, scbUser.getContact().getCompleteName()});			
			Messagebox.show(question, Labels.getLabel("txt.ui.common.warning"), Messagebox.OK, Messagebox.EXCLAMATION, new org.zkoss.zk.ui.event.EventListener() {
			    public void onEvent(Event evt) throws InterruptedException {
			        // vymazat email
			        fx.getUser().getContact().setEmail1("");
			        BindUtils.postNotifyChange(null, null, fx, "*");
			    }
			});
		}
	}
	
	public ScbUser getUser() {
		return user;
	}

	public void setUser(ScbUser user) {
		this.user = user;
	}
}
