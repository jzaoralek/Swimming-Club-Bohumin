package com.jzaoralek.scb.ui.pages.courseapplication.vm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.bind.annotation.QueryParam;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;

import com.jzaoralek.scb.dataservice.domain.Contact;
import com.jzaoralek.scb.dataservice.domain.CourseApplication;
import com.jzaoralek.scb.dataservice.domain.ScbUser;
import com.jzaoralek.scb.dataservice.domain.ScbUserRole;
import com.jzaoralek.scb.dataservice.exception.ScbValidationException;
import com.jzaoralek.scb.dataservice.utils.SecurityUtils;
import com.jzaoralek.scb.dataservice.utils.vo.Cover;
import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.WebPages;
import com.jzaoralek.scb.ui.common.converter.Converters;
import com.jzaoralek.scb.ui.common.events.SzpEventListener;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEvent;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEventQueues;
import com.jzaoralek.scb.ui.common.utils.ExcelUtil;
import com.jzaoralek.scb.ui.common.utils.MessageBoxUtils;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.BaseVM;

public class UserListVM extends BaseVM {

	private static final Logger LOG = LoggerFactory.getLogger(UserListVM.class);

	private List<ScbUser> userList;
	private List<ScbUser> userListBase;
	private UUID loggedUserUuid;
	private final UserFilter filter = new UserFilter();
	private boolean allowSendMailToUsers;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Init
	public void init(@QueryParam("allowSendMailToUsers") String allowSendMailToUsers) {
		loadData();
		this.loggedUserUuid = SecurityUtils.getLoggedUser().getUuid();
		this.allowSendMailToUsers = StringUtils.hasText(allowSendMailToUsers) && "1".equals(allowSendMailToUsers);
		final EventQueue eq = EventQueues.lookup(ScbEventQueues.USER_QUEUE.name() , EventQueues.DESKTOP, true);
		eq.subscribe(new EventListener<Event>() {
			@Override
			public void onEvent(Event event) {
				if (event.getName().equals(ScbEvent.RELOAD_USER_DATA_EVENT.name())) {
					loadData();
				}
			}
		});
	}

	@NotifyChange("*")
	@Command
	public void refreshDataCmd() {
		loadData();
		filter.setEmptyValues();
	}

	@Command
	public void exportToExcel(@BindingParam("listbox") Listbox listbox) {
		ExcelUtil.exportToExcel("seznam_uzivatelu.xls", buildExcelRowData(listbox));
	}

	@Command
    public void detailCmd(@BindingParam(WebConstants.UUID_PARAM) final UUID uuid) {
		if (uuid ==  null) {
			throw new IllegalArgumentException("uuid is null");
		}
		Executions.sendRedirect(WebPages.USER_DETAIL.getUrl()+"?"+WebConstants.UUID_PARAM+"="+uuid.toString() + "&" + WebConstants.FROM_PAGE_PARAM + "=" + WebPages.USER_LIST);
	}

	@Command
    public void deleteCmd(@BindingParam(WebConstants.ITEM_PARAM) final ScbUser item) {
		if (item ==  null) {
			throw new IllegalArgumentException("ScbUser is null");
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("Deleting user with uuid: " + item.getUuid());
		}
		final Object[] msgParams = new Object[] {item.getUsername()};
		final UUID uuid = item.getUuid();
		MessageBoxUtils.showDefaultConfirmDialog(
			"msg.ui.quest.deleteUser",
			"msg.ui.title.deleteRecord",
			new SzpEventListener() {
				@Override
				public void onOkEvent() {
					try {
						scbUserService.delete(uuid);
						EventQueueHelper.publish(ScbEventQueues.USER_QUEUE, ScbEvent.RELOAD_USER_DATA_EVENT, null, null);
						WebUtils.showNotificationInfo(Labels.getLabel("msg.ui.info.userDeleted", msgParams));
					} catch (ScbValidationException e) {
						LOG.warn("ScbValidationException caught for user with uuid: " + uuid);
						WebUtils.showNotificationError(e.getMessage());
					}
				}
			},
			msgParams
		);
	}

	@Command
	public void newItemCmd() {
		Executions.sendRedirect(WebPages.USER_DETAIL.getUrl()+"?" + WebConstants.FROM_PAGE_PARAM + "=" + WebPages.USER_LIST);
	}

	@Command
	public void resetPwdCmd(@BindingParam(WebConstants.UUID_PARAM) final UUID uuid) {
		if (uuid ==  null) {
			throw new IllegalArgumentException("uuid is null");
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("Renewining password for user with uuid: " + uuid);
		}

		ScbUser user = scbUserService.getByUuid(uuid);
		if (user == null) {
			WebUtils.showNotificationError(Labels.getLabel("msg.ui.warn.userNotExistsInDB"));
			return;
		}
		String newPassword = SecurityUtils.generatePassword();
		user.setPasswordGenerated(true);
		user.setPassword(newPassword);
		scbUserService.changePassword(user, new Cover<>(newPassword.toCharArray()));

		// send mail
		sendMailWithResetpassword(user);
		WebUtils.showNotificationInfo(Labels.getLabel("msg.ui.info.passwordResetAndSendToEmail", new Object[] {user.getUsername(), user.getContact().getEmail1()}));
	}

	@Command
	@NotifyChange("userList")
	public void filterDomCmd() {
		this.userList = filter.getUserListFiltered(this.userListBase);
	}
	
	@Command
	public void sendNewUserMailToAllUser() {
		if (CollectionUtils.isEmpty(this.userList)) {
			return;
		}
		
		for (ScbUser user : this.userList) {
			sendMailToNewUser(user);
		}
		
		WebUtils.showNotificationInfo("Obeslání uživatelů úspěšně dokončeno.");
	}
	
