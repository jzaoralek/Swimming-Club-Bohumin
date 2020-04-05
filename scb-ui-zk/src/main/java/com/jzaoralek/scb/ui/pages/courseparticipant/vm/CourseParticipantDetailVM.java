package com.jzaoralek.scb.ui.pages.courseparticipant.vm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Listitem;

import com.jzaoralek.scb.dataservice.domain.Attachment;
import com.jzaoralek.scb.dataservice.domain.CodeListItem;
import com.jzaoralek.scb.dataservice.domain.CodeListItem.CodeListType;
import com.jzaoralek.scb.dataservice.domain.Course;
import com.jzaoralek.scb.dataservice.domain.CourseApplication;
import com.jzaoralek.scb.dataservice.domain.CourseCourseParticipantVO;
import com.jzaoralek.scb.dataservice.domain.CourseParticipant;
import com.jzaoralek.scb.dataservice.domain.LearningLessonStatsWrapper;
import com.jzaoralek.scb.dataservice.service.CodeListService;
import com.jzaoralek.scb.dataservice.service.CourseApplicationService;
import com.jzaoralek.scb.dataservice.service.CourseService;
import com.jzaoralek.scb.dataservice.service.LearningLessonService;
import com.jzaoralek.scb.dataservice.utils.PaymentUtils;
import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.component.address.AddressUtils;
import com.jzaoralek.scb.ui.common.template.SideMenuComposer.ScbMenuItem;
import com.jzaoralek.scb.ui.common.utils.JasperUtil;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.BaseContextVM;

/**
 * Detail ucastnika zobrazeneho prihlasenym zakonnym zastupcem.
 *
 */
public class CourseParticipantDetailVM extends BaseContextVM {

	private static final Logger LOG = LoggerFactory.getLogger(CourseParticipantDetailVM.class);

	@WireVariable
	private CourseService courseService;
	
	@WireVariable
	private CodeListService codeListService;
	
	@WireVariable
	private CourseApplicationService courseApplicationService;
	
	@WireVariable
	private LearningLessonService learningLessonService;
	
	private CourseParticipant courseParticipant;
	private List<Listitem> swimStyleListitemList;
	private Listitem swimStyleListitemSelected;
	private LearningLessonStatsWrapper lessonStats;
	private List<Course> courseList;
	private List<CourseApplication> courseApplicationList;
	private Course courseSelected;
	private boolean showLessonStats;
	private String orgAccountNo;
	private String paymentVarSymbolFirstSemester;
	private String paymentVarSymbolSecondSemester;
	private int yearFrom;
	private boolean attendanceForParentsVisible;
	private boolean courseApplNotVerifiedAddressAllowed;

	@Init
	public void init(@QueryParam(WebConstants.UUID_PARAM) String uuid, @QueryParam(WebConstants.FROM_PAGE_PARAM) String fromPage) {
        setMenuSelected(ScbMenuItem.SEZNAM_UCASTNIKU_U);
        initYearContext();
		if (StringUtils.hasText(uuid)) {
			UUID courseParticipantUuid = UUID.fromString(uuid);
			this.courseParticipant = courseService.getCourseParticipantByUuid(courseParticipantUuid);
			loadCourseApplicationListData();
			loadCourseListData();
		}
		setReturnPage(fromPage);
		fillSwimStyleItemList();
		this.orgAccountNo = configurationService.getBankAccountNumber();
		this.paymentVarSymbolFirstSemester = PaymentUtils.buildCoursePaymentVarsymbol(this.yearFrom, 1, this.courseParticipant.getVarsymbolCore());
		this.paymentVarSymbolSecondSemester = PaymentUtils.buildCoursePaymentVarsymbol(this.yearFrom, 2, this.courseParticipant.getVarsymbolCore());
		this.attendanceForParentsVisible = configurationService.isAttendanceForParentsVisible();
		this.courseApplNotVerifiedAddressAllowed = configurationService.isCourseApplNotVerifiedAddressAllowed();
	}
	
	protected void courseYearChangeCmdCore() {
		loadCourseListData();
	}
	
	public void loadCourseListData() {
		String[] years = getYearsFromContext();
		
		int yearFrom = Integer.parseInt(years[0]);
		int yearTo = Integer.parseInt(years[1]);		
		this.yearFrom = yearFrom;

		List<Course> courseListAll = courseService.getByCourseParticipantUuid(this.courseParticipant.getUuid(), yearFrom, yearTo);
		this.courseList = new ArrayList<>();
		for (Course item : courseListAll) {
			if (item.getYearFrom() == yearFrom && item.getYearTo() == yearTo) {
				// dotazeni treneru
				item.setTrainerList(courseService.getTrainersByCourse(item.getUuid()));
				this.courseList.add(item);
			}
		}
		
		CourseCourseParticipantVO courseCourseParticipantVO = null;
		for (Course item : this.courseList) {
			courseCourseParticipantVO = courseService.getCourseCourseParticipantVO(this.courseParticipant.getUuid(), item.getUuid(), false);
			if (courseCourseParticipantVO != null) {
				item.setCourseCourseParticipantVO(courseCourseParticipantVO);
			}
		}
	}
	
