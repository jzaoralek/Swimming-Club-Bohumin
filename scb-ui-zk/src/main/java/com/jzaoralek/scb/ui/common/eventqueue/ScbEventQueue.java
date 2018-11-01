package com.jzaoralek.scb.ui.common.eventqueue;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;

import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEvent;

/**
 * Project: scb-ui-zk
 *
 * Created: 1. 11. 2018
 *
 * @author Ales Wojnar | ales@wojnar.cz | http://ales.wojnar.cz
 */
public class ScbEventQueue {

    private EventQueue<Event> queue;

    public ScbEventQueue(EventQueue<Event> queue) {
        this.queue = queue;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void subscribe(ScbEvent event, SingleEventConsumer consumer) {
        EventListener listener = new TypifiedEventListener(event, consumer);
        queue.subscribe(listener);
    }
}
