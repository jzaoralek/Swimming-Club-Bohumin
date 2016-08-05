package com.jzaoralek.scb.ui.common.template;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.Executions;

/**
 *
 * @author P3400343
 *
 */
public class MenuVM {

    @Command
    public void hrefCmd(@BindingParam("href") String href) {
        Executions.sendRedirect(href);
    }

}
