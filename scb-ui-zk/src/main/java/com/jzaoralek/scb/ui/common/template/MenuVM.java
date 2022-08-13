package com.jzaoralek.scb.ui.common.template;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;

import com.jzaoralek.scb.ui.common.security.SecurityVM;
import com.jzaoralek.scb.ui.common.utils.ConfigUtil;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper;
import com.jzaoralek.scb.ui.common.utils.EventQueueHelper.ScbEvent;

/**
 *
 * @author P3400343
 *
 */
public class MenuVM extends SecurityVM {

    @AfterCompose
    public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
        Selectors.wireComponents(view, this, false);
    }

    @Command
    public void menuFoldCmd() {
        EventQueueHelper.publish(ScbEvent.SIDE_MENU_FOLD_EVENT, null);
    }
    
    public String getOrgName() {
    	return ConfigUtil.getOrgName(configurationService);
    }
}
