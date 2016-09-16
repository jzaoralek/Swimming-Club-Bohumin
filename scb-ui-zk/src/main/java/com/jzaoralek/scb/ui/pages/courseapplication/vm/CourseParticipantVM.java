package com.jzaoralek.scb.ui.pages.courseapplication.vm;

import java.util.UUID;

import org.springframework.util.StringUtils;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.QueryParam;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import com.jzaoralek.scb.dataservice.domain.CourseApplication;
import com.jzaoralek.scb.dataservice.service.CourseApplicationService;
import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.vm.BaseVM;

public class CourseParticipantVM extends BaseVM {

	@WireVariable
	private CourseApplicationService courseApplicationService;

	private CourseApplication participant;

	@Init
	public void init(@QueryParam(WebConstants.UUID_PARAM) String uuid, @QueryParam(WebConstants.FROM_PAGE_PARAM) String fromPage) {
		if (StringUtils.hasText(uuid)) {
			this.participant = courseApplicationService.getByUuid(UUID.fromString(uuid));
		}
		setReturnPage(fromPage);
	}

	public CourseApplication getParticipant() {
		return participant;
	}

	public void setParticipant(CourseApplication participant) {
		this.participant = participant;
	}
}
