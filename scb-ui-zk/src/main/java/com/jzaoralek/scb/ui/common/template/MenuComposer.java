package com.jzaoralek.scb.ui.common.template;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Menubar;

import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEvent;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEventQueues;

/**
 * Project: sdat_web
 *
 * Created: 12. 5. 2016
 *
 * @author Ness | http://www.ness.com
 */
public class MenuComposer extends SelectorComposer<Component> {

    private static final long serialVersionUID = 1974207164739711580L;

    @Wire
    private Menubar menubar;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
    	EventQueues.lookup(ScbEventQueues.MENU_QUEUE.name() , EventQueues.DESKTOP, true).subscribe(new EventListener<Event>() {
			@Override
			public void onEvent(Event event) {
				if (event.getName().equals(ScbEvent.OPEN_MAIN_MENU_EVENT.name())) {
					openMenuItem((String) event.getData());
				}
			}
		});

    	super.doAfterCompose(comp);
    }

    private void openMenuItem(String itemId) {
        if (menubar == null || itemId == null) {
            return;
        }

        List<Component> list = menubar.getChildren();

        for (Component comp : list) {
            if (comp.getId().equals(itemId)) {
                HtmlBasedComponent hc = (HtmlBasedComponent) comp;
                hc.setClass("active");
            }
        }
    }

}
