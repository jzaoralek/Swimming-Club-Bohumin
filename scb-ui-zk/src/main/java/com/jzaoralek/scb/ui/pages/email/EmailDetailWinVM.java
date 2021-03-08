package com.jzaoralek.scb.ui.pages.email;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedHashSet;
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
import org.zkoss.zul.Button;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Popup;

import com.jzaoralek.scb.dataservice.domain.Attachment;
import com.jzaoralek.scb.dataservice.domain.Contact;
import com.jzaoralek.scb.dataservice.domain.Mail;
import com.jzaoralek.scb.dataservice.domain.MailSend;
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
	private static final String MAIL_TO_CONTACT_SET_ATTRIBUTE = "mailToContactSet";
	private static final String MAIL_CC_CONTACT_SET_ATTRIBUTE = "mailCcContactSet";
		
			
	private Set<Contact> mailToContactSet = new LinkedHashSet<>();
	private Set<Contact> mailCcContactSet = new LinkedHashSet<>();
	private String messageSubject;
	private String messageText;
	private String mailTo;
	private String mailCc;
	private List<Attachment> attachmentList;
	private boolean ccVisible = false;
	private final EmailAddressFilter filter = new EmailAddressFilter();
	private ListModel<String> emailListModel;
	private List<MailSend> mailSendList;
	private List<MailSend> mailSendSelectedList;
	private MailSend mailSendSelected;

	//	@Wire
//	private Bandpopup mailToPopup;
//	@Wire
//	private Bandpopup mailCcPopup;
	@Wire
	private Popup mailToPopupBtn;
	@Wire
	private Popup mailCcPopupBtn;
	@Wire
	private Button mailToBtn;
	@Wire
	private Button mailCcBtn;
	@Wire
	private Bandbox ccTxt;
	
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
		
		// load email contact list from cache
