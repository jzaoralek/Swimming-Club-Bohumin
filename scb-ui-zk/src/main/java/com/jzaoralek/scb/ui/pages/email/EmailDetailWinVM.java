package com.jzaoralek.scb.ui.pages.email;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.javatuples.Pair;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Bandpopup;

import com.jzaoralek.scb.dataservice.domain.Attachment;
import com.jzaoralek.scb.dataservice.domain.Contact;
import com.jzaoralek.scb.dataservice.domain.Mail;
import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.events.SzpEventListener;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEvent;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEventQueues;
import com.jzaoralek.scb.ui.common.utils.MessageBoxUtils;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.BaseVM;
import com.jzaoralek.scb.ui.pages.courseapplication.vm.MailRecipientSelectionVM.RecipientType;

/**
 * View model for email message detail window.
 * @author jakub.zaoralek
 *
 */
public class EmailDetailWinVM extends BaseVM {

	private static final String MESSAGE_SEND_CONFIRM_TITLE_MSG_KEY = "msg.ui.quest.title.messageSendConfirm";
	private Set<Contact> mailToContactSet;
	private Set<Contact> mailCcContactSet;
	private String messageSubject;
	private String messageText;
	private String mailTo;
	private String mailCc;
	private List<Attachment> attachmentList;
	private final EmailAddressFilter filter = new EmailAddressFilter();
	
	@Wire
	private Bandpopup mailToPopup;
	
	@Wire
	private Bandpopup mailCcPopup;

	@SuppressWarnings("unchecked")
	@Override
	@Init
	public void init() {
		// iniciace adres z jiné stránky
		Set<Contact> emailAddrSet = (Set<Contact>) WebUtils.getSessAtribute(WebConstants.EMAIL_RECIPIENT_LIST_PARAM);
		if (!CollectionUtils.isEmpty(emailAddrSet)) {
			this.mailToContactSet = emailAddrSet;
			WebUtils.removeSessAtribute(WebConstants.EMAIL_RECIPIENT_LIST_PARAM);
		}
		
		EventQueueHelper.queueLookup(ScbEventQueues.MAIL_QUEUE).subscribe(ScbEvent.ADD_TO_RECIPIENT_LIST_EVENT, data -> {
			addMailToAddress((Pair<Set<Contact>,RecipientType>) data);
        });
		EventQueueHelper.queueLookup(ScbEventQueues.MAIL_QUEUE).subscribe(ScbEvent.CLOSE_RECIPIENT_SELECTION_POPUP_EVENT, data -> {
			closeRecipientSelectionPopup((RecipientType) data);
        });
	}
	
