package com.jzaoralek.scb.ui.common.eventqueue;

import org.zkoss.zk.ui.event.Event;

import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEvent;

/**
 * Project: scb-ui-zk
 *
 * Created: 1. 11. 2018
 *
 * @author Ales Wojnar | ales@wojnar.cz | http://ales.wojnar.cz
 */
public class TypifiedEvent extends Event {

    private static final long serialVersionUID = -757849898919748677L;

    private ScbEvent eventType;

    public TypifiedEvent(ScbEvent eventType, Object data) {
        super(eventType.name(), null, data);
        this.eventType = eventType;
    }

    public ScbEvent getEventType() {
        return eventType;
    }
}
