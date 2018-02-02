package com.jzaoralek.scb.ui.pages.email;

import java.util.Set;

import org.springframework.util.CollectionUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Window;

import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.BaseVM;

/**
 * View model for email message detail window.
 * @author jakub.zaoralek
 *
 */
public class EmailDetailWinVM extends BaseVM {

	private Set<String> emailAddressSet;
	private String messageSubject;
	private String messageText;

	@SuppressWarnings("unchecked")
	@Override
	@Init
	public void init() {
		this.emailAddressSet = (Set<String>) WebUtils.getArg(WebConstants.COURSE_PARTIC_CONTACT_LIST_PARAM);
		this.pageHeadline = Labels.getLabel("txt.ui.common.NovaZprava");
	}
	
	@Command
	public void submitCmd(@BindingParam("window") Window window) {
		for (String item : this.emailAddressSet) {
			mailService.sendMail(item, this.messageSubject, this.messageText, null, null);
		}
		
		WebUtils.showNotificationInfo(Labels.getLabel("msg.ui.info.messageSent"));
		window.detach();
	}
	
	public int getEmailAddressCount() {
		if (CollectionUtils.isEmpty(this.emailAddressSet)) {
			return 0;
		}
		
		return this.emailAddressSet.size();
	}
	
	public String getMessageSubject() {
		return messageSubject;
	}

	public void setMessageSubject(String messageSubject) {
		this.messageSubject = messageSubject;
	}

	public String getMessageText() {
		return messageText;
	}

	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}

	public Set<String> getEmailAddressSet() {
		return emailAddressSet;
	}
}