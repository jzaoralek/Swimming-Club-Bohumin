package com.jzaoralek.scb.ui.common.utils;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;

import com.jzaoralek.scb.ui.common.eventqueue.ScbEventQueue;
import com.jzaoralek.scb.ui.common.eventqueue.TypifiedEvent;

public final class EventQueueHelper {

	public enum ScbEventQueues {
		MENU_QUEUE,
        SIDE_MENU_QUEUE,
		COURSE_APPLICATION_QUEUE,
		CODE_LIST_QUEUE,
		RESULT_QUEUE,
		USER_QUEUE,
		LEARNING_LESSON_QUEUE,
		PAYMENT_QUEUE;
	}

	public enum ScbEvent {
        OPEN_MAIN_MENU_EVENT(null),
        RELOAD_COURSE_APPLICATION_DATA_EVENT(null),
        RELOAD_COURSE_DATA_EVENT(null),
        RELOAD_COURSE_PARTICIPANT_DATA_EVENT(null),
        COURSE_UUID_FROM_APPLICATION_DATA_EVENT(null),
        COURSE_UUID_FROM_COURSE_DATA_EVENT(null),
        LESSON_NEW_DATA_EVENT(null),
        LESSON_DETAIL_DATA_EVENT(null),
        RELOAD_CODELIST_DATA_EVENT(null),
        CODELIST_NEW_DATA_EVENT(null),
        CODELIST_DETAIL_DATA_EVENT(null),
        RELOAD_RESULT_LIST_DATA_EVENT(null),
        RESULT_DETAIL_DATA_EVENT(null),
        RESULT_NEW_DATA_EVENT(null),
        RELOAD_USER_DATA_EVENT(null),
        RELOAD_PAYMENT_DATA_EVENT(null),
        RELOAD_COURSE_LOCATION_DATA_EVENT(null),
        SIDE_MENU_FOLD_EVENT(ScbEventQueues.SIDE_MENU_QUEUE),
        MOBILE_MODE_EVENT(ScbEventQueues.SIDE_MENU_QUEUE),
        SET_MENU_SELECTED(ScbEventQueues.SIDE_MENU_QUEUE);

        private ScbEventQueues queue;

        private ScbEvent(ScbEventQueues queue) {
            this.queue = queue;
        }

        public ScbEventQueues getQueue() {
            return queue;
        }
	}

	private EventQueueHelper() {}

    /**
     * Deprecated, use EventQueueHelper.publish(ScbEvent event, Object data)
     * 
     * @param eventQueueName
     * @param eventName
     * @param target
     * @param data
     */
    @Deprecated
	public static void publish(ScbEventQueues eventQueueName, ScbEvent eventName, Component target, Object data) {
		EventQueue<Event> eq = EventQueues.lookup(eventQueueName.name(), EventQueues.DESKTOP, true);
        eq.publish(new Event(eventName.name(), target, data));
	}

    /**
     * 
     * @param event
     * @param data
     */
    public static void publish(ScbEvent event, Object data) {
        publish(event, data, EventQueues.DESKTOP);
    }

    /**
     * 
     * @param event
     * @param data
     * @param scope
     */
    private static void publish(ScbEvent event, Object data, String scope) {
        if (event.getQueue() == null) {
            throw new IllegalArgumentException("Udalost " + event.name() + " nema definovanou frontu!");
        }
        EventQueue<Event> eq = EventQueues.lookup(event.getQueue().name(), scope, true);
        eq.publish(new TypifiedEvent(event, data));
    }

    /**
     * Přihlášení k odběru
     * 
     * @param queue
     * @return
     */
    public static ScbEventQueue queueLookup(ScbEventQueues queue) {
        return queueLookup(queue, EventQueues.DESKTOP);
    }

    private static ScbEventQueue queueLookup(ScbEventQueues queue, String scope) {
        return new ScbEventQueue(EventQueues.lookup(queue.name(), EventQueues.DESKTOP, true));
    }

}