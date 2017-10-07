package com.jzaoralek.scb.ui.common.utils;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.StringUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.jzaoralek.scb.ui.common.vm.Attachment;

public final class WebUtils {

	private WebUtils() {}

	public static void showNotificationInfo(String msg) {
		Clients.showNotification(msg, Clients.NOTIFICATION_TYPE_INFO, null, null, 6000, true);
	};

	public static void showNotificationError(String msg) {
		Clients.showNotification(msg, Clients.NOTIFICATION_TYPE_ERROR, null, null, 6000, true);
	};

	public static void showNotificationWarning(String msg) {
		Clients.showNotification(msg, Clients.NOTIFICATION_TYPE_WARNING, null, null, 6000, true);
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
		Filedownload.save(attachment.getByteArray(), attachment.getContentType(), attachment.getName());
	}

	public static void openModal(String uri) {
		if (!StringUtils.hasText(uri)) {
			return;
		}
		Window window = (Window)Executions.createComponents(uri, null, null);
		window.doModal();
	}

	// ==========================================================================
	// Popups
	// ==========================================================================
	/**
	 * Otevre popup s predanim parametru do VM popupu.
	 *
	 * @param page
	 *            Nazev stranky (ZUL soubor)
	 * @param windowName
	 *            Nadpis okna
	 * @param args
	 *            Seznam predavanych parametru. V modalnim okne lze argumenty nacist metodou WebHelper.getArg(argName)
	 */
	public static void openModal(String page, String windowName, Map<String, Object> args) {
		if (!StringUtils.hasText(page)) {
			throw new IllegalArgumentException("URL stranky je null");
		}
		Window window = (Window) Executions.createComponents(page, null, args);
		window.doModal();
		if (StringUtils.hasText(windowName)) {
			window.setTitle(windowName);
		}
	}

	/**
	 *
	 * @param argName
	 * @return
	 */
	public static Object getArg(String argName) {
		return Executions.getCurrent().getArg().get(argName);
	}
	
	/**
	 * Metoda pro ziskani listu objektu SelectItem z enum objektu.
	 *
	 * Vysledny List obsahuje pro kazdou konstantu definovanou pro dany Enum jednu polozku.
	 * Value polozky je konstanta enumu a Label polozky je nacten z bundle LABELS pod klicem {@literal "nbs.enum." + ${enum.name} + ${enum.item.name}}
	 *
	 * @param enumSet        objekt EnumSet obsahujici polozky enumu, pro ktere maji byt nacteny messages z message bundle
	 * @return               List<SelectItem>
	 */
	public static List<Listitem> getMessageItemsFromEnum(EnumSet<? extends Enum<?>> enumSet){

		List<Listitem> messagesList = new ArrayList<Listitem>(enumSet.size());

		// fill messageList with selected items loaded from enum object
		for(Enum<?> object:enumSet){
			Listitem item= new Listitem();
			item.setLabel(getMessageItemFromEnum(object));
			item.setValue(object);
			messagesList.add(item);
		}
		return messagesList;
	}

	public static List<Listitem> getMessageItemsFromEnumWithEmptyItem(EnumSet<? extends Enum<?>> enumSet){

		List<Listitem> messagesList = getMessageItemsFromEnum(enumSet);
		Listitem item= new Listitem();
		item.setLabel("");
		item.setValue(null);
		messagesList.add(0, item);

		return messagesList;
	}

	/**
	 * Metoda pro ziskani lokalizovane reprezentace polozky enumu z resource bundle
	 * @param e
	 * @return
	 */
	public static String getMessageItemFromEnum(Enum<?> e){
		return Labels.getLabel("enum." + e.getDeclaringClass().getSimpleName() + "." + e.name());
	}
}