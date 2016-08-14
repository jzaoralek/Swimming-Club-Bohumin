package com.jzaoralek.scb.ui.common.template;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Menubar;

import com.jzaoralek.scb.ui.common.utils.EventQueueHelper;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEvent;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEventQueues;

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
        EventQueueHelper.publish(ScbEventQueues.MENU_QUEUE, ScbEvent.OPEN_MAIN_MENU_EVENT, comp, menuId);
//        EventQueue<Event> eq = EventQueues.lookup("SDAT_QUEUE", EventQueues.DESKTOP, true);
//        eq.publish(new Event("openMainMenuItem", comp, menuId));
    }
}
