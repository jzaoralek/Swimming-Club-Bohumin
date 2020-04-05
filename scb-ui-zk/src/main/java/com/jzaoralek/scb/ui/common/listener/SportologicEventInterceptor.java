package com.jzaoralek.scb.ui.common.listener;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.EventInterceptor;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Textbox;

/**
 * Customized event interceptor.
 *
 */
public class SportologicEventInterceptor implements EventInterceptor  {

	@Override
	public Event beforeSendEvent(Event event) {
		return event;
	}

	@Override
	public Event beforePostEvent(Event event) {
		return event;
	}

	@Override
	public Event beforeProcessEvent(Event event) {
		if(event.getName().equals(Events.ON_CHANGE)) {
			// trim textovych hodnot na eventu ON_CHANGE
			trimCompTextValue(event.getTarget());
		}
		return event;
	}

	@Override
	public void afterProcessEvent(Event event) {
		// not used
	}
	
	/**
	 * Trim text hodnot ve všech Textboxech.
	 */
	private void trimCompTextValue(Component component) {
		if (component == null) {
			return;
		}
		if(Textbox.class.isInstance(component)) {
			Textbox textbox = (Textbox)component;
			textbox.setValue(textbox.getValue().trim());
		}
	}
}
