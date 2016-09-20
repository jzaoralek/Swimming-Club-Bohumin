package com.jzaoralek.scb.ui.pages.courseapplication.vm;

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

import com.jzaoralek.scb.dataservice.domain.Course;
import com.jzaoralek.scb.dataservice.exception.ScbValidationException;
import com.jzaoralek.scb.dataservice.service.CourseService;
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

public class CourseListVM {

	private static final Logger LOG = LoggerFactory.getLogger(CourseListVM.class);

	private List<Course> courseList;
	private List<Course> courseListBase;
	private final CourseApplicationFilter filter = new CourseApplicationFilter();

	@WireVariable
	private CourseService courseService;

	@Init
	public void init() {
		loadData();

		final EventQueue eq = EventQueues.lookup(ScbEventQueues.SDAT_COURSE_APPLICATION_QUEUE.name() , EventQueues.DESKTOP, true);
		eq.subscribe(new EventListener<Event>() {
			@Override
			public void onEvent(Event event) {
				if (event.getName().equals(ScbEvent.RELOAD_COURSE_DATA_EVENT.name())) {
					loadData();
					eq.unsubscribe(this);
				}
			}
		});
	}

	@Command
	public void exportToExcel(@BindingParam("listbox") Listbox listbox) {
		ExcelUtil.exportToExcel("seznam_kurzu.xls", buildExcelRowData(listbox));
	}

	@Command
    public void detailCmd(@BindingParam(WebConstants.UUID_PARAM) final UUID uuid) {
		if (uuid ==  null) {
			throw new IllegalArgumentException("uuid is null");
		}
		Executions.sendRedirect("/pages/secured/kurz.zul?"+WebConstants.UUID_PARAM+"="+uuid.toString() + "&" + WebConstants.FROM_PAGE_PARAM + "=" + WebPages.COURSE_LIST);
	}

	@NotifyChange("*")
	@Command
	public void refreshDataCmd() {
		loadData();
		filter.setEmptyValues();
	}

	@NotifyChange("*")
	@Command
    public void deleteCmd(@BindingParam(WebConstants.UUID_PARAM) final UUID uuid) {
		if (uuid ==  null) {
			throw new IllegalArgumentException("uuid is null");
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("Deleting course with uuid: " + uuid);
		}
		MessageBoxUtils.showDefaultConfirmDialog(
			"msg.ui.quest.deleteCourse",
			"msg.ui.title.deleteRecord",
			new SzpEventListener() {
				@Override
				public void onOkEvent() {
					try {
						courseService.delete(uuid);
						EventQueueHelper.publish(ScbEventQueues.SDAT_COURSE_APPLICATION_QUEUE, ScbEvent.RELOAD_COURSE_DATA_EVENT, null, null);
						WebUtils.showNotificationInfo(Labels.getLabel("msg.ui.info.courseDeleted"));
					} catch (ScbValidationException e) {
						LOG.warn("ScbValidationException caught for course with uuid: " + uuid);
						WebUtils.showNotificationError(e.getMessage());
					}
				}
			}
		);
	}

	@Command
	@NotifyChange("courseList")
	public void filterDomCmd() {
		this.courseList = filter.getApplicationListFiltered(this.courseListBase);
	}

	@Command
	public void newItemCmd() {
		Executions.sendRedirect("/pages/secured/kurz.zul?" + WebConstants.FROM_PAGE_PARAM + "=" + WebPages.COURSE_LIST);
	}

	public void loadData() {
		this.courseList = courseService.getAll();
		this.courseListBase = this.courseList;
		BindUtils.postNotifyChange(null, null, this, "courseList");
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
		Course item = null;
		for (int i = 0; i < model.getSize(); i++) {
			if (model.getElementAt(i) instanceof Course) {
				item = (Course)model.getElementAt(i);
				data.put(String.valueOf(i+1),
						new Object[] { item.getName(),
								item.getDescription(),
								item.getYearFrom() + ConfigurationServiceImpl.COURSE_YEAR_DELIMITER + item.getYearTo(),
								item.getParticipantListCount(),
								item.getLessonListCount()});
			}
		}

		return data;
	}

	public List<Course> getCourseList() {
		return courseList;
	}

	public CourseApplicationFilter getFilter() {
		return filter;
	}

	public static class CourseApplicationFilter {

		private String courseName;
		private String courseNameLc;

		public boolean matches(String courseNameIn, boolean emptyMatch) {
			if (courseName == null) {
				return emptyMatch;
			}
			if (courseName != null && !courseNameIn.toLowerCase().contains(courseNameLc)) {
				return false;
			}
			return true;
		}

		public List<Course> getApplicationListFiltered(List<Course> courseList) {
			if (courseList == null || courseList.isEmpty()) {
				return Collections.<Course>emptyList();
			}
			List<Course> ret = new ArrayList<Course>();
			for (Course item : courseList) {
				if (matches(item.getName(), true)) {
					ret.add(item);
				}
			}
			return ret;
		}

		public String getCourseName() {
			return courseName == null ? "" : courseName;
		}
		public void setCourseName(String name) {
			this.courseName = StringUtils.hasText(name) ? name.trim() : null;
			this.courseNameLc = this.courseName == null ? null : this.courseName.toLowerCase();
		}

		public void setEmptyValues() {
			courseName = null;
			courseNameLc = null;
		}
	}
}
