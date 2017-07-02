package com.jzaoralek.scb.ui.pages.courseapplication.vm;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;

import com.jzaoralek.scb.dataservice.domain.CourseApplication;
import com.jzaoralek.scb.dataservice.exception.ScbValidationException;
import com.jzaoralek.scb.dataservice.service.CourseApplicationService;
import com.jzaoralek.scb.dataservice.service.impl.ConfigurationServiceImpl;
import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.WebPages;
import com.jzaoralek.scb.ui.common.events.SzpEventListener;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEvent;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEventQueues;
import com.jzaoralek.scb.ui.common.utils.ExcelUtil;
import com.jzaoralek.scb.ui.common.utils.MessageBoxUtils;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.BaseVM;

public class CourseApplicationListVM extends BaseVM {

	private static final Logger LOG = LoggerFactory.getLogger(CourseApplicationListVM.class);

	public enum PageMode {
		COURSE_APPLICATION_LIST,
		CORSE_PARTICIPANT_LIST;
	}

	@WireVariable
	private CourseApplicationService courseApplicationService;

	private List<CourseApplication> courseApplicationList;
	private List<CourseApplication> courseApplicationListBase;
	private CourseApplicationFilter filter = new CourseApplicationFilter();
	private List<String> courseYearList;
	private String courseYearSelected;
	private PageMode pageMode;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Init
	public void init() {
		this.courseYearList = configurationService.getCourseYearList();
		this.courseYearSelected = configurationService.getCourseApplicationYear();

		setPageMode();
		loadData();

		final EventQueue eq = EventQueues.lookup(ScbEventQueues.COURSE_APPLICATION_QUEUE.name() , EventQueues.DESKTOP, true);
		eq.subscribe(new EventListener<Event>() {
			@Override
			public void onEvent(Event event) {
				if (event.getName().equals(ScbEvent.RELOAD_COURSE_APPLICATION_DATA_EVENT.name())) {
					loadData();
				}
			}
		});
	}

	private void setPageMode() {
		if (WebUtils.getCurrentUrl().contains(WebPages.APPLICATION_LIST.getUrl())) {
			this.pageMode = PageMode.COURSE_APPLICATION_LIST;
		} else if (WebUtils.getCurrentUrl().contains(WebPages.PARTICIPANT_LIST.getUrl())) {
			this.pageMode = PageMode.CORSE_PARTICIPANT_LIST;
		} else {
			throw new IllegalStateException("Unexpected page url: " + WebUtils.getCurrentUrl());
		}
	}

	@NotifyChange("*")
	@Command
    public void deleteCmd(@BindingParam(WebConstants.ITEM_PARAM) final CourseApplication item) {
		if (item ==  null) {
			throw new IllegalArgumentException("CourseApplication is null");
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("Deleting application with uuid: " + item.getUuid());
		}
		final Object[] msgParams = new Object[] {item.getCourseParticipant().getContact().getCompleteName()};
		final UUID uuid = item.getUuid();
		MessageBoxUtils.showDefaultConfirmDialog(
			"msg.ui.quest.deleteApplication",
			"msg.ui.title.deleteRecord",
			new SzpEventListener() {
				@Override
				public void onOkEvent() {
					try {
						courseApplicationService.delete(uuid);
						EventQueueHelper.publish(ScbEventQueues.COURSE_APPLICATION_QUEUE, ScbEvent.RELOAD_COURSE_APPLICATION_DATA_EVENT, null, null);
						WebUtils.showNotificationInfo(Labels.getLabel("msg.ui.info.applicationDeleted", msgParams));
					} catch (ScbValidationException e) {
						LOG.warn("ScbValidationException caught for application with uuid: " + uuid);
						WebUtils.showNotificationError(e.getMessage());
					}
				}
			},
			msgParams
		);
	}

	@Command
    public void detailCmd(@BindingParam(WebConstants.UUID_PARAM) final UUID uuid) {
		if (uuid ==  null) {
			throw new IllegalArgumentException("uuid is null");
		}
		String targetPage = (this.pageMode == PageMode.COURSE_APPLICATION_LIST) ? WebPages.APPLICATION_DETAIL.getUrl() : WebPages.PARTICIPANT_DETAIL.getUrl();
		WebPages fromPage = (this.pageMode == PageMode.COURSE_APPLICATION_LIST) ? WebPages.APPLICATION_LIST : WebPages.PARTICIPANT_LIST;
		Executions.sendRedirect(targetPage + "?"+WebConstants.UUID_PARAM+"="+uuid.toString() + "&" + WebConstants.FROM_PAGE_PARAM + "=" + fromPage);
	}

