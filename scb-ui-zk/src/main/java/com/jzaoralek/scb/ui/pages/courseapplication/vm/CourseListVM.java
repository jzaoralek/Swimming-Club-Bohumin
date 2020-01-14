package com.jzaoralek.scb.ui.pages.courseapplication.vm;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;

import com.jzaoralek.scb.dataservice.domain.Contact;
import com.jzaoralek.scb.dataservice.domain.Course;
import com.jzaoralek.scb.dataservice.domain.CourseLocation;
import com.jzaoralek.scb.dataservice.domain.CourseParticipant;
import com.jzaoralek.scb.dataservice.domain.Lesson;
import com.jzaoralek.scb.dataservice.domain.ScbUserRole;
import com.jzaoralek.scb.dataservice.domain.Course.CourseType;
import com.jzaoralek.scb.dataservice.exception.ScbValidationException;
import com.jzaoralek.scb.dataservice.utils.SecurityUtils;
import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.WebPages;
import com.jzaoralek.scb.ui.common.events.SzpEventListener;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEvent;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEventQueues;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper;
import com.jzaoralek.scb.ui.common.utils.ExcelUtil;
import com.jzaoralek.scb.ui.common.utils.MessageBoxUtils;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.pages.courseapplication.filter.CourseExternalFilter;
import com.jzaoralek.scb.ui.pages.courseapplication.filter.CourseFilter;

public class CourseListVM extends CourseAbstractVM {

	private static final Logger LOG = LoggerFactory.getLogger(CourseListVM.class);

	private List<Course> courseList;
	private List<Course> courseListBase;
	private List<CourseLocation> courseLocationList;
	private boolean showCourseFilter;
	private CourseLocation courseLocationSelected;
	private final CourseFilter filter = new CourseFilter();
	private Boolean myCourses;
	/** Cache object for external filter */
	private CourseExternalFilter externalFilter;
	private List<Course> selectedItems;
	private List<Course> courseCopyItems;
	private boolean multipleMode;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Init
	public void init() {
		initYearContext();
		// nacteni seznamu mist konani kurzu
		initCourseLocations();
		
		// default zobrazeni moje kurzy jen pokud se nejadna o admina
		this.myCourses = isLoggedUserAdmin() ? Boolean.FALSE : Boolean.TRUE;
		
		// nacteni externiho filtru ze session
		initExternalFilterCache();
		
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
	
	@AfterCompose
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
		Selectors.wireComponents(view, this, false);
	}
	
	
	private void updateExternalFilterCache()  {
		UUID courseLocSelUuid = null;
		if (this.showCourseFilter && this.courseLocationSelected != null) {
			courseLocSelUuid = this.courseLocationSelected.getUuid();
		}
		
		if (this.externalFilter == null) {
			this.externalFilter = new CourseExternalFilter(this.myCourses, courseLocSelUuid);
		} else {
			this.externalFilter.setMyCourses(this.myCourses);
			this.externalFilter.setCourseLocationUuid(courseLocSelUuid);
		}
		
		WebUtils.setSessAtribute(WebConstants.COURSE_LIST_EXT_FILTER_PARAM, this.externalFilter);
	}
	
	private void initExternalFilterCache() {
		this.externalFilter = (CourseExternalFilter)WebUtils.getSessAtribute(WebConstants.COURSE_LIST_EXT_FILTER_PARAM);
		if (this.externalFilter != null) {
			this.myCourses = this.externalFilter.getMyCourses();
			
			if (this.showCourseFilter) {
				List<CourseLocation> courseLocFilterred = this.courseLocationList.
							stream().
							filter(i -> i.getUuid().toString().equals(this.externalFilter.getCourseLocationUuid().toString())).
							collect(Collectors.toList());
				this.courseLocationSelected = courseLocFilterred.stream().findFirst().orElse(null);
			}
		}
	}

