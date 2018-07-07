package com.jzaoralek.scb.ui.common.template;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.Executions;

import com.jzaoralek.scb.ui.common.utils.ConfigUtil;
import com.jzaoralek.scb.ui.common.vm.BaseVM;

/**
 *
 * @author P3400343
 *
 */
public class MenuVM extends BaseVM {

    @Command
    public void hrefCmd(@BindingParam("href") String href) {
        Executions.sendRedirect(href);
    }
    
    public String getOrgName() {
    	return ConfigUtil.getOrgName(configurationService);
    }

}
