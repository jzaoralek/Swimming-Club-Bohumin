package com.jzaoralek.scb.ui.common.events;


/**
 * Project: szp_client
 *
 * Created: 16. 1. 2014
 *
 * @author Ales Wojnar | Ness Czech | ales.wojnar@ness.com
 */
public abstract class SzpEventListener {

	/**
	 * Event source: Messagebox.ON_OK
	 */
	public void onOkEvent() {};

	/**
	 * Event source: Messagebox.ON_CANCEL
	 */
	public void onCancelEvent() {};

	/**
	 * Event source: Messagebox.ON_YES
	 */
	public void onYesEvent() {};

	/**
	 * Event source: Messagebox.ON_NO
	 */
	public void onNoEvent() {};

	/**
	 * Event source: Kdyz se zavre dialogove okno
	 */
	public void onWindowCloseEvent() {};

}
