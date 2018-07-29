package com.jzaoralek.scb.ui.pages.configuration.vm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Window;

import com.jzaoralek.scb.dataservice.domain.CourseLocation;
import com.jzaoralek.scb.dataservice.service.CourseService;
import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEvent;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEventQueues;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.BaseVM;

public class CourseLocationWinVM extends BaseVM {

	private static final Logger LOG = LoggerFactory.getLogger(CourseLocationWinVM.class);

	@WireVariable
	private CourseService courseService;
	
	private CourseLocation item;

	@Init
	public void init() {
		this.item = (CourseLocation) WebUtils.getArg(WebConstants.ITEM_PARAM);
		if (this.item == null) {
			this.item = new CourseLocation();
		}
	}

	@Command
	public void submitCmd(@BindingParam("window") Window window) {
		courseService.store(this.item);
		WebUtils.showNotificationInfo(Labels.getLabel("msg.ui.info.changesSaved"));
		EventQueueHelper.publish(ScbEventQueues.CODE_LIST_QUEUE, ScbEvent.RELOAD_COURSE_LOCATION_DATA_EVENT, null, null);
		window.detach();
	}

	public CourseLocation getItem() {
		return item;
	}

	public void setItem(CourseLocation item) {
		this.item = item;
	}
}
