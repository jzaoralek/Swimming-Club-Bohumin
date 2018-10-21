package com.jzaoralek.scb.ui.common.template;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.A;

import com.jzaoralek.scb.ui.common.utils.ComponentUtils;
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

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        String path = WebUtils.getRequestPath();
        for (Component child : menuWrapper.getChildren()) {
            if (child instanceof A) {
                A anchor = (A) child;
                String href = anchor.getHref();
                if (href != null && (path.endsWith(href) || (path.equals("/pages/index.zul") && href.equals("/")))) {
                    ComponentUtils.addSclass(anchor, "scb-menu-active");
                }
            }
        }
        
    }
}
