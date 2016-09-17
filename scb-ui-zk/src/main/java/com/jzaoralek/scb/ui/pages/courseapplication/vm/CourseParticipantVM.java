package com.jzaoralek.scb.ui.pages.courseapplication.vm;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.bind.annotation.QueryParam;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import com.jzaoralek.scb.dataservice.domain.CourseApplication;
import com.jzaoralek.scb.dataservice.exception.ScbValidationException;
import com.jzaoralek.scb.dataservice.service.CourseApplicationService;
import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.utils.WebUtils;
import com.jzaoralek.scb.ui.common.vm.BaseVM;

public class CourseParticipantVM extends BaseVM {

	private static final Logger LOG = LoggerFactory.getLogger(CourseParticipantVM.class);

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

	@NotifyChange("*")
	@Command
    public void submit() {
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
		}
    }

	public CourseApplication getParticipant() {
		return participant;
	}

	public void setParticipant(CourseApplication participant) {
		this.participant = participant;
	}
}
