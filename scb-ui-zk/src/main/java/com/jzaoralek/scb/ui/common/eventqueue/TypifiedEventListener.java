package com.jzaoralek.scb.ui.common.eventqueue;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;

import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEvent;

/**
 * Project: scb-ui-zk
 *
 * Created: 1. 11. 2018
 *
 * @author Ales Wojnar | ales@wojnar.cz | http://ales.wojnar.cz
 */
public class TypifiedEventListener implements EventListener<Event> {

    private ScbEvent eventType;
    private SingleEventConsumer singleEventConsumer;

    public TypifiedEventListener(ScbEvent eventType, SingleEventConsumer singleEventConsumer) {
        this.eventType = eventType;
        this.singleEventConsumer = singleEventConsumer;
    }

    @Override
    public void onEvent(Event e) throws Exception {
        if (e instanceof TypifiedEvent) {
            TypifiedEvent event = (TypifiedEvent) e;
            if (event.getEventType() == eventType) {
                singleEventConsumer.accept(event.getData());
            }
        }
    }
}
