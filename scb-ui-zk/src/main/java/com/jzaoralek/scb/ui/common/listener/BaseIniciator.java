package com.jzaoralek.scb.ui.common.listener;

import java.util.Map;

import org.springframework.util.StringUtils;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.util.Initiator;

import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.utils.WebUtils;

public class BaseIniciator implements Initiator {

	@Override
	public void doInit(Page page, Map<String, Object> args) throws Exception {
		// zobrazeni notifikace po redirectu
		String notifMessage = (String)WebUtils.getSessAtribute(WebConstants.NOTIFICATION_MESSAGE);
		if (StringUtils.hasText(notifMessage)) {
			WebUtils.showNotificationInfo(notifMessage);
			WebUtils.removeSessAtribute(WebConstants.NOTIFICATION_MESSAGE);
		}
	}
}
