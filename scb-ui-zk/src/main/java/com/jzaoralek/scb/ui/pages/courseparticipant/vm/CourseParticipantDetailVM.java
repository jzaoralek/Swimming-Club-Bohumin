package com.jzaoralek.scb.ui.pages.courseparticipant.vm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.bind.annotation.QueryParam;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Listitem;

import com.jzaoralek.scb.dataservice.domain.CodeListItem;
import com.jzaoralek.scb.dataservice.domain.CodeListItem.CodeListType;
import com.jzaoralek.scb.dataservice.domain.Course;
import com.jzaoralek.scb.dataservice.domain.CourseApplication;
import com.jzaoralek.scb.dataservice.domain.CourseParticipant;
import com.jzaoralek.scb.dataservice.domain.Lesson;
import com.jzaoralek.scb.dataservice.domain.Lesson.DayOfWeek;
import com.jzaoralek.scb.dataservice.service.CodeListService;
import com.jzaoralek.scb.dataservice.service.CourseApplicationService;
import com.jzaoralek.scb.dataservice.service.CourseService;
import com.jzaoralek.scb.dataservice.service.impl.ConfigurationServiceImpl;
import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.converter.Converters;
import com.jzaoralek.scb.ui.common.utils.JasperUtil;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.Attachment;
import com.jzaoralek.scb.ui.common.vm.BaseVM;
import com.jzaoralek.scb.ui.pages.courseapplication.vm.CourseParticipantVM;

/**
 * Detail ucastnika zobrazeneho prihlasenym zakonnym zastupcem.
 *
 */
public class CourseParticipantDetailVM extends BaseVM {

	private static final Logger LOG = LoggerFactory.getLogger(CourseParticipantVM.class);

	@WireVariable
	private CourseService courseService;
	
	@WireVariable
	private CodeListService codeListService;
	
	@WireVariable
	private CourseApplicationService courseApplicationService;
	
	private CourseParticipant courseParticipant;
	private List<Listitem> swimStyleListitemList;
	private Listitem swimStyleListitemSelected;
	private List<Course> courseList;
	private List<String> courseYearList;
	private String courseYearSelected;
	private List<CourseApplication> courseApplicationList;

	@Init
	public void init(@QueryParam(WebConstants.UUID_PARAM) String uuid, @QueryParam(WebConstants.FROM_PAGE_PARAM) String fromPage) {
		this.courseYearList = configurationService.getCourseYearList();
		this.courseYearSelected = configurationService.getCourseApplicationYear();
		if (StringUtils.hasText(uuid)) {
			UUID courseParticipantUuid = UUID.fromString(uuid);
			this.courseParticipant = courseService.getCourseParticipantByUuid(courseParticipantUuid);
			loadCourseApplicationListData();
			loadCourseListData();
		}
		setReturnPage(fromPage);
		fillSwimStyleItemList();
	}
	
	@NotifyChange("courseList")
	@Command
	public void courseYearChangeCmd() {
		loadCourseListData();
	}
	
	public void loadCourseListData() {
		if (!StringUtils.hasText(this.courseYearSelected)) {
			return;
		}
		String[] years = this.courseYearSelected.split(ConfigurationServiceImpl.COURSE_YEAR_DELIMITER);
		if (years.length < 2) {
			return;
		}
		int yearFrom = Integer.parseInt(years[0]);
		int yearTo = Integer.parseInt(years[1]);

		List<Course> courseListAll = courseService.getByCourseParticipantUuid(this.courseParticipant.getUuid());
		this.courseList = new ArrayList<>();
		for (Course item : courseListAll) {
			if (item.getYearFrom() == yearFrom && item.getYearTo() == yearTo) {
				this.courseList.add(item);
			}
		}
	}

	public void loadCourseApplicationListData() {
		this.courseApplicationList = courseApplicationService.getByCourseParticipantUuid(this.courseParticipant.getUuid());
	}
	
	@NotifyChange("*")
	@Command
    public void submit() {
		try {
			// update
			if (LOG.isDebugEnabled()) {
				LOG.debug("Updating application: " + this.courseParticipant);
			}
			courseApplicationService.storeCourseParticipant(this.courseParticipant);
			WebUtils.showNotificationInfo(Labels.getLabel("msg.ui.info.changesSaved"));
		} catch (Exception e) {
			LOG.error("Unexpected exception caught for application: " + this.courseParticipant, e);
			throw new RuntimeException(e);
		}
    }
	
	@Command
	public void downloadCourseApplicationCmd(@BindingParam(WebConstants.COURSE_APPLICATION_PARAM) CourseApplication courseApplication) {
		byte[] byteArray = JasperUtil.getReport(courseApplication, Labels.getLabel("txt.ui.menu.applicationWithYear", new Object[] {courseApplication.getYearFrom()}));
		Attachment attachment = buildCourseApplicationAttachment(courseApplication, byteArray);
		WebUtils.downloadAttachment(attachment);
	}
	
	private void fillSwimStyleItemList() {
		List<CodeListItem> swimStyleList = codeListService.getItemListByType(CodeListType.SWIMMING_STYLE);
		this.swimStyleListitemList = new ArrayList<>();
		for (CodeListItem item : swimStyleList) {
			swimStyleListitemList.add(new Listitem(item.getName(), item.getUuid().toString()));
		}
		this.swimStyleListitemList.add(0, new Listitem(Labels.getLabel("txt.ui.common.all"), null));
		this.swimStyleListitemSelected = this.swimStyleListitemList.get(0);
	}
	
	/**
	 * Prevede lekci na format zobrazitelny v tabulce se spravnym formatem dnu a casu.
	 * @param lesson
	 * @return
	 */
	public String getLessonToUi(Lesson lesson) {
		StringBuilder sb = new StringBuilder();
		sb.append(Converters.getEnumlabelconverter().coerceToUi(lesson.getDayOfWeek(), null, null));
		sb.append(" ");
		sb.append(Converters.getTimeconverter().coerceToUi(lesson.getTimeFrom(), null, null));
		sb.append(" - ");
		sb.append(Converters.getTimeconverter().coerceToUi(lesson.getTimeTo(), null, null));
		return sb.toString();
	}
	
	public CourseParticipant getCourseParticipant() {
		return courseParticipant;
	}
	
	public List<Listitem> getSwimStyleListitemList() {
		return swimStyleListitemList;
	}

	public void setSwimStyleListitemList(List<Listitem> swimStyleListitemList) {
		this.swimStyleListitemList = swimStyleListitemList;
	}

	public Listitem getSwimStyleListitemSelected() {
		return swimStyleListitemSelected;
	}

	public void setSwimStyleListitemSelected(Listitem swimStyleListitemSelected) {
		this.swimStyleListitemSelected = swimStyleListitemSelected;
	}
	
	public List<Course> getCourseList() {
		return courseList;
	}
	
	public List<String> getCourseYearList() {
		return courseYearList;
	}

	public void setCourseYearList(List<String> courseYearList) {
		this.courseYearList = courseYearList;
	}

	public String getCourseYearSelected() {
		return courseYearSelected;
	}

	public void setCourseYearSelected(String courseYearSelected) {
		this.courseYearSelected = courseYearSelected;
	}
	
	public List<CourseApplication> getCourseApplicationList() {
		return courseApplicationList;
	}
}
