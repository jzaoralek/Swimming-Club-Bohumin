package com.jzaoralek.scb.ui.common.template;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.A;

import com.jzaoralek.scb.ui.common.utils.ComponentUtils;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEvent;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEventQueues;
import com.jzaoralek.scb.ui.common.utils.WebUtils;

/**
 * Project: scb-ui-zk
 *
 * Created: 21. 10. 2018
 *
 * @author Ales Wojnar | ales@wojnar.cz | http://ales.wojnar.cz
 */
public class SideMenuComposer extends SelectorComposer<Component> {

    private static final long serialVersionUID = 8422985903729979203L;

    @Wire
    private HtmlBasedComponent menuWrapper;

    public enum ScbMenuItem {
        HOME,
        PRIHLASKA,
        SEZNAM_PRIHLASEK,
        SEZNAM_KURZU,
        SEZNAM_UCASTNIKU_AT,
        SEZNAM_UCASTNIKU_U,
        UZIVATEL,
        SENAM_UZIVATELU,
        PLATBY,
        ZPRAVY,
        NASTAVENI;
    }

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        init();

        EventQueueHelper.queueLookup(ScbEventQueues.SIDE_MENU_QUEUE).subscribe(ScbEvent.SET_MENU_SELECTED, data -> {
            selectMenu((ScbMenuItem) data);
        });
    }

    /**
     * 
     */
    private void init() {
        String path = WebUtils.buildCustCtxUri(WebUtils.getRequestPath());
        for (Component child : menuWrapper.getChildren()) {
            if (child instanceof A) {
                A anchor = (A) child;
                String href = anchor.getHref();
                if (href != null && (path.endsWith(href) || (path.equals(WebUtils.buildCustCtxUri("/pages/index.zul")) && href.equals("/")))) {
                    ComponentUtils.addSclass(anchor, "scb-menu-active");
                }
            }
        }
    }

    /**
     * 
     * @param menuItemId
     */
    private void selectMenu(ScbMenuItem menuItem) {
        for (Component child : menuWrapper.getChildren()) {
            if (child instanceof A) {
                A anchor = (A) child;
                if (menuItem.name().equals(anchor.getId())) {
                    ComponentUtils.addSclass(anchor, "scb-menu-active");
                    return;
                }
            }
        }
    }
}
