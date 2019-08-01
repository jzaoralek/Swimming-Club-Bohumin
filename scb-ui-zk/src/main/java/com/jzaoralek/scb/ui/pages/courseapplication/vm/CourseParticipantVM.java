package com.jzaoralek.scb.ui.pages.courseapplication.vm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

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
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Listitem;

import com.jzaoralek.scb.dataservice.domain.CodeListItem;
import com.jzaoralek.scb.dataservice.domain.CodeListItem.CodeListType;
import com.jzaoralek.scb.dataservice.domain.Course;
import com.jzaoralek.scb.dataservice.domain.CourseApplication;
import com.jzaoralek.scb.dataservice.domain.LearningLessonStatsWrapper;
import com.jzaoralek.scb.dataservice.domain.Result;
import com.jzaoralek.scb.dataservice.domain.ScbUserRole;
import com.jzaoralek.scb.dataservice.exception.ScbValidationException;
import com.jzaoralek.scb.dataservice.service.CodeListService;
import com.jzaoralek.scb.dataservice.service.CourseApplicationService;
import com.jzaoralek.scb.dataservice.service.LearningLessonService;
import com.jzaoralek.scb.dataservice.service.ResultService;
import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.WebPages;
import com.jzaoralek.scb.ui.common.component.address.AddressUtils;
import com.jzaoralek.scb.ui.common.converter.Converters;
import com.jzaoralek.scb.ui.common.events.SzpEventListener;
import com.jzaoralek.scb.ui.common.template.SideMenuComposer.ScbMenuItem;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEvent;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEventQueues;
import com.jzaoralek.scb.ui.common.utils.MessageBoxUtils;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.BaseVM;

public class CourseParticipantVM extends BaseVM {

	private static final Logger LOG = LoggerFactory.getLogger(CourseParticipantVM.class);

	private static final String RESULT_DETAIL_WINDOW = "/pages/secured/TRAINER/result-detail-window.zul";

	@WireVariable
	private CourseApplicationService courseApplicationService;

	@WireVariable
	private CodeListService codeListService;

	@WireVariable
	private ResultService resultService;
	
	@WireVariable
	private LearningLessonService learningLessonService;

	private List<Listitem> swimStyleListitemList;
	private Listitem swimStyleListitemSelected;
	private List<Listitem> courseItemList;
	private Listitem courseListitemSelected;
	private String pageHeadline;
	private CourseApplication participant;
	private LearningLessonStatsWrapper lessonStats;
	private boolean attendanceTabSelected;
	private UUID courseUuidSelected;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Init
	public void init(@QueryParam(WebConstants.UUID_PARAM) String uuid
			, @QueryParam(WebConstants.FROM_PAGE_PARAM) String fromPage
			, @QueryParam(WebConstants.COURSE_UUID_PARAM) String courseUuid) {
        setMenuSelected(ScbMenuItem.SEZNAM_UCASTNIKU_AT);
        setMenuSelected(ScbMenuItem.SEZNAM_UCASTNIKU_U);
		if (StringUtils.hasText(uuid)) {
			this.participant = courseApplicationService.getByUuid(UUID.fromString(uuid));
			this.pageHeadline = this.participant.getCourseParticipant().getContact().getCompleteName();
		}
		if (StringUtils.hasText(courseUuid)) {
			this.attendanceTabSelected = true;
			this.courseUuidSelected = UUID.fromString(courseUuid);
		}
		setReturnPage(fromPage);
		fillSwimStyleItemList();
		buildCourseStatistics();
		
		final EventQueue eq = EventQueues.lookup(ScbEventQueues.RESULT_QUEUE.name() , EventQueues.DESKTOP, true);
		eq.subscribe(new EventListener<Event>() {
			@Override
			public void onEvent(Event event) {
				if (event.getName().equals(ScbEvent.RELOAD_RESULT_LIST_DATA_EVENT.name())) {
					refreshDataCmd();
				}
			}
		});
	}

