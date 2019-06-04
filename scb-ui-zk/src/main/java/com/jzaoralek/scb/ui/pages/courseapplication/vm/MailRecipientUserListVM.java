package com.jzaoralek.scb.ui.pages.courseapplication.vm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zul.Listitem;

import com.jzaoralek.scb.dataservice.domain.ScbUser;
import com.jzaoralek.scb.dataservice.domain.ScbUserRole;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEvent;
import com.jzaoralek.scb.ui.common.vm.BaseVM;

public class MailRecipientUserListVM extends BaseVM {

	private List<ScbUser> userList;
	private List<ScbUser> userListBase;
	private final UserFilter filter = new UserFilter();
	private List<ScbUser> userListSelected;

	@Override
	@Init
	public void init() {
		loadData();
	}
	
	@NotifyChange({"userList","filter"})
	@Command
	public void refreshDataCmd() {
		loadData();
		this.filter.setEmptyValues();
	}
	
	@Command
	@NotifyChange("userList")
	public void filterDomCmd() {
		this.userList = filter.getUserListFiltered(this.userListBase);
	}
	
	@NotifyChange({"userListSelected","filter"})
	@Command
	public void submitCmd() {
		if (!CollectionUtils.isEmpty(this.userListSelected)) {
			final List<String> emailList = new ArrayList<>();
			this.userListSelected.forEach(i -> emailList.add(i.getContact().getEmail1()));
			EventQueueHelper.publish(ScbEvent.ADD_TO_RECIPIENT_LIST_EVENT, emailList);
		}
		
		this.userListSelected = null;
		this.filter.setEmptyValues();
	}
	
	private void loadData() {
		this.userList = scbUserService.getAll();
		this.userListBase = this.userList;
	}
	
	public List<ScbUser> getUserList() {
		return userList;
	}
	public List<ScbUser> getUserListSelected() {
		return userListSelected;
	}
	public void setUserListSelected(List<ScbUser> userListSelected) {
		this.userListSelected = userListSelected;
	}
	public UserFilter getFilter() {
		return filter;
	}
	
	public static class UserFilter {
		
		private String completeName;
		private String completeNameLc;
		private String email;
		private String emailLc;
		private Listitem roleItem;

		public boolean matches(String completeNameIn, String emailIn, ScbUserRole roleIn, boolean emptyMatch) {
			if (completeName == null &&  email == null && roleItem == null) {
				return emptyMatch;
			}
			if (completeName != null && !completeNameIn.toLowerCase().contains(completeNameLc)) {
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
				if (matches(item.getContact().getCompleteName()
						, item.getContact().getEmail1()
						, item.getRole()
						, true)) {
					ret.add(item);
				}
			}
			return ret;
		}

		public String getCompleteName() {
			return completeName == null ? "" : completeName;
		}

		public void setCompleteName(String name) {
			this.completeName = StringUtils.hasText(name) ? name.trim() : null;
			this.completeNameLc = this.completeName == null ? null : this.completeName.toLowerCase();
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
			completeName = null;
			email = null;
			emailLc = null;
			roleItem = null;
		}
	}
}