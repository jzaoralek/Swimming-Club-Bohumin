package com.jzaoralek.scb.ui.common.utils;

import org.springframework.util.StringUtils;
import org.zkoss.zk.ui.HtmlBasedComponent;

/**
 * Project: scb-ui-zk
 *
 * Created: 21. 10. 2018
 *
 * @author Ales Wojnar | ales@wojnar.cz | http://ales.wojnar.cz
 */
public class ComponentUtils {

    /**
     * Nstaví komponentě css třídu (zachová ostatní již nastavené css třídy)
     *
     * @param comp
     * @param sclass
     */
    public static void addSclass(HtmlBasedComponent comp, String sclass) {
        removeSclass(comp, sclass);

        String sc = comp.getSclass();
        if (StringUtils.isEmpty(sc)) {
            comp.setSclass(sclass);
        } else {
            comp.setSclass(sc + " " + sclass);
        }
    }

    /**
     * Odstraní z komponenty danou css třídu (ostatní zachová)
     *
     * @param comp
     * @param sclass
     */
    public static void removeSclass(HtmlBasedComponent comp, String sclass) {
        if (comp == null || StringUtils.isEmpty(comp.getSclass())) {
            return;
        }
        String sc = comp.getSclass();
        if (!sc.contains(" ")) {
            if (sc.equals(sclass)) {
                comp.setSclass(null);
                return;
            }
        } else if (sc.startsWith(sclass + " ")) {
            sc = sc.substring(sclass.length() + 1);
        } else if (sc.endsWith(" " + sclass)) {
            sc = sc.substring(0, sc.length() - sclass.length() - 1);
        } else if (sc.contains(" " + sclass + " ")) {
            sc = sc.replace(" " + sclass + " ", " ");
        }
        comp.setSclass(sc);
    }
}