	public String getTrainersToUi(Course course) {
		if (course == null || CollectionUtils.isEmpty(course.getTrainerList())) {
			return "";
		}
		List<String> names = course.getTrainerList().stream().map(i -> i.getContact().getCompleteName()).collect(Collectors.toList());
		if (CollectionUtils.isEmpty(names)) {
			return "";
		}
		return names.stream().collect(Collectors.joining(","));
	}

	public void loadCourseApplicationListData() {
		this.courseApplicationList = courseApplicationService.getByCourseParticipantUuid(this.courseParticipant.getUuid());
	}
	
	@NotifyChange("*")
	@Command
    public void submit() {
		if (!AddressUtils.isAddressValid()) {
			return;
		}
		// overeni validni adresy pred  submitem
		addressValidationBeforeSubmit(this.courseParticipant.getContact(), this.courseApplNotVerifiedAddressAllowed, this::submitCore);
    }
	
	public void submitCore() {
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
		} finally {
			AddressUtils.setAddressInvalid();
		}
    }
	
	@Command
	public void downloadCourseApplicationCmd(@BindingParam(WebConstants.COURSE_APPLICATION_PARAM) CourseApplication courseApplication) {
		byte[] byteArray = JasperUtil.getReport(courseApplication, Labels.getLabel("txt.ui.menu.applicationWithYear", new Object[] {courseApplication.getYearFrom()}), configurationService);
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
	
	@NotifyChange({"lessonStats","showLessonStats"})
	@Command
	public void showAttendanceCmd(@BindingParam("course") Course course) {
		this.lessonStats = learningLessonService.buildCourseStatistics(course.getUuid(), this.courseParticipant.getUuid());
		this.showLessonStats = true;
	}
	
	@NotifyChange("courseList")
	@Command
	public void cancelParticipancyCmd(@BindingParam("course") Course course) {
		courseApplicationService.updateCourseParticInterruption(Arrays.asList(course.getCourseCourseParticipantVO().getUuid()), Calendar.getInstance().getTime());
		loadCourseListData();
		WebUtils.showNotificationInfo(Labels.getLabel("msg.ui.info.courseParticipationCanceled"));
	}
	
	@NotifyChange("courseList")
	@Command
	public void renewParticipancyCmd(@BindingParam("course") Course course) {
		// potreba zkontrolovat zda-li uz neni zaplnen
		Course selectedCourseDb = courseService.getByUuid(course.getUuid());
		if (selectedCourseDb.isFullOccupancy()) {
			WebUtils.showNotificationWarning(Labels.getLabel("msg.ui.warn.courseIsFull", new Object[] {course.getName()}));
			return;
		}
		
		courseApplicationService.updateCourseParticInterruption(Arrays.asList(course.getCourseCourseParticipantVO().getUuid()), null);
		loadCourseListData();
		WebUtils.showNotificationInfo(Labels.getLabel("msg.ui.info.courseParticipationRenewed"));
	}
	
	/**
	 * Nastavi datum narozeni podle rodneho cisla.
	 * @param personalNumber
	 * @param fx
	 */
	@Command
	public void birtNumberOnChangeCmd(@BindingParam("personal_number") String personalNumber, @BindingParam("fx") CourseParticipantDetailVM fx) {
		// predvyplneni datumu narozeni podle rodneho cisla
		boolean success = WebUtils.setBirthdateByBirthNumer(personalNumber, fx.getCourseParticipant(), configurationService);
		if (success) {
			BindUtils.postNotifyChange(null, null, this, "courseParticipant");			
		}
	}
	
	public boolean isAttendanceForParentsVisible() {
		return this.attendanceForParentsVisible;
	}
	
	public boolean cancelParticipancyAllowed(Course course) {
		if (course == null || course.getCourseCourseParticipantVO() == null) {
			return false;
		}
		
		return course.getCourseCourseParticipantVO().getCourseParticipationInterruptedAt() == null;
	}
	
	public String getRowColorStyleByParticipancy(Course course) {
		if (course == null || course.getCourseCourseParticipantVO() == null) {
			return "";
		}
		
		return course.getCourseCourseParticipantVO().getCourseParticipationInterruptedAt() == null ? "background: #DCFEDD;" : "background: #F0F0F0";
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
	
	public List<CourseApplication> getCourseApplicationList() {
		return courseApplicationList;
	}
	
	public Course getCourseSelected() {
		return courseSelected;
	}

	public void setCourseSelected(Course courseSelected) {
		this.courseSelected = courseSelected;
	}
	
	public LearningLessonStatsWrapper getLessonStats() {
		return lessonStats;
	}
	
	public boolean isShowLessonStats() {
		return showLessonStats;
	}
	
	public String getOrgAccountNo() {
		return orgAccountNo;
	}
	
	public String getPaymentVarSymbolFirstSemester() {
		return paymentVarSymbolFirstSemester;
	}

	public String getPaymentVarSymbolSecondSemester() {
		return paymentVarSymbolSecondSemester;
	}

}