	@NotifyChange("*")
	@Command
    public void submit() {
		if (!AddressUtils.isAddressValid()) {
			return;
		}
		try {
			// update
			if (LOG.isDebugEnabled()) {
				LOG.debug("Updating application: " + this.participant);
			}
			courseApplicationService.store(this.participant);
			WebUtils.showNotificationInfo(Labels.getLabel("msg.ui.info.changesSaved"));
		} catch (ScbValidationException e) {
			LOG.warn("ScbValidationException caught for application: " + this.participant);
			WebUtils.showNotificationError(e.getMessage());
		} catch (Exception e) {
			LOG.error("Unexpected exception caught for application: " + this.participant, e);
			throw new RuntimeException(e);
		} finally {
			AddressUtils.setAddressInvalid();
		}
    }

	@NotifyChange("participant")
	@Command
    public void loadResultsByStyleCmd() {
		if (this.swimStyleListitemSelected.getValue() != null) {
			this.participant.getCourseParticipant().setResultList(resultService.getByCourseParticipantAndStyle(this.participant.getCourseParticipant().getUuid(), UUID.fromString((String)this.swimStyleListitemSelected.getValue())));
		} else {
			this.participant.getCourseParticipant().setResultList(resultService.getByCourseParticipant(this.participant.getCourseParticipant().getUuid()));
		}
	}

	@Command
	public void newResultCmd() {
		Result result = new Result();
		result.setCourseParticipantUuid(this.participant.getCourseParticipant().getUuid());
		result.setResultDate(Calendar.getInstance().getTime());
		EventQueueHelper.publish(ScbEventQueues.RESULT_QUEUE, ScbEvent.RESULT_NEW_DATA_EVENT, null, result);
		WebUtils.openModal(RESULT_DETAIL_WINDOW);
	}

	@Command
	public void resultDetailCmd(@BindingParam("result") final Result result) {
		if (result == null) {
			throw new IllegalArgumentException("result is null");
		}
		EventQueueHelper.publish(ScbEventQueues.RESULT_QUEUE, ScbEvent.RESULT_DETAIL_DATA_EVENT, null, result);
		WebUtils.openModal(RESULT_DETAIL_WINDOW);
	}

	@Command
	public void resultDeleteCmd(@BindingParam(WebConstants.ITEM_PARAM) final Result item) {
		if (item ==  null) {
			throw new IllegalArgumentException("Result is null");
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("Deleting result with uuid: " + item.getUuid());
		}
		final Object[] msgParams = new Object[] {item.getStyle().getName() + ", " + item.getDistance()+"m" + ", " + Converters.getIntervaltomillsconverter().coerceToUi(item.getResultTime(), null, null)};
		final UUID uuid = item.getUuid();
		MessageBoxUtils.showDefaultConfirmDialog(
			"msg.ui.quest.deleteItem.arg",
			"msg.ui.title.deleteRecord",
			new SzpEventListener() {
				@Override
				public void onOkEvent() {
					try {
						resultService.delete(uuid);
						EventQueueHelper.publish(ScbEventQueues.RESULT_QUEUE, ScbEvent.RELOAD_RESULT_LIST_DATA_EVENT, null, null);
						WebUtils.showNotificationInfo(Labels.getLabel("msg.ui.info.itemDeleted.arg", msgParams));
					} catch (ScbValidationException e) {
						LOG.warn("ScbValidationException caught for result with uuid: " + uuid, e);
						WebUtils.showNotificationError(e.getMessage());
					}
				}
			},
			msgParams
		);
	}

	@Command
    public void loadLessonStatsByCourseCmd() {
		Executions.sendRedirect(WebPages.PARTICIPANT_DETAIL.getUrl() + "?"+WebConstants.UUID_PARAM+"="+this.participant.getUuid().toString() + "&" + WebConstants.FROM_PAGE_PARAM + "=" + WebPages.PARTICIPANT_LIST + "&" + WebConstants.COURSE_UUID_PARAM + "=" + courseListitemSelected.getValue());
	}
	
	@Command
    public void refreshDataCmd() {
		this.participant = courseApplicationService.getByUuid(this.participant.getUuid());
		BindUtils.postNotifyChange(null, null, this, "participant");
	}
	
