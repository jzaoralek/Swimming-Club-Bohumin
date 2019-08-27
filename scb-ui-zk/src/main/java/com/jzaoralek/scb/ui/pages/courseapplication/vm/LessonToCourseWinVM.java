package com.jzaoralek.scb.ui.pages.courseapplication.vm;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.jzaoralek.scb.dataservice.domain.Lesson;
import com.jzaoralek.scb.dataservice.domain.Lesson.DayOfWeek;
import com.jzaoralek.scb.dataservice.exception.ScbValidationException;
import com.jzaoralek.scb.dataservice.service.LessonService;
import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEvent;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEventQueues;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.BaseVM;

public class LessonToCourseWinVM extends BaseVM {

	private static final Logger LOG = LoggerFactory.getLogger(LessonToCourseWinVM.class);

	private final List<Listitem> dayOfWeekList = WebUtils.getMessageItemsFromEnum(EnumSet.allOf(Lesson.DayOfWeek.class));
	
	private final List<String> dayTimeList = Arrays.asList("6:00","6:15","6:30","6:45",
															"7:00","7:15","7:30","7:45",
															"8:00","8:15","8:30","8:45",
															"9:00","9:15","9:30","9:45",
															"10:00","10:15","10:30","10:45",
															"11:00","11:15","11:30","11:45",
															"12:00","12:15","12:30","12:45",
															"13:00","13:15","13:30","13:45",
															"14:00","14:15","14:30","14:45",
															"15:00","15:15","15:30","15:45","15:50",
															"16:00","16:15","16:30","16:45","16:50",
															"17:00","17:15","17:30","17:45","17:50","17:55",
															"18:00","18:10","18:15","18:25","18:30","18:45","18:55",
															"19:00","19:20","19:15","19:30","19:45",
															"20:00","20:15","20:30","20:45",
															"21:00");
	private final DateFormat timeFormat = new SimpleDateFormat(WebConstants.WEB_TIME_PATTERN);

	@WireVariable
	private LessonService lessonService;

	private Lesson lesson;
	private Listitem dayOfWeekSelected;
	private List<String> timeFromList;
	private List<String> timeToList;
	private String timeFromSelected;
	private String timeToSelected;

	@Init
	public void init() {
		final EventQueue eq = EventQueues.lookup(ScbEventQueues.COURSE_APPLICATION_QUEUE.name() , EventQueues.DESKTOP, true);
		eq.subscribe(new EventListener<Event>() {
			@Override
			public void onEvent(Event event) {
				if (event.getName().equals(ScbEvent.LESSON_NEW_DATA_EVENT.name())) {
					initData((Lesson)event.getData());
					eq.unsubscribe(this);
				}
				if (event.getName().equals(ScbEvent.LESSON_DETAIL_DATA_EVENT.name())) {
					initData((Lesson)event.getData());
					eq.unsubscribe(this);
				}
			}
		});
	}

	private void initData(Lesson lesson) {
		if (lesson == null) {
			throw new IllegalArgumentException("lesson is null");
		}

		this.lesson = lesson;

		if (lesson.getDayOfWeek() == null) {
			this.dayOfWeekSelected = this.dayOfWeekList.get(0);
			this.timeFromList = this.dayTimeList.subList(0, this.dayTimeList.size() - 1);
			this.timeToList = this.dayTimeList.subList(1, this.dayTimeList.size());
			this.timeFromSelected = this.dayTimeList.get(0);
			this.timeToSelected = this.dayTimeList.get(1);
		} else {
			this.dayOfWeekSelected = getDayOfWeekListItem(lesson.getDayOfWeek());
			this.timeFromSelected = (String)getTimeConverter().coerceToUi(lesson.getTimeFrom(), null, null);
			this.timeToSelected = (String)getTimeConverter().coerceToUi(lesson.getTimeTo(), null, null);
			this.timeFromList = this.dayTimeList.subList(0, this.dayTimeList.size() - 1);
			this.timeToList = this.dayTimeList.subList(this.dayTimeList.indexOf(this.timeFromSelected) + 1, this.dayTimeList.size());
		}

		BindUtils.postNotifyChange(null, null, this, "lesson");
		BindUtils.postNotifyChange(null, null, this, "dayOfWeekSelected");
		BindUtils.postNotifyChange(null, null, this, "timeFromList");
		BindUtils.postNotifyChange(null, null, this, "timeToList");
		BindUtils.postNotifyChange(null, null, this, "timeFromSelected");
		BindUtils.postNotifyChange(null, null, this, "timeToSelected");
	}

	private Listitem getDayOfWeekListItem(DayOfWeek dayOfWeek) {
		if (dayOfWeek == null) {
			return null;
		}

		for (Listitem item : this.dayOfWeekList) {
			if (item.getValue() == dayOfWeek) {
				return item;
			}
		}

		return null;
	}

	@NotifyChange({"timeToSelected","timeToList"})
	@Command
	public void changeTimeFromCmd() {
		this.timeToList = this.dayTimeList.subList(this.dayTimeList.indexOf(this.timeFromSelected) + 1, this.dayTimeList.size());
		this.timeToSelected = this.timeToList.get(0);
	}

	@Command
	public void submitCmd(@BindingParam("window") Window window) {
		try {
			this.lesson.setDayOfWeek((DayOfWeek)this.dayOfWeekSelected.getValue());
			this.lesson.setTimeFrom(new Time(timeFormat.parse(this.timeFromSelected).getTime()));
			this.lesson.setTimeTo(new Time(timeFormat.parse(this.timeToSelected).getTime()));

			// ulozit do databaze
			lessonService.store(lesson);
			EventQueueHelper.publish(ScbEventQueues.COURSE_APPLICATION_QUEUE, ScbEvent.RELOAD_COURSE_PARTICIPANT_DATA_EVENT, null, this.lesson.getCourseUuid());
			window.detach();
		} catch (ScbValidationException e) {
			LOG.warn("ScbValidationException caught during storing lesson to course uuid: " + this.lesson.getCourseUuid());
			WebUtils.showNotificationError(e.getMessage());
		} catch (ParseException e) {
			LOG.error("ParseException caught during storing lesson to course uuid: " + this.lesson.getCourseUuid(), e);
			WebUtils.showNotificationError(e.getMessage());
		}
	}

	public Lesson getLesson() {
		return lesson;
	}

	public List<Listitem> getDayofWeekList() {
		return dayOfWeekList;
	}

	public Listitem getDayOfWeekSelected() {
		return dayOfWeekSelected;
	}

	public void setDayOfWeekSelected(Listitem dayOfWeekSelected) {
		this.dayOfWeekSelected = dayOfWeekSelected;
	}

	public String getTimeFromSelected() {
		return timeFromSelected;
	}

	public void setTimeFromSelected(String timeFromSelected) {
		this.timeFromSelected = timeFromSelected;
	}

	public String getTimeToSelected() {
		return timeToSelected;
	}

	public void setTimeToSelected(String timeToSelected) {
		this.timeToSelected = timeToSelected;
	}

	public List<String> getTimeFromList() {
		return timeFromList;
	}

	public List<String> getTimeToList() {
		return timeToList;
	}
}