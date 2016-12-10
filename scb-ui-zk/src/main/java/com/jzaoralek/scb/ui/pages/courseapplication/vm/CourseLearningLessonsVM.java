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
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import com.jzaoralek.scb.dataservice.domain.Course;
import com.jzaoralek.scb.dataservice.domain.LearningLesson;
import com.jzaoralek.scb.dataservice.domain.LearningLessonStatsWrapper;
import com.jzaoralek.scb.dataservice.domain.Lesson;
import com.jzaoralek.scb.dataservice.service.CourseService;
import com.jzaoralek.scb.dataservice.service.LearningLessonService;
import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.WebPages;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEvent;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEventQueues;
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Init
	public void init(@QueryParam(WebConstants.UUID_PARAM) final String uuid
			, @QueryParam(WebConstants.FROM_PAGE_PARAM) String fromPage
			, @QueryParam(WebConstants.TAB_PARAM) String tabSelected) {
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

		if (StringUtils.isEmpty(tabSelected) || CourseLearnLessonTab.valueOf(tabSelected) == CourseLearnLessonTab.LESSONS) {
			// lekce
			this.tabSelected = CourseLearnLessonTab.LESSONS;
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.set(Calendar.DAY_OF_MONTH, 1);
			this.monthSelected = cal;
			buildLessonList(this.monthSelected);			
		} else if (CourseLearnLessonTab.valueOf(tabSelected) == CourseLearnLessonTab.ATTENDANCE) {
			// statistika dochazky
			this.tabSelected = CourseLearnLessonTab.ATTENDANCE;
			this.lessonStats = learningLessonService.buildCourseStatistics(this.course);			
		}
		
		final EventQueue eq = EventQueues.lookup(ScbEventQueues.LEARNING_LESSON_QUEUE.name() , EventQueues.DESKTOP, true);
		eq.subscribe(new EventListener<Event>() {
			@Override
			public void onEvent(Event event) {
				if (event.getName().equals(ScbEvent.RELOAD_LEARNIN_LESSON_LIST_DATA_EVENT.name())) {
					realoadLessonList((LearningLesson)event.getData());
				}
			}
		});
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
		 EventQueueHelper.publish(ScbEventQueues.LEARNING_LESSON_QUEUE, ScbEvent.LEARNIN_LESSON_DETAIL_DATA_EVENT, null, item);
		 WebUtils.openModal("/pages/secured/learning-lesson-window.zul");
	}
	
	@Command
	public void redirectToTab(@BindingParam(WebConstants.TAB_PARAM) String tab) {
		if (!StringUtils.hasText(tab)) {
			throw new IllegalArgumentException("tab is null");
		}
		
		CourseLearnLessonTab tabSelected = CourseLearnLessonTab.valueOf(tab);
		Executions.sendRedirect("/pages/secured/kurz-vyuka.zul?"+WebConstants.UUID_PARAM+"="+this.course.getUuid().toString() + "&" + WebConstants.FROM_PAGE_PARAM + "=" + WebPages.COURSE_LIST + "&" + WebConstants.TAB_PARAM + "=" + tabSelected);
	}

	/**
	 * Vygeneruje seznam lekci pro dany mesic
	 */
	private void buildLessonList(Calendar cal) {
		int monthSelected=cal.get(Calendar.MONTH);

		this.monthSelectedLabel = Labels.getLabel("txt.ui.month." + String.valueOf(monthSelected)) + " " + cal.get(Calendar.YEAR);
		BindUtils.postNotifyChange(null, null, this, "monthSelectedLabel");

		// prochazet dny v mesici, pokud den v tydnu odpovidajici dnu lekce zaradit do seznamu lekci
		List<Lesson> lessonInWeekList = this.course.getLessonList();
		// vsechny lekce pro dany kurz v mesici
		List<LearningLesson> lessonInMonthList = new ArrayList<LearningLesson>();
		while (monthSelected==cal.get(Calendar.MONTH)) {
			for (Lesson lessonInWeekItem : lessonInWeekList) {
			  if (cal.get(Calendar.DAY_OF_WEEK)-1 == (lessonInWeekItem.getDayOfWeek().getIndex()+1)) {
				  lessonInMonthList.add(new LearningLesson(lessonInWeekItem, cal.getTime()));
			  }
			}
		  cal.add(Calendar.DAY_OF_MONTH, 1);
		}

		//spojit vsechny lekce pro dany kurz v mesici a oducene lessons v mesici
		List<LearningLesson> learnLessonListFinal = new ArrayList<LearningLesson>();

		// oducene lekce pro dany kurz
		List<LearningLesson> learnedLearningLessonList = learningLessonService.getByCourse(this.course.getUuid());

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

		// nelze se posunout na cervenec a srpen
		this.prevMonthBtnDisabled = this.monthSelected.get(Calendar.MONTH) == Calendar.SEPTEMBER + 1;
		this.nextMonthBtnDisabled = this.monthSelected.get(Calendar.MONTH) == (Calendar.JUNE + 1);
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