	@Command
	@NotifyChange("courseApplicationList")
	public void filterDomCmd() {
		this.courseApplicationList = filter.getApplicationListFiltered(this.courseApplicationListBase);
	}

	@NotifyChange("courseApplicationList")
	@Command
	public void exportToExcel(@BindingParam("listbox") Listbox listbox) {
		String filename = this.pageMode == PageMode.COURSE_APPLICATION_LIST ? "seznam_prihlasek.xls" : "seznam_ucastniku.xls";
		ExcelUtil.exportToExcel(filename, buildExcelRowData(listbox));
	}

	@NotifyChange("*")
	@Command
	public void refreshDataCmd() {
		loadData();
		filter.setEmptyValues();
	}

	@NotifyChange("*")
	@Command
	public void courseYearChangeCmd() {
		loadData();
	}

	@NotifyChange("*")
	@Command
	public void updatePayedCmd(@BindingParam(WebConstants.CHECKED_PARAM) final Boolean checked
			, @BindingParam(WebConstants.UUID_PARAM) final UUID uuid) {
		if (checked == null) {
			throw new IllegalArgumentException("checked is null");
		}
		if (uuid == null) {
			throw new IllegalArgumentException("uuid is null");
		}

		try {
			courseApplicationService.updatePayed(uuid, checked);
			EventQueueHelper.publish(ScbEventQueues.COURSE_APPLICATION_QUEUE, ScbEvent.RELOAD_COURSE_APPLICATION_DATA_EVENT, null, null);
			//WebUtils.showNotificationInfo(Labels.getLabel("msg.ui.info.applicationDeleted", msgParams));
		} catch (ScbValidationException e) {
			LOG.warn("ScbValidationException caught for application with uuid: " + uuid);
			WebUtils.showNotificationError(e.getMessage());
		}

	}

	private Map<String, Object[]> buildExcelRowData(@BindingParam("listbox") Listbox listbox) {
		Map<String, Object[]> data = new LinkedHashMap<String, Object[]>();

		DateFormat dateFormat = new SimpleDateFormat(WebConstants.WEB_DATETIME_PATTERN);

		// header
		Listhead lh = listbox.getListhead();
		Object[] headerArray = new Object[lh.getChildren().size() + 2];
		for (int i = 0; i < lh.getChildren().size(); i++) {
			headerArray[i] = ((Listheader) lh.getChildren().get(i)).getLabel();
		}
		
		if (this.pageMode == PageMode.COURSE_APPLICATION_LIST)  {
			headerArray[lh.getChildren().size()-1] = Labels.getLabel("txt.ui.common.phone") + " 2";
			headerArray[lh.getChildren().size()] = Labels.getLabel("txt.ui.common.email") + " 2";			
			headerArray[lh.getChildren().size()+1] = Labels.getLabel("txt.ui.common.residence");
		}
		data.put("0", headerArray);

		// rows
		ListModel<Object> model = listbox.getListModel();
		CourseApplication item = null;
		for (int i = 0; i < model.getSize(); i++) {
			if (model.getElementAt(i) instanceof CourseApplication) {
				item = (CourseApplication)model.getElementAt(i);
				if (this.pageMode == PageMode.COURSE_APPLICATION_LIST) {
					data.put(String.valueOf(i+1),
						new Object[] { item.getCourseParticipant().getContact().getCompleteName(),
								getDateConverter().coerceToUi(item.getCourseParticipant().getBirthdate(), null, null),
								item.getCourseParticipant().getPersonalNo(),
								item.getCourseParticRepresentative().getContact().getCompleteName(),
								item.getCourseParticRepresentative().getContact().getPhone1(),
								item.getCourseParticRepresentative().getContact().getEmail1(),
								dateFormat.format(item.getModifAt()),
								item.getCourseParticipant().getInCourseInfo(),
								!item.isCurrentParticipant() ? Labels.getLabel("txt.ui.common.yes") : Labels.getLabel("txt.ui.common.no"),
								item.getCourseParticRepresentative().getContact().getPhone2(),
								item.getCourseParticRepresentative().getContact().getEmail2(),
								item.getCourseParticipant().getContact().buildResidence()});
				} else {
					data.put(String.valueOf(i+1),
						new Object[] { item.getCourseParticipant().getContact().getCompleteName(),
//								getDateConverter().coerceToUi(item.getCourseParticipant().getBirthdate(), null, null),
//								item.getCourseParticRepresentative().getContact().getCompleteName(),
//								item.getCourseParticRepresentative().getContact().getPhone1(),
//								item.getCourseParticRepresentative().getContact().getEmail1(),
								item.getCourseParticipant().getInCourseInfo(),
								item.isPayed() ? Labels.getLabel("txt.ui.common.yes") : Labels.getLabel("txt.ui.common.no"),
//								item.getCourseParticipant().getContact().buildResidence()
								});
				}
			}
		}

		return data;
	}

