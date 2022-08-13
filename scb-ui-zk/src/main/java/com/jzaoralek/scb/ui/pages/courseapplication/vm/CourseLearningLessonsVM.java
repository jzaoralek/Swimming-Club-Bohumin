package com.jzaoralek.scb.ui.pages.courseapplication.vm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
import org.zkoss.zk.ui.select.annotation.WireVariable;

import com.jzaoralek.scb.dataservice.domain.Course;
import com.jzaoralek.scb.dataservice.domain.LearningLesson;
import com.jzaoralek.scb.dataservice.domain.LearningLessonStatsWrapper;
import com.jzaoralek.scb.dataservice.domain.Lesson;
import com.jzaoralek.scb.dataservice.service.CourseService;
import com.jzaoralek.scb.dataservice.service.LearningLessonService;
import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.WebPages;
import com.jzaoralek.scb.ui.common.template.SideMenuComposer.ScbMenuItem;
import com.jzaoralek.scb.ui.common.utils.DateUtil;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.BaseVM;

public class CourseLearningLessonsVM extends BaseVM {
	
	private enum CourseLearnLessonTab {
		LESSONS,
		ATTENDANCE;
	}

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	@WireVariable
	private CourseService courseService;

	@WireVariable
	private LearningLessonService learningLessonService;

	private Course course;
	private List<LearningLesson> learningLessonList;
	private Calendar monthSelected;
	private boolean prevMonthBtnDisabled;
	private boolean nextMonthBtnDisabled;
	private String monthSelectedLabel;
	private LearningLessonStatsWrapper lessonStats;
	private CourseLearnLessonTab tabSelected;

	@Init
	public void init(@QueryParam(WebConstants.UUID_PARAM) final String uuid
			, @QueryParam(WebConstants.FROM_PAGE_PARAM) String fromPage
			, @QueryParam(WebConstants.TAB_PARAM) String tabSelected) {
        setMenuSelected(ScbMenuItem.SEZNAM_KURZU);
		if (!StringUtils.hasText(uuid)) {
			throw new IllegalArgumentException("uuid is null");
		}
		Course course = courseService.getByUuid(UUID.fromString(uuid));
		if (course == null) {
			throw new IllegalStateException("Course doesn't exist for uuid:" + uuid);
		}

		this.course = course;
		this.pageHeadline = Labels.getLabel("txt.ui.heading.learningCourse", new Object[]{this.course.getName()});
		setReturnPage(fromPage);

		// lekce
		this.tabSelected = CourseLearnLessonTab.LESSONS;
		
		// ziskani vybraneho mesice z cache
		Calendar cal = (Calendar)WebUtils.getSessAtribute(WebConstants.COURSE_LEARN_LESSONS_MONTH_SELECTED_PARAM);
		if (cal == null) {
			cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.set(Calendar.DAY_OF_MONTH, 1);			
		}
		this.monthSelected = cal;
		
		buildLessonList(this.monthSelected);
	}

	public void realoadLessonList(LearningLesson lesson) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(lesson.getLessonDate());
		cal.set(Calendar.DAY_OF_MONTH, 1);
		this.monthSelected = cal;
		buildLessonList(this.monthSelected);

