package com.jzaoralek.scb.ui.pages.courseapplication.vm;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.jzaoralek.scb.dataservice.domain.CourseLocation;
import com.jzaoralek.scb.dataservice.domain.ScbUserRole;
import com.jzaoralek.scb.dataservice.exception.ScbValidationException;
import com.jzaoralek.scb.dataservice.service.CourseService;
import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.WebPages;
import com.jzaoralek.scb.ui.common.events.SzpEventListener;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEvent;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEventQueues;
import com.jzaoralek.scb.ui.common.utils.ExcelUtil;
import com.jzaoralek.scb.ui.common.utils.MessageBoxUtils;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.BaseContextVM;
import com.jzaoralek.scb.ui.pages.courseapplication.filter.CourseFilter;

public class CourseListVM extends BaseContextVM {

	private static final Logger LOG = LoggerFactory.getLogger(CourseListVM.class);

	private List<Course> courseList;
	private List<Course> courseListBase;
	private List<CourseLocation> courseLocationList;
	private boolean showCourseFilter;
	private CourseLocation courseLocationSelected;
	private final CourseFilter filter = new CourseFilter();

	@WireVariable
	private CourseService courseService;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Init
	public void init() {
		initYearContext();
		loadData();

		final EventQueue eq = EventQueues.lookup(ScbEventQueues.COURSE_APPLICATION_QUEUE.name() , EventQueues.DESKTOP, true);
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
		if (!isLoggedUserInRole(ScbUserRole.ADMIN.name())) {
			return;
		}
		
		if (uuid ==  null) {
			throw new IllegalArgumentException("uuid is null");
		}
		Executions.sendRedirect("/pages/secured/ADMIN/kurz.zul?"+WebConstants.UUID_PARAM+"="+uuid.toString() + "&" + WebConstants.FROM_PAGE_PARAM + "=" + WebPages.COURSE_LIST);
	}

	@Command
    public void courseLearningLessonsCmd(@BindingParam(WebConstants.UUID_PARAM) final UUID uuid) {
		if (uuid ==  null) {
			throw new IllegalArgumentException("uuid is null");
		}
		Executions.sendRedirect("/pages/secured/TRAINER/kurz-vyuka.zul?"+WebConstants.UUID_PARAM+"="+uuid.toString() + "&" + WebConstants.FROM_PAGE_PARAM + "=" + WebPages.COURSE_LIST);
	}

	@NotifyChange("*")
	@Command
	public void refreshDataCmd() {
		loadData();
		filter.setEmptyValues();
	}

	@NotifyChange("*")
	@Command
    public void deleteCmd(@BindingParam(WebConstants.ITEM_PARAM) final Course item) {
		if (item ==  null) {
			throw new IllegalArgumentException("Course");
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("Deleting course with uuid: " + item.getUuid());
		}
		final Object[] msgParams = new Object[] {item.getName()};
		final UUID uuid = item.getUuid();
		MessageBoxUtils.showDefaultConfirmDialog(
			"msg.ui.quest.deleteCourse",
			"msg.ui.title.deleteRecord",
			new SzpEventListener() {
				@Override
				public void onOkEvent() {
					try {
						courseService.delete(uuid);
						EventQueueHelper.publish(ScbEventQueues.COURSE_APPLICATION_QUEUE, ScbEvent.RELOAD_COURSE_DATA_EVENT, null, null);
						WebUtils.showNotificationInfo(Labels.getLabel("msg.ui.info.courseDeleted", msgParams));
					} catch (ScbValidationException e) {
						LOG.warn("ScbValidationException caught for course with uuid: " + uuid);
						WebUtils.showNotificationError(e.getMessage());
					}
				}
			},
			msgParams
		);
	}

	protected void courseYearChangeCmdCore() {
		loadData();
	}

	@Command
	@NotifyChange("courseList")
	public void filterDomCmd() {
		this.courseList = filter.getApplicationListFiltered(this.courseListBase);
	}

	@Command
	public void newItemCmd() {
		WebUtils.redirectToNewCourse();
	}
	
	@NotifyChange("courseList")
	@Command
	public void filterByCourseLocationCmd() {
		this.courseList = WebUtils.filterByLocation(this.courseLocationSelected, this.courseListBase);
	}

	public void loadData() {
		String[] years = getYearsFromContext();
		
		int yearFrom = Integer.parseInt(years[0]);
		int yearTo = Integer.parseInt(years[1]);

		this.courseList = courseService.getAll(yearFrom, yearTo, false);
		this.courseListBase = this.courseList;
		
		this.courseLocationList = courseService.getCourseLocationAll();
		if (this.courseLocationList != null && this.courseLocationList.size() > 1) {
			// pokud vice nez jedno misto konani, zobrazit vyber mist konani
			this.showCourseFilter = true;
			this.courseLocationSelected = this.courseLocationList.get(0);
			this.courseList = WebUtils.filterByLocation(this.courseLocationSelected, this.courseListBase);
		}
		
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
								item.getParticipantListCount()});
			}
		}

		return data;
	}

	public List<Course> getCourseList() {
		return courseList;
	}
	
	public List<CourseLocation> getCourseLocationList() {
		return courseLocationList;
	}

	public CourseFilter getFilter() {
		return filter;
	}
	
	public boolean isShowCourseFilter() {
		return showCourseFilter;
	}
	
	public CourseLocation getCourseLocationSelected() {
		return courseLocationSelected;
	}

	public void setCourseLocationSelected(CourseLocation courseLocationSelected) {
		this.courseLocationSelected = courseLocationSelected;
	}
}
