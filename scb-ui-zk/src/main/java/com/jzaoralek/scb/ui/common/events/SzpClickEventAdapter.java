package com.jzaoralek.scb.ui.common.events;

import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.ClickEvent;

/**
 * Project: szp_client
 *
 * Created: 11. 6. 2014
 *
 * @author Ales Wojnar | Ness Czech | ales.wojnar@ness.com
 */
public class SzpClickEventAdapter implements EventListener<ClickEvent>{

	private SzpEventListener szpListener;

	public SzpClickEventAdapter(SzpEventListener szpListener) {
		this.szpListener = szpListener;
	}

	@Override
	public void onEvent(ClickEvent e) throws Exception {
		if (szpListener == null) {
			return;
		}

		if (Messagebox.ON_OK.equals(e.getName())) {
			szpListener.onOkEvent();
        } else if (Messagebox.ON_YES.equals(e.getName())) {
			szpListener.onYesEvent();
		} else if (Messagebox.ON_NO.equals(e.getName())) {
			szpListener.onNoEvent();
		} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
			szpListener.onCancelEvent();
		} else if (e.getName().equals("onClose")) {
			szpListener.onWindowCloseEvent();
		} else {
        	throw new IllegalArgumentException("Unknow click event: " + e.getName());
		}
	}

}
