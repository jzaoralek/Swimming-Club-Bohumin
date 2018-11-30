package com.jzaoralek.scb.ui.common.template;

import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zul.West;

import com.jzaoralek.scb.ui.common.utils.ComponentUtils;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEvent;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEventQueues;
import com.jzaoralek.scb.ui.common.utils.WebUtils;

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
    private static final String DISPLAY_MODE = "DISPLAY_MODE";
    private static final String MENU_STATE = "MENU_STATE";
    private static final String SIZE_FULL = "230px";
    private static final String SIZE_FOLDED = "55px";
    private static final String SIZE_HIDDEN = "0px";

    private West panel;

    public enum DisplayMode {
        FULL,
        SMALL,
        MOBILE;
    }

    private enum MenuState {
        OPEN,
        CLOSED;

        /**
         * Další stav
         * @return
         */
        public MenuState next() {
            if (this == OPEN) {
                return CLOSED;
            } else {
                return OPEN;
            }
        }

        public boolean isOpen() {
            if (this == OPEN) {
                return true;
            }
            return false;
        }

    }

    @Override
    public void doAfterCompose(West comp) throws Exception {
        panel = comp;
        init();

        EventQueueHelper.queueLookup(ScbEventQueues.SIDE_MENU_QUEUE).subscribe(ScbEvent.SIDE_MENU_FOLD_EVENT, data -> {
            changeFold();
        });

        EventQueueHelper.queueLookup(ScbEventQueues.SIDE_MENU_QUEUE).subscribe(ScbEvent.MOBILE_MODE_EVENT, data -> {
            WebUtils.setSessAtribute(DISPLAY_MODE, data);
            renderMenu();
        });
    }

    /**
     * 
     */
    private void init() {
        if (loadDisplayMode() == DisplayMode.MOBILE) {
            WebUtils.setSessAtribute(MENU_STATE, MenuState.CLOSED);
        }
        renderMenu();
    }

    /**
     * 
     */
    private void renderMenu() {
        MenuState state = loadMenuState();
        DisplayMode displayMode = loadDisplayMode();

        if (displayMode == DisplayMode.MOBILE) {
            if (state.isOpen()) {
                renderFolded();
            } else {
                renderHidden();
            }
        } else {
            if (state.isOpen()) {
                renderFull();
            } else {
                renderFolded();
            }
        }
    }

    /**
     * 
     */
    private void renderHidden() {
        panel.setWidth(SIZE_HIDDEN);
        ComponentUtils.addSclass(panel, FOLD_CLASS);
    }

    /**
     * 
     */
    private void renderFolded() {
        panel.setWidth(SIZE_FOLDED);
        ComponentUtils.addSclass(panel, FOLD_CLASS);
    }

    /**
     * 
     */
    private void renderFull() {
        panel.setWidth(SIZE_FULL);
        ComponentUtils.removeSclass(panel, FOLD_CLASS);
    }

    /**
     * Uživatel kliknul na ikonu sbalení/rozbalení menu
     */
    private void changeFold() {
        MenuState state = loadMenuState();
        state = state.next();
        WebUtils.setSessAtribute(MENU_STATE, state);
        renderMenu();
    }

    /**
     * 
     * @return
     */
    private MenuState loadMenuState() {
        MenuState state = (MenuState) WebUtils.getSessAtribute(MENU_STATE);
        DisplayMode displayMode = loadDisplayMode();
        if (state == null) {
            if (displayMode == DisplayMode.FULL) {
                state = MenuState.OPEN;
            } else {
                state = MenuState.CLOSED;
            }
        }
        return state;
    }

    /**
     * 
     * @return
     */
    private DisplayMode loadDisplayMode() {
        return (DisplayMode) WebUtils.getSessAtribute(DISPLAY_MODE);
    }

}
