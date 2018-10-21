package com.jzaoralek.scb.ui.common.template;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.Executions;

import com.jzaoralek.scb.ui.common.security.SecurityVM;
import com.jzaoralek.scb.ui.common.utils.ConfigUtil;

/**
 *
 * @author P3400343
 *
 */
public class MenuVM extends SecurityVM {

    @Command
    public void hrefCmd(@BindingParam("href") String href) {
        Executions.sendRedirect(href);
    }
    
    public String getOrgName() {
    	return ConfigUtil.getOrgName(configurationService);
    }

}
