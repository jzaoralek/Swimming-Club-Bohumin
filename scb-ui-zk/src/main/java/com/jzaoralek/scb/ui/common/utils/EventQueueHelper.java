package com.jzaoralek.scb.ui.common.utils;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;

public final class EventQueueHelper {

	public enum ScbEventQueues {
		MENU_QUEUE,
		SDAT_COURSE_APPLICATION_QUEUE;
	}

	public enum ScbEvent {
		OPEN_MAIN_MENU_EVENT,
		RELOAD_COURSE_APPLICATION_DATA_EVENT;
	}

	private EventQueueHelper() {}

	public static void publish(ScbEventQueues eventQueueName, ScbEvent eventName, Component target, Object data) {
		EventQueue<Event> eq = EventQueues.lookup(eventQueueName.name(), EventQueues.DESKTOP, true);
        eq.publish(new Event(eventName.name(), target, data));
	}

//	public static void subscribe(SdatEventQueues eventQueueName, SdatEvent eventName, Consumer<Object> consumer) {
//		EventQueues.lookup(eventQueueName, EventQueues.DESKTOP, true).subscribe(new EventListener<Event>() {
//			@SuppressWarnings("unchecked")
//			@Override
//			public void onEvent(Event event) {
//				if (event.getName().equals(eventName)) {
//					consumer.accept(event.getData());
//				}
//			}
//		});
//		EventQueues.lookup(eventQueueName.name(), EventQueues.DESKTOP, true).subscribe((event) -> {
//			if (event.getName().equals(eventName.name())) {
//				consumer.accept(event.getData());
//			}}
//		);
//	}
}