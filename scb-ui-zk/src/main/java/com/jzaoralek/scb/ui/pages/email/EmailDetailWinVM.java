package com.jzaoralek.scb.ui.pages.email;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.javatuples.Pair;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Window;

import com.jzaoralek.scb.dataservice.domain.Attachment;
import com.jzaoralek.scb.dataservice.domain.Mail;
import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.events.SzpEventListener;
import com.jzaoralek.scb.ui.common.utils.MessageBoxUtils;
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
	private String mailTo;
	private String mailCc;
	private List<Attachment> attachmentList;
	private final EmailAddressFilter filter = new EmailAddressFilter();

	@SuppressWarnings("unchecked")
	@Override
	@Init
	public void init() {
		this.emailAddressSet = (Set<String>) WebUtils.getArg(WebConstants.COURSE_PARTIC_CONTACT_LIST_PARAM);
		this.emailAddressSetBase = this.emailAddressSet;
	}
	
	@Command
	public void submitCmd(@BindingParam("window") Window window) {
		if (CollectionUtils.isEmpty(this.emailAddressSet)) {
			return;
		}
		
		List<Mail> mailList = new ArrayList<>();
		for (String item : this.emailAddressSet) {
			mailList.add(new Mail(item, null,  this.messageSubject, this.messageText, this.attachmentList));
		}
		
		mailService.sendMailBatch(mailList);
		
		WebUtils.showNotificationInfo(Labels.getLabel("msg.ui.info.messageSent"));
		window.detach();
	}
	
	@Command
	public void sendCmd() {
		// validations
		// valid at least one to email address
		if (!StringUtils.hasText(this.mailTo)) {
			// zadna emailova adresa neni zadana
			MessageBoxUtils.showOkWarningDialog("msg.ui.warn.MessageEnterToAddress", "msg.ui.quest.title.messageSendConfirm", null);
			return;
		}
		
		// not valid to email address
		Pair<List<String>, List<String>> mailToValidResult = WebUtils.validateEmailList(this.mailTo);
		if (CollectionUtils.isEmpty(mailToValidResult.getValue0())) {
			// zadna ze zadanych adres neni platna
			MessageBoxUtils.showOkWarningDialog("msg.ui.warn.MessageEnterValidToAddress", "msg.ui.quest.title.messageSendConfirm", null);
			return;
		}
		List<String> invalidEmailList = mailToValidResult.getValue1();
		if (!CollectionUtils.isEmpty(invalidEmailList)) {
			// neplatne adresy prijemcu
			MessageBoxUtils.showOkWarningDialog("msg.ui.warn.MessageInvalidToAddress", "msg.ui.quest.title.messageSendConfirm", null);
			return;
		}
		
		// not valid cc email address
		Pair<List<String>, List<String>> mailCcValidResult = WebUtils.validateEmailList(this.mailCc);
		if (!CollectionUtils.isEmpty(mailCcValidResult.getValue1())) {
			// neplatne adresy prijemcu
			MessageBoxUtils.showOkWarningDialog("msg.ui.warn.MessageInvalidCcAddress", "msg.ui.quest.title.messageSendConfirm", null);
			return;
		}
		
		// confirmations
		// not filled subject
		boolean emptySubject = !StringUtils.hasText(this.messageSubject);
		// not filled text
		boolean emptyText = !StringUtils.hasText(this.messageText);
		
		String confirmMessage = null;
		if (emptySubject && emptyText) {
			confirmMessage = "msg.ui.quest.MessageSendWithoutSubjectAndText";
		} else if (emptySubject) {
			confirmMessage = "msg.ui.quest.MessageSendWithoutSubject";
		} else if (emptyText) {
			confirmMessage = "msg.ui.quest.MessageSendWithoutText";
		}
		
		if (StringUtils.hasText(confirmMessage)) {
			MessageBoxUtils.showDefaultConfirmDialog(
					confirmMessage,
					"msg.ui.quest.title.messageSendConfirm",
					new SzpEventListener() {
						@Override
						public void onOkEvent() {
							sendMessage();
						}
					}
				);
		} else {
			sendMessage();
		}
	}
	
	private void sendMessage() {
		mailService.sendMailBatch(Arrays.asList(new Mail(this.mailTo, this.mailCc,  this.messageSubject, this.messageText, this.attachmentList)));
		WebUtils.showNotificationInfo(Labels.getLabel("msg.ui.info.messageSent"));
		clearMessage();
	}
	
	@Command
	@NotifyChange("emailAddressSet")
	public void filterDomCmd() {
		this.emailAddressSet = filter.getEmailListFiltered(this.emailAddressSetBase);
	}
	
	/**
	 * Add attachment
	 */
	@NotifyChange("attachmentList")
	@Command
	public void uploadCmd(@BindingParam("event") UploadEvent event) {
		Media media = event.getMedia();
		
		Attachment attachment = new Attachment();
		attachment.setByteArray(media.getByteData());
		attachment.setContentType(media.getContentType());
		attachment.setName(media.getName());
		
		if (this.attachmentList == null) {
			this.attachmentList = new ArrayList<>();
		}
		
		this.attachmentList.add(attachment);		
	}
	
	@Command
	public void downloadAttachmentCmd(@BindingParam(WebConstants.ITEM_PARAM) Attachment item) {
		WebUtils.downloadAttachment(item);
	}
	
	/**
	 * Add attachment
	 */
	@NotifyChange("attachmentList")
	@Command
	public void removeAttachmentCmd(@BindingParam(WebConstants.ITEM_PARAM) Attachment item) {
		if (this.attachmentList == null) {
			return;
		}
		
		this.attachmentList.remove(item);
	}
	
	@Command
	public void clearMessageCmd() {
		if (isMessageEmpty()) {
			return;
		}
		
		MessageBoxUtils.showDefaultConfirmDialog(
			"msg.ui.quest.MessageThrowOut",
			"msg.ui.title.MessageThrowOut",
			new SzpEventListener() {
				@Override
				public void onOkEvent() {
					clearMessage();
				}
			}
		);
	}
	
	private void clearMessage() {
		this.messageSubject = "";
		this.messageText = "";
		this.mailTo = "";
		this.mailCc = "";
		this.attachmentList = null;
		
		BindUtils.postNotifyChange(null, null, this, "*");
	}
	
	public int getEmailAddressCount() {
		if (CollectionUtils.isEmpty(this.emailAddressSet)) {
			return 0;
		}
		
		return this.emailAddressSet.size();
	}
	
	@DependsOn({"messageSubject", "messageText", "mailTo", "mailCc", "attachmentList"})
	public boolean isMessageEmpty() {
		return !StringUtils.hasText(this.messageSubject)
			&& !StringUtils.hasText(this.messageText)
			&& !StringUtils.hasText(this.mailTo)
			&& !StringUtils.hasText(this.mailCc)
			&& this.attachmentList == null;
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
	public List<Attachment> getAttachmentList() {
		return attachmentList;
	}
	public String getMailTo() {
		return mailTo;
	}
	public void setMailTo(String mailTo) {
		this.mailTo = mailTo;
	}
	public String getMailCc() {
		return mailCc;
	}
	public void setMailCc(String mailCc) {
		this.mailCc = mailCc;
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