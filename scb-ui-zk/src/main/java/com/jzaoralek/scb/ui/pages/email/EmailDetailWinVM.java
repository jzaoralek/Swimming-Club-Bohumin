package com.jzaoralek.scb.ui.pages.email;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
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
	private Set<String> emailAddressSetBase;
	private String messageSubject;
	private String messageText;
	private final EmailAddressFilter filter = new EmailAddressFilter();

	@SuppressWarnings("unchecked")
	@Override
	@Init
	public void init() {
		this.emailAddressSet = (Set<String>) WebUtils.getArg(WebConstants.COURSE_PARTIC_CONTACT_LIST_PARAM);
		this.emailAddressSetBase = this.emailAddressSet;
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
	
	@Command
	@NotifyChange("emailAddressSet")
	public void filterDomCmd() {
		this.emailAddressSet = filter.getEmailListFiltered(this.emailAddressSetBase);
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
	
	public void setEmailAddressSet(Set<String> emailAddressSet) {
		this.emailAddressSet = emailAddressSet;
	}
	
	public EmailAddressFilter getFilter() {
		return filter;
	}
	
	public static class EmailAddressFilter {

		private String address;
		private String addressLc;

		public boolean matches(String addressIn, boolean emptyMatch) {
			if (address == null) {
				return emptyMatch;
			}
			if (address != null && !addressIn.toLowerCase().contains(addressLc)) {
				return false;
			}
			return true;
		}

		public Set<String> getEmailListFiltered(Set<String> courseList) {
			if (courseList == null || courseList.isEmpty()) {
				return Collections.<String>emptySet();
			}
			Set<String> ret = new HashSet<>();
			for (String item : courseList) {
				if (matches(item, true)) {
					ret.add(item);
				}
			}
			return ret;
		}

		public String getAddress() {
			return address == null ? "" : address;
		}
		public void setAddress(String name) {
			this.address = StringUtils.hasText(name) ? name.trim() : null;
			this.addressLc = this.address == null ? null : this.address.toLowerCase();
		}

		public void setEmptyValues() {
			address = null;
			addressLc = null;
		}
	}
}