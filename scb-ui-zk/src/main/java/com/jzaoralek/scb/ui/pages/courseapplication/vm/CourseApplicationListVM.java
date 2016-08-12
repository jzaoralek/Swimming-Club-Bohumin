package com.jzaoralek.scb.ui.pages.courseapplication.vm;

import java.util.List;
import java.util.UUID;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import com.jzaoralek.scb.dataservice.domain.CourseApplication;
import com.jzaoralek.scb.dataservice.service.CourseApplicationService;
import com.jzaoralek.scb.ui.common.events.SzpEventListener;
import com.jzaoralek.scb.ui.common.utils.MessageBoxUtils;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.BaseVM;

public class CourseApplicationListVM extends BaseVM {

	@WireVariable
	private CourseApplicationService courseApplicationService;

	private List<CourseApplication> courseApplicationList;

	@Init
	public void init() {
		this.courseApplicationList = courseApplicationService.getAll();
	}

	@NotifyChange("*")
	@Command
    public void deleteCmd(@BindingParam("uuid") UUID uuid) {
		if (uuid ==  null) {
			throw new IllegalArgumentException("uuid is null");
		}
		MessageBoxUtils.showDefaultConfirmDialog(
			"msg.ui.quest.deleteApplication",
			"msg.ui.title.deleteRecord",
			new SzpEventListener() {
				@Override
				public void onOkEvent() {
					courseApplicationService.delete(uuid);
					WebUtils.showNotificationInfo(Labels.getLabel("msg.ui.info.applicationDeleted"));
				}
			}
		);
	}

	public List<CourseApplication> getCourseApplicationList() {
		return courseApplicationList;
	}
}
