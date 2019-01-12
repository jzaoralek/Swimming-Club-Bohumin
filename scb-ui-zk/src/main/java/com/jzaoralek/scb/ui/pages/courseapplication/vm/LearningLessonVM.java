package com.jzaoralek.scb.ui.pages.courseapplication.vm;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import com.jzaoralek.scb.dataservice.domain.CourseParticipant;
import com.jzaoralek.scb.dataservice.domain.LearningLesson;
import com.jzaoralek.scb.dataservice.service.CourseService;
import com.jzaoralek.scb.dataservice.service.LearningLessonService;
import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.converter.Converters;
import com.jzaoralek.scb.ui.common.template.SideMenuComposer.ScbMenuItem;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.BaseVM;

public class LearningLessonVM extends BaseVM {

	@WireVariable
	private CourseService courseService;

	@WireVariable
	private LearningLessonService learningLessonService;

	private LearningLesson learningLesson;
	private List<CourseParticipant> allCourseParticList;
	private List<CourseParticipant> lessonParticipantList;

	@Override
	@Init
	public void init() {
        setMenuSelected(ScbMenuItem.SEZNAM_KURZU);
        this.returnToUrl = (String)WebUtils.getSessAtribute(WebConstants.FROM_PAGE_URL);
        
        if (this.learningLesson == null) {
        	LearningLesson item = (LearningLesson)WebUtils.getSessAtribute(WebConstants.ITEM_PARAM);
        	if (item != null) {
        		initData(item);
        		WebUtils.removeSessAtribute(WebConstants.ITEM_PARAM);
        	}        	
        }
	}

	@Command
	public void submitCmd() {
		this.learningLesson.setParticipantList(this.lessonParticipantList);
		learningLessonService.store(this.learningLesson);
		Executions.sendRedirect(this.returnToUrl);
	}

	private void initData(LearningLesson learningLesson) {
		if (learningLesson == null) {
			return;
		}

		this.learningLesson = learningLesson.getUuid() != null ? learningLessonService.getByUUID(learningLesson.getUuid()) : learningLesson;
		this.allCourseParticList = courseService.getByCourseParticListByCourseUuid(this.learningLesson.getLesson().getCourseUuid(), false);

		// selected vybrat zvsech ucastniku
		if (!CollectionUtils.isEmpty(this.learningLesson.getParticipantList())) {
			List<CourseParticipant> courseParticSelected = new ArrayList<CourseParticipant>();
			for  (CourseParticipant courseParticipant : this.allCourseParticList) {
				for (CourseParticipant lessonParticipant : this.learningLesson.getParticipantList()) {
					if (courseParticipant.getUuid().toString().equals(lessonParticipant.getUuid().toString())) {
						courseParticSelected.add(courseParticipant);
					}
				}
			}
			this.lessonParticipantList = courseParticSelected;
		}

		this.pageHeadline = buildPageHeadline(learningLesson);

		BindUtils.postNotifyChange(null, null, this, "pageHeadline");
		BindUtils.postNotifyChange(null, null, this, "learningLesson");
		BindUtils.postNotifyChange(null, null, this, "allCourseParticList");
		BindUtils.postNotifyChange(null, null, this, "lessonParticipantList");
	}

	private String buildPageHeadline(LearningLesson learningLesson) {
		return Labels.getLabel("txt.ui.common.lessons") + " - "
			+ Converters.getEnumlabelconverter().coerceToUi(learningLesson.getLesson().getDayOfWeek(), null, null) + ", "
			+ Converters.getDateconverter().coerceToUi(learningLesson.getLessonDate(), null, null) + ", "
			+ Converters.getTimeconverter().coerceToUi(learningLesson.getTimeFrom(), null, null) + "-"
			+ Converters.getTimeconverter().coerceToUi(learningLesson.getTimeTo(), null, null);
	}

	public LearningLesson getLearningLesson() {
		return learningLesson;
	}

	public void setLearningLesson(LearningLesson learningLesson) {
		this.learningLesson = learningLesson;
	}

	public List<CourseParticipant> getLessonParticipantList() {
		return lessonParticipantList;
	}

	public void setLessonParticipantList(List<CourseParticipant> lessonParticipantList) {
		this.lessonParticipantList = lessonParticipantList;
	}

	public List<CourseParticipant> getAllCourseParticList() {
		return allCourseParticList;
	}

	public void setAllCourseParticList(List<CourseParticipant> allCourseParticList) {
		this.allCourseParticList = allCourseParticList;
	}
}