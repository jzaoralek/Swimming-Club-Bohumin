package com.jzaoralek.scb.ui.common.utils;

import javax.servlet.http.HttpServletRequest;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.Clients;

public final class WebUtils {

	private WebUtils() {}

	public static void showNotificationInfo(String msg) {
		Clients.showNotification(msg, Clients.NOTIFICATION_TYPE_INFO, null, null, 4000, true);
	};

	public static void showNotificationError(String msg) {
		Clients.showNotification(msg, Clients.NOTIFICATION_TYPE_ERROR, null, null, 4000, true);
	};

	public static void showNotificationWarning(String msg) {
		Clients.showNotification(msg, Clients.NOTIFICATION_TYPE_WARNING, null, null, 4000, true);
	};

	/**
	 * Vraci url za domenou
	 * @return
	 */
	public static String getCurrentUrl() {
		return Executions.getCurrent().getContextPath()
				+ Executions.getCurrent().getDesktop().getRequestPath();
	}

	public static HttpServletRequest getRequest() {
		return (HttpServletRequest)Executions.getCurrent().getNativeRequest();
	}
}
