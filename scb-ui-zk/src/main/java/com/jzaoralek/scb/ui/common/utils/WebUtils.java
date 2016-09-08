package com.jzaoralek.scb.ui.common.utils;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.StringUtils;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Window;

import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.servlet.FileDownloadServlet;
import com.jzaoralek.scb.ui.common.vm.Attachment;

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

	public static void downloadAttachment(Attachment attachment) {
		if (attachment == null) {
			return;
		}
		Executions.getCurrent().getSession().setAttribute(WebConstants.ATTACHMENT_PARAM, attachment);
		String attachmentUrl = Executions.getCurrent().getContextPath() + FileDownloadServlet.URL;
		Clients.evalJavaScript("window.open('"+attachmentUrl+"', '_blank');");
	}

	public static void openModal(String uri) {
		if (!StringUtils.hasText(uri)) {
			return;
		}
		Window window = (Window)Executions.createComponents(uri, null, null);
		window.doModal();
	}
}