		BindUtils.postNotifyChange(null, null, this, "learningLessonList");
		BindUtils.postNotifyChange(null, null, this, "prevMonthBtnDisabled");
		BindUtils.postNotifyChange(null, null, this, "nextMonthBtnDisabled");
	}

	@NotifyChange("*")
	@Command
	public void previousMonthCmd() {
		this.monthSelected.add(Calendar.MONTH, -2);
		this.monthSelected.set(Calendar.DATE, 1);
		buildLessonList(this.monthSelected);
	}

	@NotifyChange("*")
	@Command
	public void nextMonthCmd() {
		this.monthSelected.add(Calendar.MONTH, 0);
		this.monthSelected.set(Calendar.DATE, 1);
		buildLessonList(this.monthSelected);
	}

	@Command
	public void lessonDetailCmd(@BindingParam(WebConstants.ITEM_PARAM) LearningLesson item) {
		if (item.isInFuture()) {
			return;
		}
		
		WebUtils.setSessAtribute(WebConstants.ITEM_PARAM, item);
		WebUtils.setSessAtribute(WebConstants.FROM_PAGE_URL, WebPages.COURSE_LESSONS.getUrl() + "?" + WebConstants.UUID_PARAM+"="+this.course.getUuid() + "&" + WebConstants.FROM_PAGE_PARAM + "=" + WebPages.COURSE_LIST);
		WebUtils.sendRedirect(WebPages.LEARNING_LESSON.getUrl());
	}

	
	@NotifyChange("lessonStats")
	@Command
	public void lessonAttendanceTabOnSelect() {
		this.lessonStats = learningLessonService.buildCourseStatistics(this.course.getUuid(), null);	
	}
	
	/**
	 * Ulozeni mesice do session, potreba vytvorit novou instanci, protoze puvodni je menena.
	 * @param cal
	 */
	private void storeSelectedMonthToSession(Calendar cal) {
		Calendar calCorrectMonth = Calendar.getInstance();
		calCorrectMonth.setTime(cal.getTime());
		WebUtils.setSessAtribute(WebConstants.COURSE_LEARN_LESSONS_MONTH_SELECTED_PARAM, calCorrectMonth);
	}
	
	/**
	 * Vygeneruje seznam lekci pro dany mesic
	 */
	private void buildLessonList(Calendar cal) {
		// ulozeni vybraneho mesice do session
		storeSelectedMonthToSession(cal);
				
		int monthSelected=cal.get(Calendar.MONTH);

		this.monthSelectedLabel = Labels.getLabel("txt.ui.month." + String.valueOf(monthSelected)) + " " + cal.get(Calendar.YEAR);
		BindUtils.postNotifyChange(null, null, this, "monthSelectedLabel");

		// prochazet dny v mesici, pokud den v tydnu odpovidajici dnu lekce zaradit do seznamu lekci
		List<Lesson> lessonInWeekList = this.course.getLessonList();
		
		// vsechny lekce pro dany kurz v mesici
		List<LearningLesson> lessonInMonthList = new ArrayList<>();
		while (monthSelected==cal.get(Calendar.MONTH)) {
			for (Lesson lessonInWeekItem : lessonInWeekList) {
			  if (cal.get(Calendar.DAY_OF_WEEK)-1 == (lessonInWeekItem.getDayOfWeek().getIndex()+1)) {
				  lessonInMonthList.add(new LearningLesson(lessonInWeekItem, cal.getTime()));
			  }
			}
		  cal.add(Calendar.DAY_OF_MONTH, 1);
		}
		
		//spojit vsechny lekce pro dany kurz v mesici a oducene lessons v mesici
		List<LearningLesson> learnLessonListFinal = new ArrayList<>();

		// oducene lekce pro dany kurz
//		List<LearningLesson> learnedLearningLessonList = learningLessonService.getByCourse(this.course.getUuid());
		Calendar calCorrectMonth = Calendar.getInstance();
		calCorrectMonth.setTime(cal.getTime());
		calCorrectMonth.add(Calendar.MONTH, -1);		
		List<LearningLesson> learnedLearningLessonList = learningLessonService.getByCourseInterval(this.course.getUuid(), DateUtil.getFirstDateOfCurrentMonth(calCorrectMonth), DateUtil.getLastDateOfCurrentMonth(calCorrectMonth));
		
		for (LearningLesson lessonItem : lessonInMonthList) {
			LearningLesson lesson = searchInLearnedLessonList(lessonItem, learnedLearningLessonList);
			if (lesson != null) {
				// lekce oducena
				learnLessonListFinal.add(lesson);
			} else {
				// lekce zatim nebyla oducena
				learnLessonListFinal.add(lessonItem);
			}
		}
		
		this.learningLessonList = learnLessonListFinal;

		// nelze se posunout na cervenec a srpen, zakomentovano
		this.prevMonthBtnDisabled = false; //this.monthSelected.get(Calendar.MONTH) == Calendar.SEPTEMBER + 1;
		this.nextMonthBtnDisabled = false; //this.monthSelected.get(Calendar.MONTH) == (Calendar.JUNE + 1);
	}

	private LearningLesson searchInLearnedLessonList(LearningLesson lesson, List<LearningLesson> learnedLessonList) {
		if (CollectionUtils.isEmpty(learnedLessonList)) {
			return null;
		}

		for (LearningLesson item : learnedLessonList) {
			if (item.getLesson().getUuid().toString().equals(lesson.getLesson().getUuid().toString())
					&& sdf.format(item.getLessonDate()).equals(sdf.format(lesson.getLessonDate()))) {
				return item;
			}
		}

		return null;
	}
	
	public CourseLearnLessonTab getTabSelected() {
		return tabSelected;
	}

	public Course getCourse() {
		return course;
	}

	public List<LearningLesson> getLearningLessonList() {
		return learningLessonList;
	}

	public boolean isPrevMonthBtnDisabled() {
		return prevMonthBtnDisabled;
	}

	public boolean isNextMonthBtnDisabled() {
		return nextMonthBtnDisabled;
	}

	public String getMonthSelectedLabel() {
		return monthSelectedLabel;
	}
	
	public LearningLessonStatsWrapper getLessonStats() {
		return lessonStats;
	}
}