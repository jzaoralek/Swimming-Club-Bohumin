package com.jzaoralek.scb.ui.pages.courseapplication.vm;

import java.util.List;
import java.util.UUID;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import com.jzaoralek.scb.dataservice.domain.CourseApplication;
import com.jzaoralek.scb.dataservice.exception.ScbValidationException;
import com.jzaoralek.scb.dataservice.service.CourseApplicationService;
import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.WebPages;
import com.jzaoralek.scb.ui.common.events.SzpEventListener;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEvent;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEventQueues;
import com.jzaoralek.scb.ui.common.utils.MessageBoxUtils;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.BaseVM;

public class CourseApplicationListVM extends BaseVM {

	@WireVariable
	private CourseApplicationService courseApplicationService;

	private List<CourseApplication> courseApplicationList;

	@Init
	public void init() {
		loadData();

		EventQueues.lookup(ScbEventQueues.SDAT_COURSE_APPLICATION_QUEUE.name() , EventQueues.DESKTOP, true).subscribe(new EventListener<Event>() {
			@Override
			public void onEvent(Event event) {
				if (event.getName().equals(ScbEvent.RELOAD_COURSE_APPLICATION_DATA_EVENT.name())) {
					loadData();
				}
			}
		});
	}

	@NotifyChange("*")
	@Command
    public void deleteCmd(@BindingParam(WebConstants.UUID_PARAM) final UUID uuid) {
		if (uuid ==  null) {
			throw new IllegalArgumentException("uuid is null");
		}
		MessageBoxUtils.showDefaultConfirmDialog(
			"msg.ui.quest.deleteApplication",
			"msg.ui.title.deleteRecord",
			new SzpEventListener() {
				@Override
				public void onOkEvent() {
					try {
						courseApplicationService.delete(uuid);
						EventQueueHelper.publish(ScbEventQueues.SDAT_COURSE_APPLICATION_QUEUE, ScbEvent.RELOAD_COURSE_APPLICATION_DATA_EVENT, null, null);
						WebUtils.showNotificationInfo(Labels.getLabel("msg.ui.info.applicationDeleted"));
					} catch (ScbValidationException e) {
						WebUtils.showNotificationError(e.getMessage());
					}
				}
			}
		);
	}

	@Command
    public void detailCmd(@BindingParam(WebConstants.UUID_PARAM) final UUID uuid) {
		if (uuid ==  null) {
			throw new IllegalArgumentException("uuid is null");
		}
		Executions.sendRedirect("/pages/secured/prihlaska-do-klubu.zul?"+WebConstants.UUID_PARAM+"="+uuid.toString() + "&" + WebConstants.FROM_PAGE_PARAM + "=" + WebPages.APPLICATION_LIST);
	}

	public void loadData() {
		this.courseApplicationList = courseApplicationService.getAll();
		BindUtils.postNotifyChange(null, null, this, "courseApplicationList");
	}

	public List<CourseApplication> getCourseApplicationList() {
		return courseApplicationList;
	}
}
