package com.jzaoralek.scb.ui.pages.configuration.vm;

import java.util.HashMap;
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
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Messagebox;

import com.jzaoralek.scb.dataservice.domain.CodeListItem;
import com.jzaoralek.scb.dataservice.domain.CourseLocation;
import com.jzaoralek.scb.dataservice.exception.ScbValidationException;
import com.jzaoralek.scb.dataservice.service.CourseService;
import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.events.SzpEventListener;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEvent;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEventQueues;
import com.jzaoralek.scb.ui.common.utils.MessageBoxUtils;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.BaseVM;

public class CourseLocationAdminVM extends BaseVM {

	private static final Logger LOG = LoggerFactory.getLogger(SwimStyleAdminVM.class);

	private static final String COURSE_LOCATION_WINDOW= "/pages/secured/ADMIN/course-location-window.zul";

	@WireVariable
	private CourseService courseService;

	private List<CourseLocation> courseLocationList;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Init
	public void init() {
		loadData();

		final EventQueue eq = EventQueues.lookup(ScbEventQueues.CODE_LIST_QUEUE.name() , EventQueues.DESKTOP, true);
		eq.subscribe(new EventListener<Event>() {
			@Override
			public void onEvent(Event event) {
				if (event.getName().equals(ScbEvent.RELOAD_COURSE_LOCATION_DATA_EVENT.name())) {
					loadData();
				}
			}
		});
	}

	@NotifyChange("courseLocationList")
	@Command
	public void refreshDataCmd() {
		loadData();
	}

	@NotifyChange("courseLocationList")
	@Command
	public void deleteCmd(@BindingParam(WebConstants.ITEM_PARAM) final CourseLocation item) {
		if (item ==  null) {
			throw new IllegalArgumentException("item is null");
		}
		
		// validace zda nani misto kurzu pouzito u nejakeho kurzu
		if (courseService.existsByCourseLocation(item.getUuid())) {
			Messagebox.show(Labels.getLabel("msg.ui.validation.err.courseLocationUsed", new Object[] {item.getName()}), Labels.getLabel("txt.ui.common.warning"), Messagebox.OK, Messagebox.EXCLAMATION);
			return;
		}
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("Deleting courseLocation with uuid: " + item.getUuid());
		}
		final Object[] msgParams = new Object[] {item.getName()};
		MessageBoxUtils.showDefaultConfirmDialog(
			"msg.ui.quest.deleteItem.arg",
			"msg.ui.title.deleteRecord",
			new SzpEventListener() {
				@Override
				public void onOkEvent() {
					courseService.deleteCourseLocation(item);;
					EventQueueHelper.publish(ScbEventQueues.CODE_LIST_QUEUE, ScbEvent.RELOAD_COURSE_LOCATION_DATA_EVENT, null, null);
					WebUtils.showNotificationInfo(Labels.getLabel("msg.ui.info.itemDeleted.arg", msgParams));
				}
			},
			msgParams
		);
	}

	@Command
	public void newItemCmd() {
		WebUtils.openModal(COURSE_LOCATION_WINDOW);
	}

	@Command
    public void detailCmd(@BindingParam("item") final CourseLocation item) {
		if (item == null) {
			throw new IllegalArgumentException("codeListItem is null");
		}
		
		Map<String, Object> args = new HashMap<>();
		args.put(WebConstants.ITEM_PARAM, item);
		WebUtils.openModal(COURSE_LOCATION_WINDOW, null, args);
	}

	private void loadData() {
		this.courseLocationList = courseService.getCourseLocationAll();
		BindUtils.postNotifyChange(null, null, this, "courseLocationList");
	}
	
	public List<CourseLocation> getCourseLocationList() {
		return courseLocationList;
	}
	
}