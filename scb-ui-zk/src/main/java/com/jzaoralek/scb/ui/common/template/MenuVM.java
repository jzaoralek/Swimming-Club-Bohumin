package com.jzaoralek.scb.ui.common.template;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Div;

import com.jzaoralek.scb.ui.common.security.SecurityVM;
import com.jzaoralek.scb.ui.common.utils.ConfigUtil;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEvent;
import com.jzaoralek.scb.ui.common.utils.WebUtils;

/**
 *
 * @author P3400343
 *
 */
public class MenuVM extends SecurityVM {

    public static final String SIDE_MENU_FOLDED = "SIDE_MENU_FOLDED";
    private static final String FOLDED_CLASS = "app-logo-wrapper-folded";

    private boolean menuFolded;

    @Wire
    private Div appLogoWrapper;

    @AfterCompose
    public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
        Selectors.wireComponents(view, this, false);
        initAppLogoSize();
    }

    @Command
    public void menuFoldCmd() {
        loadMenuFolded();
        if (menuFolded) {
            changeMenuFold("230px", false, true);
        } else {
            changeMenuFold("55px", true, true);
        }
    }

    @Command
    public void hrefCmd(@BindingParam("href") String href) {
        Executions.sendRedirect(href);
    }
    
    public String getOrgName() {
    	return ConfigUtil.getOrgName(configurationService);
    }

    /**
     * 
     * @param size
     * @param doFold
     */
    private void changeMenuFold(String size, boolean doFold, boolean notifySideBar) {
        /*
        if (doFold) {
            ComponentUtils.addSclass(appLogoWrapper, FOLDED_CLASS);
        } else {
            ComponentUtils.removeSclass(appLogoWrapper, FOLDED_CLASS);
        }
        appLogoWrapper.setWidth(size);
        */
        WebUtils.setSessAtribute(SIDE_MENU_FOLDED, doFold);
        menuFolded = doFold;
        if (notifySideBar) {
            EventQueueHelper.publish(ScbEvent.SIDE_MENU_FOLD_EVENT, doFold);
        }
    }

    /**
     * 
     */
    private void initAppLogoSize() {
        loadMenuFolded();
        changeMenuFold("55px", menuFolded, true);
    }

    /**
     * 
     */
    private void loadMenuFolded() {
        Boolean folded = (Boolean) WebUtils.getSessAtribute(SIDE_MENU_FOLDED);
        if (folded != null && folded) {
            menuFolded = true;
        } else {
            menuFolded = false;
        }
    }

}
