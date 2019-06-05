package com.jzaoralek.scb.ui.pages.courseapplication.vm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Listitem;

import com.jzaoralek.scb.dataservice.domain.Course;
import com.jzaoralek.scb.dataservice.domain.CourseLocation;
import com.jzaoralek.scb.dataservice.domain.CourseParticipant;
import com.jzaoralek.scb.dataservice.domain.ScbUser;
import com.jzaoralek.scb.dataservice.domain.ScbUserRole;
import com.jzaoralek.scb.dataservice.service.CourseService;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEvent;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.BaseContextVM;
import com.jzaoralek.scb.ui.pages.courseapplication.filter.CourseApplicationFilter;

public class MailRecipientSelectionVM extends BaseContextVM {

	@WireVariable
	private CourseService courseService;
	
	private List<ScbUser> userList;
	private List<ScbUser> userListBase;
	private final UserFilter userFilter = new UserFilter();
	private List<ScbUser> userListSelected;
	
	private List<Course> courseList;
	private List<Course> courseListBase;
	private List<CourseLocation> courseLocationList;
	private List<Course> courseListSelected;
	private boolean showCourseFilter;
	private CourseLocation courseLocationSelected;
	private final CourseApplicationFilter courseFilter = new CourseApplicationFilter();

	@Init
	public void init() {
		initYearContext();
	}
	
	@NotifyChange({"userList","userFilter"})
	@Command
	public void refreshUserListDataCmd() {
		loadUserListData();
		this.userFilter.setEmptyValues();
	}
	
	@NotifyChange({"courseList","courseFilter"})
	@Command
	public void refreshCourseListDataCmd() {
		this.courseLocationSelected = null;
		loadCourseListData();
		this.courseFilter.setEmptyValues();
	}
	
	@Command
	@NotifyChange("userList")
	public void filterDomCmd() {
		this.userList = userFilter.getUserListFiltered(this.userListBase);
	}
	
	@NotifyChange({"userListSelected","userFilter"})
	@Command
	public void submitCmd() {
		final List<String> emailList = new ArrayList<>();
		if (!CollectionUtils.isEmpty(this.userListSelected)) {
			this.userListSelected.forEach(i -> emailList.add(i.getContact().getEmail1()));
		}
		
		EventQueueHelper.publish(ScbEvent.ADD_TO_RECIPIENT_LIST_EVENT, emailList);
		this.userListSelected = null;
		this.userFilter.setEmptyValues();
	}
	
	@NotifyChange({"courseListSelected","courseFilter"})
	@Command
	public void submitCourseSelectionCmd() {
		final List<String> emailList = new ArrayList<>();
		if (!CollectionUtils.isEmpty(this.courseListSelected)) {
			this.courseListSelected.forEach(i -> emailList.addAll(getParticEmailAddressList(i.getParticipantList())));;
		}
		
		EventQueueHelper.publish(ScbEvent.ADD_TO_RECIPIENT_LIST_EVENT, emailList);
		this.courseListSelected = null;
		this.courseFilter.setEmptyValues();
	}
	
	private List<String> getParticEmailAddressList(List<CourseParticipant> courseParticList) {
		if (CollectionUtils.isEmpty(courseParticList)) {
			return Collections.emptyList();
		}
		final List<String> ret = new ArrayList<>();
		courseParticList.forEach(i -> ret.add(i.getContact().getEmail1()));
		
		return ret;
	}
	
	@Command
	public void usersTabSelectedCmd() {
		if (this.userList == null) {
			loadUserListData();
			BindUtils.postNotifyChange(null, null, this, "userList");
		}
	}
	
	@Command
	public void courseTabSelectedCmd() {
		if (this.courseList == null) {
			loadCourseListData();
		}
	}
	
	@Command
	@NotifyChange("courseList")
	public void filterCourseListCmd() {
		this.courseList = this.courseFilter.getApplicationListFiltered(this.courseListBase);
	}
	
	@NotifyChange("courseList")
	@Command
	public void filterByCourseLocationCmd() {
		this.courseList = WebUtils.filterByLocation(this.courseLocationSelected, this.courseListBase);
	}
	
	private void loadUserListData() {
		this.userList = scbUserService.getAll();
		this.userListBase = this.userList;
	}
	
	@Override
	protected void courseYearChangeCmdCore() {
		loadCourseListData();	
	}
	
	private void loadCourseListData() {
		String[] years = getYearsFromContext();
		
		int yearFrom = Integer.parseInt(years[0]);
		int yearTo = Integer.parseInt(years[1]);

		this.courseList = courseService.getAll(yearFrom, yearTo, false);
		this.courseListBase = this.courseList;
		
		this.courseLocationList = courseService.getCourseLocationAll();
		if (this.courseLocationList != null && this.courseLocationList.size() > 1) {
			// pokud vice nez jedno misto konani, zobrazit vyber mist konani
			this.showCourseFilter = true;
			this.courseLocationList.add(0, null);
			if (this.courseLocationSelected == null) {
				this.courseLocationSelected = this.courseLocationList.get(0);				
			} else {
				this.courseLocationSelected = courseLocationList.stream()
		                .filter(line -> this.courseLocationSelected.getUuid().toString().equals(line.getUuid().toString()))
		                .collect(Collectors.toList()).get(0);
			}
			this.courseList = WebUtils.filterByLocation(this.courseLocationSelected, this.courseListBase);
		}
		
		BindUtils.postNotifyChange(null, null, this, "courseList");
		BindUtils.postNotifyChange(null, null, this, "courseLocationList");
		BindUtils.postNotifyChange(null, null, this, "showCourseFilter");
		BindUtils.postNotifyChange(null, null, this, "courseLocationSelected");
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
	public UserFilter getUserFilter() {
		return userFilter;
	}
	public List<Course> getCourseList() {
		return courseList;
	}
	public void setCourseList(List<Course> courseList) {
		this.courseList = courseList;
	}
	public List<CourseLocation> getCourseLocationList() {
		return courseLocationList;
	}
	public boolean isShowCourseFilter() {
		return showCourseFilter;
	}
	public CourseApplicationFilter getCourseFilter() {
		return courseFilter;
	}
	public CourseLocation getCourseLocationSelected() {
		return courseLocationSelected;
	}
	public void setCourseLocationSelected(CourseLocation courseLocationSelected) {
		this.courseLocationSelected = courseLocationSelected;
	}
	public List<Course> getCourseListSelected() {
		return courseListSelected;
	}
	public void setCourseListSelected(List<Course> courseListSelected) {
		this.courseListSelected = courseListSelected;
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
			List<ScbUser> ret = new ArrayList<>();
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