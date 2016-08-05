package com.jzaoralek.scb.ui.common.template;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
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
public class MenuComposer extends SelectorComposer<Component> {

    private static final long serialVersionUID = 1974207164739711580L;

    @Wire
    private Menubar menubar;

    /**
     * Řešeno přes event queue, protože composer nedokáže registrovat
     * globální command
     *
     * @param evt
     */
//    @Subscribe("SDAT_QUEUE")
    public void eventQueueListener(Event evt) {
        if (evt.getName().equals("openMainMenuItem")) {
            openMenuItem((String) evt.getData());
        }
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
