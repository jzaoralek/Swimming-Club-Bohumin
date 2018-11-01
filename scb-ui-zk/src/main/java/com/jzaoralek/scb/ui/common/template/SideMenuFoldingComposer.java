package com.jzaoralek.scb.ui.common.template;

import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zul.West;

import com.jzaoralek.scb.ui.common.utils.ComponentUtils;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEvent;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEventQueues;

/**
 * Project: scb-ui-zk
 *
 * Created: 1. 11. 2018
 *
 * @author Ales Wojnar | ales@wojnar.cz | http://ales.wojnar.cz
 */
public class SideMenuFoldingComposer extends SelectorComposer<West>{

    private static final long serialVersionUID = 1328765241564L;
    private static final String FOLD_CLASS = "scb-side-menu-wrapper-folded";

    private West panel;

    @Override
    public void doAfterCompose(West comp) throws Exception {
        panel = comp;

        EventQueueHelper.queueLookup(ScbEventQueues.SIDE_MENU_QUEUE).subscribe(ScbEvent.SIDE_MENU_FOLD_EVENT, data -> {
            foldSidePanel((Boolean) data);
        });
    }

    /**
     * 
     * @param doFold
     */
    private void foldSidePanel(Boolean doFold) {
        if (doFold) {
            panel.setWidth("55px");
            ComponentUtils.addSclass(panel, FOLD_CLASS);
        } else {
            panel.setWidth("230px");
            ComponentUtils.removeSclass(panel, FOLD_CLASS);
        }
    }
}