	public void loadData() {
		if (!StringUtils.hasText(this.courseYearSelected)) {
			return;
		}
		String[] years = this.courseYearSelected.split(ConfigurationServiceImpl.COURSE_YEAR_DELIMITER);
		if (years.length < 2) {
			return;
		}
		int yearFrom = Integer.valueOf(years[0]);
		int yearTo = Integer.valueOf(years[1]);

		this.courseApplicationList = (this.pageMode == PageMode.COURSE_APPLICATION_LIST) ? courseApplicationService.getAll(yearFrom, yearTo) : courseApplicationService.getAssignedToCourse(yearFrom, yearTo);
		this.courseApplicationListBase = this.courseApplicationList;
		BindUtils.postNotifyChange(null, null, this, "courseApplicationList");
	}

	public List<CourseApplication> getCourseApplicationList() {
		return courseApplicationList;
	}

	public CourseApplicationFilter getFilter() {
		return filter;
	}

	public void setFilter(CourseApplicationFilter filter) {
		this.filter = filter;
	}

	public List<String> getCourseYearList() {
		return courseYearList;
	}

	public String getCourseYearSelected() {
		return courseYearSelected;
	}

	public void setCourseYearSelected(String courseYearSelected) {
		this.courseYearSelected = courseYearSelected;
	}

	public static class CourseApplicationFilter {
		private DateFormat dateFormat = new SimpleDateFormat(WebConstants.WEB_DATE_PATTERN);
		private DateFormat dateTimeFormat = new SimpleDateFormat(WebConstants.WEB_DATETIME_PATTERN);

		private String code;

		private String courseParticName;
		private String courseParticNameLc;
		private String birthDate;
		private String birthNo;
		private String courseParticRepresentative;
		private String courseParticRepresentativeLc;
		private String phone;
		private String email;
		private String emailLc;
		private String modifAt;
		private String course;
		private String courseLc;
		private Boolean inCourse;
		private Boolean payed;
		private Boolean newParticipant;

		public boolean matches(String courseParticNameIn, String birthDateIn, String birthNoIn, String courseParticRepresentativeIn, String phoneIn, String emailIn, String modifAtIn, String courseIn, boolean inCourseIn, boolean payedIn, boolean newParticipantIn, boolean emptyMatch) {
			if (courseParticName == null && birthDate == null && birthNo == null && courseParticRepresentative == null && phone == null && email == null && modifAt == null && course == null && inCourse == null && payed == null && newParticipant == null) {
				return emptyMatch;
			}
			if (courseParticName != null && !courseParticNameIn.toLowerCase().contains(courseParticNameLc)) {
				return false;
			}
			if (birthDate != null && !birthDateIn.contains(birthDate)) {
				return false;
			}
			if (birthNo != null && !birthNoIn.contains(birthNo)) {
				return false;
			}
			if (courseParticRepresentative != null && !courseParticRepresentativeIn.toLowerCase().contains(courseParticRepresentativeLc)) {
				return false;
			}
			if (phone != null && !phoneIn.contains(phone)) {
				return false;
			}
			if (email != null && !emailIn.toLowerCase().contains(emailLc)) {
				return false;
			}
			if (modifAt != null && !modifAtIn.contains(modifAt)) {
				return false;
			}
			if (course != null && !courseIn.toLowerCase().contains(courseLc)) {
				return false;
			}
			if (inCourse != null && (inCourse != inCourseIn)) {
				return false;
			}
			if (payed != null && (payed != payedIn)) {
				return false;
			}
			if (newParticipant != null && (newParticipant != newParticipantIn)) {
				return false;
			}
			
			return true;
		}