	@Command
	public void exportToExcel(@BindingParam("listbox") Listbox listbox) {
		ExcelUtil.exportToExcel("seznam_kurzu.xls", buildExcelRowData(listbox));
	}
	@Command
	public void exportCompleteCoursesToExcelCmd() {
		ExcelUtil.exportToExcel("seznam_kurzu_"+getCourseLocationName()+".xls", buildExcelCompleteCourseRowData());
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
		updateExternalFilterCache();
		loadData();
		filter.setEmptyValues();
	}

	@NotifyChange("*")
	@Command
	public void filterByMyCoursesCmd() {
		loadData();
		updateExternalFilterCache();
	}
	
	@Command
    public void deleteCmd(@BindingParam(WebConstants.ITEM_PARAM) final Course item) {
		if (item ==  null) {
			throw new IllegalArgumentException("Course");
		}
		
		// check user role
		if (!isLoggedUserInRole(ScbUserRole.ADMIN.name())) {
			return;
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("Deleting course with uuid: " + item.getUuid());
		}
		
		deleteCore(item, false);
	}
	
	/**
	 * Course list delete.
	 */
	@Command
	public void deleteListCmd() {
		if (CollectionUtils.isEmpty(this.selectedItems)) {
			return;
		}
		
		// check user role
		if (!isLoggedUserInRole(ScbUserRole.ADMIN.name())) {
			return;
		}

		final List<Course> itemsToDelete = this.selectedItems;
		
		MessageBoxUtils.showDefaultConfirmDialog(
			"msg.ui.quest.deleteCourseList",
			"msg.ui.title.deleteRecord",
			new SzpEventListener() {
				@Override
				public void onOkEvent() {
					try {
						for (Course item : itemsToDelete) {
							if (LOG.isDebugEnabled()) {
								LOG.debug("Deleting course with uuid: " + item.getUuid());
							}
							courseService.delete(item.getUuid());							
						}
						WebUtils.showNotificationInfo(Labels.getLabel("msg.ui.info.courseListDeleted"));
						EventQueueHelper.publish(ScbEventQueues.COURSE_APPLICATION_QUEUE, ScbEvent.RELOAD_COURSE_DATA_EVENT, null, null);						
					} catch (ScbValidationException e) {
						LOG.warn("ScbValidationException caught during deleting courses: " + itemsToDelete, e);
						WebUtils.showNotificationError(e.getMessage());
					}
				}
			}
		);
	}
	
	/**
	 * Create selected course copy items and open dialog window.
	 * @param uuid
	 * @param courseName
	 * @param component
	 */
	@NotifyChange({"courseCopy","copyCourseType","copyCourseName","copyCourseYear"})
	@Command
	public void buildCourseCopyItemsCmd(@BindingParam(WebConstants.COMPONENT_PARAM) Component component) {
		if (CollectionUtils.isEmpty(this.selectedItems)) {
			return;
		}
		
		// check user role
		if (!isLoggedUserInRole(ScbUserRole.ADMIN.name())) {
			return;
		}
		
		this.courseCopyItems = new ArrayList<>();
		
		this.copyCourseType = CourseType.STANDARD;
		this.copyCourseName = "TODO";
		this.copyCourseYear = configurationService.getCourseApplicationYear();
		
		for (Course item : this.selectedItems) {
			this.courseCopyItems.add(this.courseCopy = courseService.buildCopy(item.getUuid(), configurationService.getCourseApplicationYear(), true));			
		}
		
		courseCopyPopup.open(component);
	}
	
