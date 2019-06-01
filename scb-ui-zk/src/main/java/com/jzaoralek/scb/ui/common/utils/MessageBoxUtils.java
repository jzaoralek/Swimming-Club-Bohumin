package com.jzaoralek.scb.ui.common.utils;

import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Messagebox;

import com.jzaoralek.scb.ui.common.events.SzpClickEventAdapter;
import com.jzaoralek.scb.ui.common.events.SzpEventListener;

/**
 * Project: szp_client
 *
 * Created: 16. 1. 2014
 *
 * @author Ales Wojnar | Ness Czech | ales.wojnar@ness.com
 */
public class MessageBoxUtils {

	public static final String DEFAULT_CONFIRM_MSG = "msg.quest.confirmAction";
	public static final String DEFAULT_CONFIRM_TITLE_MSG = "msg.quest.confirmAction.title";
	public static final String BTN_YES_LABEL = Labels.getLabel("btn.yes");
	public static final String BTN_NO_LABEL = Labels.getLabel("btn.no");
	public static final String BTN_OK_LABEL = Labels.getLabel("btn.ok");
	public static final String BTN_OK_WARNING_DIALOG_LABEL = Labels.getLabel("btn.ok.warningDialog");
	public static final String BTN_CANCEL_LABEL = Labels.getLabel("btn.cancel");

	/**
	 * Zobrazí dialogové okno, které obsahuje tlačítka "OK" a "Cancel"
	 *
	 * @param szpEventListener
	 */
	public static void showDefaultConfirmDialog(SzpEventListener szpEventListener) {
		showDefaultConfirmDialog(DEFAULT_CONFIRM_MSG, DEFAULT_CONFIRM_TITLE_MSG, szpEventListener);
	}

	/**
	 * Zobrazí dialogové okno, které obsahuje tlačítka "OK" a "Cancel".
	 *
	 * Volitelné popisky.
	 *
	 * @param messageKey klíč textové zprávy
	 * @param titleKey klíč textace pro nadpis dialogového okna
	 * @param szpEventListener
	 */
	public static void showDefaultConfirmDialog(String messageKey, String titleKey, SzpEventListener szpEventListener) {
		showDefaultConfirmDialog(messageKey, titleKey, szpEventListener, new Object[]{});
	}

	/**
	 * Zobrazí dialogové okno, které obsahuje tlačítka "OK" a "Cancel".
	 *
	 * Volitelné popisky.
	 *
	 * Volitelné parametry textové zprávy.
	 *
	 * @param messageKey klíč textové zprávy
	 * @param titleKey klíč textace pro nadpis dialogového okna
	 * @param szpEventListener
	 * @param msgParam parametry textové zprávy
	 */
	public static void showDefaultConfirmDialog(String messageKey, String titleKey, SzpEventListener szpEventListener, Object... msgParam) {
		Messagebox.show(
				Labels.getLabel(messageKey, msgParam),
				Labels.getLabel(titleKey),
				new Messagebox.Button[] {Messagebox.Button.OK, Messagebox.Button.CANCEL},
				new String[] {BTN_OK_LABEL, BTN_CANCEL_LABEL},
				Messagebox.QUESTION,
				null,
				new SzpClickEventAdapter(szpEventListener));
	}

	/**
	 * Zobrazí dialogové okno, které obsahuje tlačítka "YES" a "NO".
	 *
	 * @param messageKey
	 * @param titleKey
	 * @param szpEventListener
	 */
	public static void showYesNoConfirmDialog(String messageKey, String titleKey, SzpEventListener szpEventListener) {
		Messagebox.show(
				Labels.getLabel(messageKey),
				Labels.getLabel(titleKey),
				new Messagebox.Button[] {Messagebox.Button.YES, Messagebox.Button.NO},
				new String[] {BTN_YES_LABEL, BTN_NO_LABEL},
				Messagebox.QUESTION,
				null,
				new SzpClickEventAdapter(szpEventListener));
	}

	/**
	 * Zobrazí dialogové okno s vykřičníkem a tlačítkem "OK"
	 *
	 * @param messageKey
	 * @param titleKey
	 * @param szpEventListener
	 */
	public static void showOkWarningDialog(String messageKey, String titleKey, SzpEventListener szpEventListener) {
		Messagebox.show(
				Labels.getLabel(messageKey),
				Labels.getLabel(titleKey),
				new Messagebox.Button[] {Messagebox.Button.OK},
				new String[] {BTN_OK_WARNING_DIALOG_LABEL},
				Messagebox.EXCLAMATION,
				null,
				new SzpClickEventAdapter(szpEventListener));
	}
	
	/**
	 * Zobrazí dialogové okno s vykřičníkem a tlačítkem "OK"
	 *
	 * @param messageKey
	 * @param titleKey
	 * @param szpEventListener
	 */
	public static void showOkWarningDialog(String messageKey, String titleKey, SzpEventListener szpEventListener, Object... msgParam) {
		Messagebox.show(
				Labels.getLabel(messageKey, msgParam),
				Labels.getLabel(titleKey),
				new Messagebox.Button[] {Messagebox.Button.OK},
				new String[] {BTN_OK_WARNING_DIALOG_LABEL},
				Messagebox.EXCLAMATION,
				null,
				new SzpClickEventAdapter(szpEventListener));
	}

	/**
	 * Zobrazí dialogové okno s vykřičníkem a tlačítkem "OK" (viz showOkWarningDialog) s nastaveným popiskem a titlkem
	 * informujícím o chybějících právech na prohlížení/editaci obsahu.
	 */
	public static void showHaveNoRightsDialog() {
		MessageBoxUtils.showOkWarningDialog(
			"txt.haveNoRigthsCommon",
			"txt.unauthorizedAccessShort",
			null);
	}
}