		public List<CourseApplication> getApplicationListFiltered(List<CourseApplication> codelistModelList) {
			if (codelistModelList == null || codelistModelList.isEmpty()) {
				return Collections.<CourseApplication>emptyList();
			}
			List<CourseApplication> ret = new ArrayList<CourseApplication>();
			for (CourseApplication item : codelistModelList) {
				if (matches(item.getCourseParticipant().getContact().getSurname() + " " + item.getCourseParticipant().getContact().getFirstname()
						, dateFormat.format(item.getCourseParticipant().getBirthdate())
						, item.getCourseParticipant().getPersonalNo()
						, item.getCourseParticRepresentative().getContact().getSurname() + " " + item.getCourseParticRepresentative().getContact().getFirstname()
						, item.getCourseParticRepresentative().getContact().getPhone1()
						, item.getCourseParticRepresentative().getContact().getEmail1()
						, dateTimeFormat.format(item.getModifAt())
						, item.getCourseParticipant().getInCourseInfo()
						, item.getCourseParticipant().inCourse()
						, item.isPayed()
						, !item.isCurrentParticipant()
						, true)) {
					ret.add(item);
				}
			}
			return ret;
		}

		public String getCode() {
			return code == null ? "" : code;
		}
		public void setCode(String code) {
			this.code = StringUtils.hasText(code) ? code.trim() : null;
		}
		public String getCourseParticName() {
			return courseParticName == null ? "" : courseParticName;
		}
		public void setCourseParticName(String name) {
			this.courseParticName = StringUtils.hasText(name) ? name.trim() : null;
			this.courseParticNameLc = this.courseParticName == null ? null : this.courseParticName.toLowerCase();
		}

		public String getCourseParticRepresentative() {
			return courseParticRepresentative == null ? "" : courseParticRepresentative;
		}

		public void setCourseParticRepresentative(String courseParticRepresentative) {
			this.courseParticRepresentative = StringUtils.hasText(courseParticRepresentative) ? courseParticRepresentative.trim() : null;
			this.courseParticRepresentativeLc = this.courseParticRepresentative == null ? null : this.courseParticRepresentative.toLowerCase();
		}

		public String getBirthDate() {
			return birthDate == null ? "" : birthDate;
		}

		public void setBirthDate(String birthDate) {
			this.birthDate = StringUtils.hasText(birthDate) ? birthDate.trim() : null;
		}

		public String getBirthNo() {
			return birthNo == null ? "" : birthNo;
		}

		public void setBirthNo(String birthNo) {
			this.birthNo = StringUtils.hasText(birthNo) ? birthNo.trim() : null;
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

		public String getModifAt() {
			return modifAt == null ? "" : modifAt;
		}

		public void setModifAt(String modifAt) {
			this.modifAt = StringUtils.hasText(modifAt) ? modifAt.trim() : null;
		}

		public String getCourse() {
			return course == null ? "" : course;
		}
		public void setCourse(String name) {
			this.course = StringUtils.hasText(name) ? name.trim() : null;
			this.courseLc = this.course == null ? null : this.course.toLowerCase();
		}
		public Boolean getInCourse() {
			return inCourse;
		}

		public void setInCourse(Boolean inCourse) {
			this.inCourse = inCourse;
		}

		public Boolean getPayed() {
			return payed;
		}

		public void setPayed(Boolean payed) {
			this.payed = payed;
		}
		
		public Boolean getNewParticipant() {
			return newParticipant;
		}

		public void setNewParticipant(Boolean newParticipant) {
			this.newParticipant = newParticipant;
		}

		public void setEmptyValues() {
			code = null;
			courseParticName = null;
			courseParticNameLc = null;
			birthDate = null;
			birthNo = null;
			courseParticRepresentative = null;
			phone = null;
			email = null;
			emailLc = null;
			modifAt = null;
			course = null;
			courseLc = null;
			inCourse = null;
			payed = null;
			newParticipant = null;
		}
	}
}