	/**
	 * Persist course copy items.
	 */
	@Command
	public void copyItemsCmd() {
		if (CollectionUtils.isEmpty(this.selectedItems)) {
			return;
		}
		// check user role
		if (!isLoggedUserInRole(ScbUserRole.ADMIN.name())) {
			return;
		}
		
				
		if (LOG.isDebugEnabled()) {
			LOG.debug("Copying courses: " + this.selectedItems);
		}
		
		Course courseNew = null;
		try {
			for (Course item : this.selectedItems) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("Copying course with uuid: " + item.getUuid());
				}
				// copying course
				courseNew = courseService.copy(item.getUuid(), this.courseCopy, this.copyParticipants, this.copyLessons, this.copyTrainers);
			}
			// redirect to course list
			//  TODO: redirect na seznam  kurzu v rocniku, do ktereho se kopirovalo
//			Executions.sendRedirect("/pages/secured/ADMIN/kurz.zul?"+WebConstants.UUID_PARAM+"="+courseNew.getUuid().toString() + "&" + WebConstants.FROM_PAGE_PARAM + "=" + WebPages.COURSE_LIST);
			WebUtils.showNotificationInfoAfterRedirect(Labels.getLabel("msg.ui.info.courseListCopied", new Object[] {courseNew.getYear()}));
		} catch (ScbValidationException e) {
			LOG.warn("ScbValidationException caught for course: " + courseNew, e);
			WebUtils.showNotificationError(e.getMessage());
		} catch (Exception e) {
			LOG.error("Unexpected exception caught for course: " + courseNew, e);
			throw new RuntimeException(e);
		}
	}

	protected void courseYearChangeCmdCore() {
		loadData();
	}

	@NotifyChange("multipleMode")
	@Command
	public void onSelectCmd() {
		this.multipleMode = this.selectedItems.size() > 1;
	}
	
	@Command
	@NotifyChange("courseList")
	public void filterDomCmd() {
		this.courseList = filter.getApplicationListFiltered(this.courseListBase);
	}
	
	@NotifyChange("courseList")
	@Command
	public void filterByCourseLocationCmd() {
//		this.courseList = WebUtils.filterByLocation(this.courseLocationSelected, this.courseListBase);
		loadData();
		updateExternalFilterCache();
	}
	
	/**
	 * Otevre stranku pro odeslani emailu na emailove adresy vybranych ucastniku.
	 */
	@Command
	public void goToSendEmailCmd() {
		if (CollectionUtils.isEmpty(this.courseList)) {
			return;
		}
		
		final Set<Contact> contactList = new HashSet<>();
		this.courseList.forEach(i -> contactList.addAll(WebUtils.getParticEmailAddressList(i, courseService, scbUserService)));
		
		goToSendEmailCore(contactList);
	}

	public void loadData() {
		String[] years = getYearsFromContext();
		
		int yearFrom = Integer.parseInt(years[0]);
		int yearTo = Integer.parseInt(years[1]);
		
		// nacteni vsech nebo pouze prirazenych kurzu
		this.courseList = this.myCourses ? courseService.getByTrainer(SecurityUtils.getLoggedUser().getUuid(), yearFrom, yearTo, false) : courseService.getAll(yearFrom, yearTo, false);
		this.courseListBase = this.courseList;
		
		if (this.showCourseFilter) {
			if (this.courseLocationSelected == null) {
				this.courseLocationSelected = this.courseLocationList.get(0);				
			}
			this.courseList = WebUtils.filterByLocation(this.courseLocationSelected, this.courseListBase);
		}

		BindUtils.postNotifyChange(null, null, this, "courseList");
	}
	
	private void initCourseLocations() {
		this.courseLocationList = courseService.getCourseLocationAll();
		// pokud vice nez jedno misto konani, zobrazit vyber mist konani
		this.showCourseFilter = (this.courseLocationList != null && this.courseLocationList.size() > 1);
	}

	private Map<String, Object[]> buildExcelRowData(@BindingParam("listbox") Listbox listbox) {
		Map<String, Object[]> data = new LinkedHashMap<>();

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
	
	/**
	 * Sestaveni dat pro detailni report o kurzech, vcetne ucastniku, lekci, treneru.
	 * @param listbox
	 * @return
	 */
	private Map<String, Object[]> buildExcelCompleteCourseRowData() {
		Map<String, Object[]> data = new LinkedHashMap<>();

		DateFormat dateFormat = new SimpleDateFormat(WebConstants.WEB_DATE_ISCUS_PATTERN);

		int MAX_COLS = 20;
		
		// header, kurzy, lokace,  pocet
		Object[] headerArray = getRow(Arrays.asList(Labels.getLabel("txt.ui.menu.courses"),
				getCourseLocationName(),
				this.getCourseYearSelected(),
				Labels.getLabel("txt.ui.common.count") + WebConstants.COLON + (!CollectionUtils.isEmpty(this.courseList) ? this.courseList.size() : 0)));
		data.put("0", headerArray);
		
		// empty  row
		data.put("1", getEmptyRow(MAX_COLS));
		
		// courses
		if (CollectionUtils.isEmpty(this.courseListBase)) {
			// no data found
			data.put("2", getRow(Arrays.asList(Labels.getLabel("txt.ui.common.noDataFound"))));
		} else {
			
			int row  = 3;
			Course courseDetail =  null;
			for (Course course : this.courseList) {
				courseDetail = courseService.getByUuid(course.getUuid());
				// course summary
				// Nazev kurzu | Lekce: {lekce}|Počet účastníků: {pocet}/{kapacita}
				data.put(String.valueOf(row++), 
						getRow(Arrays.asList(courseDetail.getName(), 
								getLekceStr(courseDetail.getLessonList()), 
								Labels.getLabel("txt.ui.common.Participants2") + WebConstants.COLON + courseDetail.getParticipantListCount() +"/"+course.getMaxParticipantCount())));
				// course participants
				if (!CollectionUtils.isEmpty(courseDetail.getParticipantList())) {
					for (CourseParticipant coursePartic : courseDetail.getParticipantList()) {
						data.put(String.valueOf(row++), 
								getRow(Arrays.asList(coursePartic.getContact().getCompleteName(),
										coursePartic.getBirthdate() != null ? dateFormat.format(coursePartic.getBirthdate()) : "",
												getNotNullStringEmptyChar(coursePartic.getContact().getStreet()),
												getNotNullLongEmptyChar(coursePartic.getContact().getLandRegistryNumber()),
												getNotNullStringEmptyChar(coursePartic.getContact().getHouseNumber()),
												getNotNullStringEmptyChar(coursePartic.getContact().getCity()),
												getNotNullStringEmptyChar(coursePartic.getContact().getZipCode()))));
					}
					data.put(String.valueOf(row++), getEmptyRow(MAX_COLS));
				}
			}			
		}

		return data;
	}
	
	private String getLekceStr(List<Lesson> lessonList) {
		if (CollectionUtils.isEmpty(lessonList)) {
			return null;
		}
		
		return lessonList.stream().map(i-> getLessonToUi(i)).collect(Collectors.joining(", "));	
	}
	
	private Object[] getEmptyRow(int cols) {
		Object[] ret = new Object[cols];
		
		for (int i = 0;i < cols; i++) {
			ret[i] = "";
		}
		
		return ret;
	}
	
	private Object[] getRow(List<Object> dataList) {
		Object[] ret = new Object[dataList.size()];
		
		for (int i = 0;i < dataList.size(); i++) {
			ret[i] = dataList.get(i);
		}
		
		return ret;
	}
	
	private String getCourseLocationName() {
		if (this.courseLocationSelected == null) {
			return "";
		}
		
		return this.courseLocationSelected.getName();
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
	public Boolean getMyCourses() {
		return myCourses;
	}
	public void setMyCourses(Boolean myCourses) {
		this.myCourses = myCourses;
	}
	public List<Course> getSelectedItems() {
		return selectedItems;
	}
	public void setSelectedItems(List<Course> selectedItems) {
		this.selectedItems = selectedItems;
	}
	public boolean isMultipleMode() {
		return multipleMode;
	}
}
