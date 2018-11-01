package com.jzaoralek.scb.ui.common.eventqueue;

/**
 * Project: scb-ui-zk
 *
 * Created: 1. 11. 2018
 *
 * @author Ales Wojnar | ales@wojnar.cz | http://ales.wojnar.cz
 */
public interface SingleEventConsumer {

    public void accept(Object data) throws Exception;

}