	@AfterCompose
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
		Selectors.wireComponents(view, this, false);
	}
	
	@Command
	public void sendCmd() {
		// validations
		// at least one mailTo email address
		if (!StringUtils.hasText(this.mailTo)) {
			// zadna emailova adresa neni zadana
			MessageBoxUtils.showOkWarningDialog("msg.ui.warn.MessageEnterToAddress", MESSAGE_SEND_CONFIRM_TITLE_MSG_KEY, null);
			return;
		}
		
		// no mailTo addresses valid
		Pair<List<String>, List<String>> mailToValidResult = WebUtils.validateEmailList(this.mailTo);
		if (CollectionUtils.isEmpty(mailToValidResult.getValue0())) {
			// zadna ze zadanych adres neni platna
			MessageBoxUtils.showOkWarningDialog("msg.ui.warn.MessageEnterValidToAddress", MESSAGE_SEND_CONFIRM_TITLE_MSG_KEY, null);
			return;
		}
		List<String> invalidEmailList = mailToValidResult.getValue1();
		if (!CollectionUtils.isEmpty(invalidEmailList)) {
			// neplatne adresy prijemcu
			MessageBoxUtils.showOkWarningDialog("msg.ui.warn.MessageInvalidToAddress", MESSAGE_SEND_CONFIRM_TITLE_MSG_KEY, null, invalidEmailList);
			return;
		}
		
		// no mailCc addresses valid
		Pair<List<String>, List<String>> mailCcValidResult = WebUtils.validateEmailList(this.mailCc);
		if (mailCcValidResult != null) {
			invalidEmailList = mailCcValidResult.getValue1();
			if (!CollectionUtils.isEmpty(invalidEmailList)) {
				// neplatne adresy prijemcu
				MessageBoxUtils.showOkWarningDialog("msg.ui.warn.MessageInvalidCcAddress", MESSAGE_SEND_CONFIRM_TITLE_MSG_KEY, null, invalidEmailList);
				return;
			}			
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
					MESSAGE_SEND_CONFIRM_TITLE_MSG_KEY,
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
		List<Mail> mailList = new ArrayList<>();
		Set<String> mailToList = WebUtils.emailAddressStrToList(this.mailTo);
		if (!CollectionUtils.isEmpty(mailToList)) {
			mailToList.forEach(i -> mailList.add(Mail.ofHtml(i.trim(), null,  this.messageSubject, this.messageText, this.attachmentList)));			
		}
		
		Set<String> mailCcList = WebUtils.emailAddressStrToList(this.mailCc);
		if (!CollectionUtils.isEmpty(mailCcList)) {
			mailCcList.forEach(i -> mailList.add(Mail.ofHtml(i.trim(), null,  this.messageSubject, this.messageText, this.attachmentList)));			
		}
		
		mailService.sendMailBatch(mailList);
		WebUtils.showNotificationInfo(Labels.getLabel("msg.ui.info.messageSent"));
		clearMessage();
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
	
	@NotifyChange("mailTo")
	@Command
	public void updateValueCmd() {
		this.mailTo = "asasas";
	}
	
	private void clearMessage() {
		this.messageSubject = "";
		this.messageText = "";
		this.mailTo = "";
		this.mailCc = "";
		this.attachmentList = null;
		if (!CollectionUtils.isEmpty(this.mailToContactSet)) {
			this.mailToContactSet.clear();			
		}
		if (!CollectionUtils.isEmpty(this.mailCcContactSet)) {
			this.mailCcContactSet.clear();			
		}
		
		BindUtils.postNotifyChange(null, null, this, "*");
	}
	
	@Command
	public void initMailToPopupCmd() {
		EventQueueHelper.publish(ScbEvent.INIT_RECIPIENT_SELECTION_EVENT, RecipientType.TO);
	}
	
	@Command
	public void initMailCcPopupCmd() {
		EventQueueHelper.publish(ScbEvent.INIT_RECIPIENT_SELECTION_EVENT, RecipientType.CC);
	}
	
	/**
	 * Remove email contact from emailAddressSet.
	 * @param item
	 */
	@NotifyChange("mailToContactSet")
	@Command
	public void removeRecipientCmd(@BindingParam(WebConstants.ITEM_PARAM) Contact item) {
		Objects.requireNonNull(item, "item");
		
		if (CollectionUtils.isEmpty(this.mailToContactSet)) {
			return;
		}
		
		this.mailToContactSet.remove(item);
	}
	
	/**
	 * Prida emailove adresy do seznamu adresatu.
	 * @param mailToList
	 */
	private void addMailToAddress(Pair<Set<Contact>,RecipientType> emailListWithType) {
		Set<Contact> emailList = emailListWithType.getValue0();
		if (!CollectionUtils.isEmpty(emailList)) {
			if (emailListWithType.getValue1() == RecipientType.TO) {
				// pridani do mailTo kontaktu
				if (this.mailToContactSet == null) {
					this.mailToContactSet = new HashSet<>();
				}
				this.mailToContactSet.addAll(emailList);
				BindUtils.postNotifyChange(null, null, this,"mailToContactSet");
			} else {
				// pridani do mailCc kontaktu
				if (this.mailCcContactSet == null) {
					this.mailCcContactSet = new HashSet<>();
				}
				this.mailCcContactSet.addAll(emailList);		
				BindUtils.postNotifyChange(null, null, this,"mailToContactSet");
			}
		}
	}
	
	/**
	 * Zavre popup s vyberem adresastu.
	 */
	private void closeRecipientSelectionPopup(RecipientType recipientType) {
		Bandbox bandbox = null;
		if (recipientType == RecipientType.TO) {
			// zavreni popupu To
			bandbox = (Bandbox)this.mailToPopup.getParent();
			bandbox.close();			
		} else {
			// zavreni popupu Cc
			bandbox = (Bandbox)this.mailCcPopup.getParent();
			bandbox.close();
		}		
	}
	
	public int getEmailAddressCount() {
		if (CollectionUtils.isEmpty(this.mailToContactSet)) {
			return 0;
		}
		
		return this.mailToContactSet.size();
	}
	
	@DependsOn({"messageSubject", "messageText", "mailTo", "mailCc", "attachmentList"})
	public boolean isMessageEmpty() {
		return !StringUtils.hasText(this.messageSubject)
			&& !StringUtils.hasText(this.messageText)
			&& !StringUtils.hasText(this.mailTo)
			&& !StringUtils.hasText(this.mailCc)
			&& CollectionUtils.isEmpty(this.mailToContactSet)
			&& CollectionUtils.isEmpty(this.mailCcContactSet)
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
	public Set<Contact> getMailToContactSet() {
		return mailToContactSet;
	}
	public Set<Contact> getMailCcContactSet() {
		return mailCcContactSet;
	}
	
	public static class EmailAddressFilter {

		private String address;
		private String addressLc;

		public boolean matches(String addressIn, boolean emptyMatch) {
			if (address == null) {
				return emptyMatch;
			}
			return addressIn.toLowerCase().contains(addressLc);
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