	/**
	 * Otevre stranku pro odeslani emailu na emailove adresy vybranych ucastniku.
	 */
	@Command
	public void goToSendEmailCmd() {
		goToSendEmailCore(buildContactSet(this.userList));
	}
	
	/**
	 * Sestavi seznam emailovych adres ucastniku.
	 * @param courseApplicationList
	 * @return
	 */
	private Set<Contact> buildContactSet(List<ScbUser> userList) {
		if (CollectionUtils.isEmpty(userList)) {
			return Collections.emptySet();
		}
		
		Set<Contact> ret = new HashSet<>();
		for (ScbUser item : userList) {
			if (item.getContact() != null && StringUtils.hasText(item.getContact().getEmail1())) {
				ret.add(item.getContact());				
			}
		}
		return ret;
	}

	public Boolean canDelete(UUID userUuid) {
		return !userUuid.toString().equals(this.loggedUserUuid.toString());
	}

	public void loadData() {
		this.userList = scbUserService.getAll();
		this.userListBase = this.userList;
		BindUtils.postNotifyChange(null, null, this, "userList");
	}

	private Map<String, Object[]> buildExcelRowData(@BindingParam("listbox") Listbox listbox) {
		Map<String, Object[]> data = new LinkedHashMap<String, Object[]>();

		// header
		Listhead lh = listbox.getListhead();
		Object[] headerArray = new Object[lh.getChildren().size()];
		for (int i = 0; i < lh.getChildren().size(); i++) {
			headerArray[i] = ((Listheader) lh.getChildren().get(i)).getLabel();
		}
		data.put("0", headerArray);

		// rows
		ListModel<Object> model = listbox.getListModel();
		ScbUser item = null;
		for (int i = 0; i < model.getSize(); i++) {
			if (model.getElementAt(i) instanceof ScbUser) {
				item = (ScbUser)model.getElementAt(i);
				data.put(String.valueOf(i+1),
						new Object[] { item.getUsername(),
								Converters.getEnumlabelconverter().coerceToUi(item.getRole(), null, null),
								item.getContact().getCompleteName(),
								item.getContact().getEmail1(),
								item.getContact().getPhone1()});
			}
		}

		return data;
	}

	public List<ScbUser> getUserList() {
		return userList;
	}

	public UserFilter getFilter() {
		return filter;
	}
	
	public boolean isAllowSendMailToUsers() {
		return allowSendMailToUsers;
	}

	public static class UserFilter {

		private String usernameName;
		private String usernameNameLc;
		private String completeName;
		private String completeNameLc;
		private String phone;
		private String email;
		private String emailLc;
		private Listitem roleItem;

		public boolean matches(String usernameNameIn, String completeNameIn, String phoneIn, String emailIn, ScbUserRole roleIn, boolean emptyMatch) {
			if (usernameName == null && completeName == null && phone == null && email == null && roleItem == null) {
				return emptyMatch;
			}
			if (usernameName != null && !usernameNameIn.toLowerCase().contains(usernameNameLc)) {
				return false;
			}
			if (completeName != null && !completeNameIn.toLowerCase().contains(completeNameLc)) {
				return false;
			}
			if (phone != null && !phoneIn.contains(phone)) {
				return false;
			}
			if (email != null && !emailIn.toLowerCase().contains(emailLc)) {
				return false;
			}
			if (roleItem != null && roleItem.getValue() != null && ((ScbUserRole)roleItem.getValue()) != roleIn) {
				return false;
			}
			return true;
		}

		public List<ScbUser> getUserListFiltered(List<ScbUser> codelistModelList) {
			if (codelistModelList == null || codelistModelList.isEmpty()) {
				return Collections.<ScbUser>emptyList();
			}
			List<ScbUser> ret = new ArrayList<ScbUser>();
			for (ScbUser item : codelistModelList) {
				if (matches(item.getUsername()
						, item.getContact().getCompleteName()
						, item.getContact().getPhone1()
						, item.getContact().getEmail1()
						, item.getRole()
						, true)) {
					ret.add(item);
				}
			}
			return ret;
		}

		public String getUsernameName() {
			return usernameName == null ? "" : usernameName;
		}

		public void setUsernameName(String name) {
			this.usernameName = StringUtils.hasText(name) ? name.trim() : null;
			this.usernameNameLc = this.usernameName == null ? null : this.usernameName.toLowerCase();
		}

		public String getCompleteName() {
			return completeName == null ? "" : completeName;
		}

		public void setCompleteName(String name) {
			this.completeName = StringUtils.hasText(name) ? name.trim() : null;
			this.completeNameLc = this.completeName == null ? null : this.completeName.toLowerCase();
		}

		public String getPhone() {
			return phone == null ? "" : phone;
		}

		public void setPhone(String phone) {
			this.phone = StringUtils.hasText(phone) ? phone.trim() : null;
		}

		public String getEmail() {
			return email == null ? "" : email;
		}

		public void setEmail(String email) {
			this.email = StringUtils.hasText(email) ? email.trim() : null;
			this.emailLc = this.email == null ? null : this.email.toLowerCase();
		}

		public Listitem getRoleItem() {
			return roleItem;
		}

		public void setRoleItem(Listitem roleItem) {
			this.roleItem = roleItem;
		}

		public void setEmptyValues() {
			usernameName = null;
			usernameNameLc = null;
			completeName = null;
			phone = null;
			email = null;
			emailLc = null;
			roleItem = null;
		}
	}
}