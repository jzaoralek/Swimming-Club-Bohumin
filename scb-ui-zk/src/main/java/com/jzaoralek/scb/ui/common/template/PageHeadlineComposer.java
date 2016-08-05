package com.jzaoralek.scb.ui.common.template;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Menubar;

/**
 * Project: sdat_web
 *
 * Created: 12. 5. 2016
 *
 * @author Ness | http://www.ness.com
 */
public class PageHeadlineComposer extends SelectorComposer<Component> {

    /**
     *
     */
    private static final long serialVersionUID = 4989366907428879967L;

    @Wire
    Menubar menubar;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        String menuId = (String) comp.getAttribute("menuId");

        EventQueue<Event> eq = EventQueues.lookup("SDAT_QUEUE", EventQueues.DESKTOP, true);
        eq.publish(new Event("openMainMenuItem", comp, menuId));
    }
}