	/**
	 * Otevre stranku pro odeslani emailu na emailove adresy vybranych ucastniku.
	 */
	@Command
	public void goToSendEmailCmd() {
		goToSendEmailCore(new HashSet<>(Arrays.asList(this.participant.getCourseParticRepresentative().getContact())));
	}
	
	/**
	 * Nastavi datum narozeni podle rodneho cisla.
	 * @param personalNumber
	 * @param fx
	 */
	@Command
	public void birtNumberOnChangeCmd(@BindingParam("personal_number") String personalNumber, @BindingParam("fx") CourseParticipantVM fx) {
		// predvyplneni datumu narozeni podle rodneho cisla
		 WebUtils.setBirthdateByBirthNumer(personalNumber, fx.getParticipant().getCourseParticipant());
		 BindUtils.postNotifyChange(null, null, this, "participant");
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
	
	private void buildCourseStatistics() {
		Course course = null;
		this.courseItemList = new ArrayList<>();
		if (participant.getCourseParticipant().getCourseList().size() == 1) {
			// ucastnik prirazen na jeden kurz, statistika primo zobrazena
			course = participant.getCourseParticipant().getCourseList().get(0);
			Listitem itemCourse = new Listitem(course.getName(), course.getUuid().toString());
			courseItemList.add(itemCourse);
			this.courseListitemSelected = itemCourse;
			buildCourseStatsByCourse(course.getUuid());
		} else {
			// ucastnik prirazen na vice kurzu, zobrazena nabidka
			for (Course item : participant.getCourseParticipant().getCourseList()) {
				courseItemList.add(new Listitem(item.getName(), item.getUuid().toString()));
			}
			
			if (!CollectionUtils.isEmpty(participant.getCourseParticipant().getCourseList())) {
				if (this.courseUuidSelected != null) {
					// zmena vybraneho kurzu
					buildCourseStatsByCourse(this.courseUuidSelected);
					// nastaveni vybrane hodnoty do this.courseListitemSelected
					for (Listitem item : courseItemList) {
						if (((String)item.getValue()).equals(this.courseUuidSelected.toString())) {
							this.courseListitemSelected = item;
						}
					}
				} else {
					// init zobrazeni, vybran prvni kurz
					buildCourseStatsByCourse(participant.getCourseParticipant().getCourseList().get(0).getUuid());
					this.courseListitemSelected = courseItemList.get(0);					
				}
			}
			
		}
	}
	
	private void buildCourseStatsByCourse(UUID courseUuid) {
		if (courseUuid == null) {
			return;
		}
		this.lessonStats = learningLessonService.buildCourseStatistics(courseUuid, participant.getCourseParticipant().getUuid());
	}
	
	public boolean isItemReadOnly() {
		return isLoggedUserInRole(ScbUserRole.TRAINER.name());
	}

	public CourseApplication getParticipant() {
		return participant;
	}

	public void setParticipant(CourseApplication participant) {
		this.participant = participant;
	}

	public List<Listitem> getSwimStyleListitemList() {
		return swimStyleListitemList;
	}

	public Listitem getSwimStyleListitemSelected() {
		return swimStyleListitemSelected;
	}
	
	public void setSwimStyleListitemSelected(Listitem swimStyleListitemSelected) {
		this.swimStyleListitemSelected = swimStyleListitemSelected;
	}
	
	public List<Listitem> getCourseItemList() {
		return courseItemList;
	}

	public Listitem getCourseListitemSelected() {
		return courseListitemSelected;
	}

	public void setCourseListitemSelected(Listitem courseListitemSelected) {
		this.courseListitemSelected = courseListitemSelected;
	}

	public String getPageHeadline() {
		return pageHeadline;
	}

	public LearningLessonStatsWrapper getLessonStats() {
		return lessonStats;
	}
	
	public boolean isAttendanceTabSelected() {
		return attendanceTabSelected;
	}

	public void setAttendanceTabSelected(boolean attendanceTabSelected) {
		this.attendanceTabSelected = attendanceTabSelected;
	}
}
