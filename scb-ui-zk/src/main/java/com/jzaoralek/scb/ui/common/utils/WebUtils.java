package com.jzaoralek.scb.ui.common.utils;

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
}