//		List<String> emailList = scbUserService.getEmailAll();
//		if (!CollectionUtils.isEmpty(emailList)) {
//			emailListModel = new SimpleListModel<>(emailList.toArray(new String[emailList.size()]));
//		}

		// event listener - pridani kontaktu do seznamu prijemci
		EventQueueHelper.queueLookup(ScbEventQueues.MAIL_QUEUE).subscribe(ScbEvent.ADD_TO_RECIPIENT_LIST_EVENT, data -> {
			addMailToAddress((Pair<Set<Contact>,RecipientType>) data);
        });
		// event listener - zavreni popupu pro vyber prijemcu
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
		String mailToStr = WebUtils.contactListToEmailStr(this.mailToContactSet);
		if (!StringUtils.hasText(mailToStr)) {
			// zadna emailova adresa neni zadana
			MessageBoxUtils.showOkWarningDialog("msg.ui.warn.MessageEnterToAddress", MESSAGE_SEND_CONFIRM_TITLE_MSG_KEY, null);
			return;
		}
		
		// no mailTo addresses valid
		Pair<List<String>, List<String>> mailToValidResult = WebUtils.validateEmailList(mailToStr);
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
		String mailCcStr = WebUtils.contactListToEmailStr(this.mailCcContactSet);
		Pair<List<String>, List<String>> mailCcValidResult = WebUtils.validateEmailList(mailCcStr);
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
		Set<String> mailAddrSet = new LinkedHashSet<>();
		if (!CollectionUtils.isEmpty(this.mailToContactSet)) {
			// unikatnost prohnanim pres Set
			this.mailToContactSet.forEach(i -> mailAddrSet.add(i.getEmail1().trim()));
			mailAddrSet.forEach(i -> mailList.add(Mail.ofHtml(i, null,  this.messageSubject, this.messageText, this.attachmentList)));		
		}
		
		if (!CollectionUtils.isEmpty(this.mailCcContactSet)) {
			// unikatnost prohnanim pres Set
			this.mailCcContactSet.forEach(i -> mailAddrSet.add(i.getEmail1().trim()));	
			mailAddrSet.forEach(i -> mailList.add(Mail.ofHtml(i, null,  this.messageSubject, this.messageText, this.attachmentList)));			
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
	
	/**
	 * Prevede rucne zadany email do seznamu kontaktu prijemcu.
	 * @param recipientType
	 */
	public void addAddressToContactSet(String type) {
		Objects.requireNonNull(type, type);
		RecipientType recipientType = RecipientType.valueOf(type);
		if (recipientType == RecipientType.TO) {
			// pridani adresy do mailToContactSet
			this.mailToContactSet.addAll(getContactSetFromStr(this.mailTo));
			this.mailTo = "";
			BindUtils.postNotifyChange(null, null, this, MAIL_TO_CONTACT_SET_ATTRIBUTE);
			BindUtils.postNotifyChange(null, null, this, "mailTo");
		} else {
			// pridani adresy do mailCcContactSet
			this.mailCcContactSet.addAll(getContactSetFromStr(this.mailCc));
			this.mailCc = "";
			BindUtils.postNotifyChange(null, null, this, MAIL_CC_CONTACT_SET_ATTRIBUTE);
			BindUtils.postNotifyChange(null, null, this, "mailCc");
		}	
	}
	
	@Command
	public void mailRecipientOnOKCmd(@BindingParam("recipientType")String type){
		addAddressToContactSet(type);
	}	
	
	private Set<Contact> getContactSetFromStr(String value) {
		Set<String> mailToSet = WebUtils.emailAddressStrToList(value);
		if (CollectionUtils.isEmpty(mailToSet)) {
			return Collections.emptySet();
		}
		
		Set<Contact> ret = new LinkedHashSet<>();
		List<Contact> contactItemList = null;
		Contact contactItem =  null;
		for (String item : mailToSet) {
			contactItemList = scbUserService.getContactByEmail(item);
			if (!CollectionUtils.isEmpty(contactItemList)) {
				ret.add(contactItemList.get(0));
			} else {
				contactItem = new Contact();
				contactItem.setEmail1(item);
				ret.add(contactItem);
			}
		}
		
		return ret;
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
		mailToPopupBtn.open(mailToBtn);
	}
	
	@Command
	public void initMailCcPopupCmd() {
		EventQueueHelper.publish(ScbEvent.INIT_RECIPIENT_SELECTION_EVENT, RecipientType.CC);
		mailCcPopupBtn.open(mailCcBtn);
	}
	
	/**
	 * Remove email contact from emailAddressSet.
	 * @param item
	 */
	@NotifyChange(MAIL_TO_CONTACT_SET_ATTRIBUTE)
	@Command
	public void removeRecipientCmd(@BindingParam(WebConstants.ITEM_PARAM) Contact item, @BindingParam("recipientType")String type) {
		Objects.requireNonNull(item, "item");
		Objects.requireNonNull(type, type);
		
		RecipientType recipientType = RecipientType.valueOf(type);
		if (recipientType == RecipientType.TO) {
			// odebrani adresy z mailToContactSet
			if (CollectionUtils.isEmpty(this.mailToContactSet)) {
				return;
			}		
			this.mailToContactSet.remove(item);
			BindUtils.postNotifyChange(null, null, this, MAIL_TO_CONTACT_SET_ATTRIBUTE);
		} else {
			// odebrani adresy z mailCcContactSet
			if (CollectionUtils.isEmpty(this.mailCcContactSet)) {
				return;
			}		
			this.mailCcContactSet.remove(item);
			BindUtils.postNotifyChange(null, null, this, MAIL_CC_CONTACT_SET_ATTRIBUTE);
		}
	}
	
	@NotifyChange("ccVisible")
	@Command
	public void ccVisibleCmd() {
		this.ccVisible = true;
		ccTxt.setFocus(true);
	}
	
	@NotifyChange("mailSendList")
	@Command
	public void mailSendSelectedCmd() {
		Calendar fromCal = Calendar.getInstance();
		fromCal.add(Calendar.DATE, -1);
		Calendar toCal = Calendar.getInstance();
		
		this.mailSendList = mailService.getByDateInterval(fromCal.getTime(), toCal.getTime());
	}
	
	@NotifyChange("mailSendSelected")
	@Command
	public void onMailSelectCmd() {
//		this.mailSendSelected = this.mailSendSelectedList.get(0);
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
				this.mailToContactSet.addAll(emailList);
				this.mailToPopupBtn.close();
				BindUtils.postNotifyChange(null, null, this,MAIL_TO_CONTACT_SET_ATTRIBUTE);
			} else {
				// pridani do mailCc kontaktu
				this.mailCcContactSet.addAll(emailList);
				this.mailCcPopupBtn.close();
				BindUtils.postNotifyChange(null, null, this,MAIL_CC_CONTACT_SET_ATTRIBUTE);
			}
		}
	}
	
	/**
	 * Zavre popup s vyberem adresastu.
	 */
	private void closeRecipientSelectionPopup(RecipientType recipientType) {
//		Bandbox bandbox = null;
		if (recipientType == RecipientType.TO) {
			// zavreni popupu To
//			bandbox = (Bandbox)this.mailToPopup.getParent();
//			bandbox.close();
			mailToPopupBtn.close();
		} else {
			// zavreni popupu Cc
//			bandbox = (Bandbox)this.mailCcPopup.getParent();
//			bandbox.close();
			mailCcPopupBtn.close();
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
	public boolean isCcVisible() {
		return ccVisible;
	}
	public ListModel<String> getEmailListModel() {
		return emailListModel;
	}
	public List<MailSend> getMailSendList() {
		return mailSendList;
	}
	public List<MailSend> getMailSendSelectedList() {
		return mailSendSelectedList;
	}
	public void setMailSendSelectedList(List<MailSend> mailSendSelectedList) {
		this.mailSendSelectedList = mailSendSelectedList;
	}
	public MailSend getMailSendSelected() {
		return mailSendSelected;
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
			Set<String> ret = new LinkedHashSet<>();
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