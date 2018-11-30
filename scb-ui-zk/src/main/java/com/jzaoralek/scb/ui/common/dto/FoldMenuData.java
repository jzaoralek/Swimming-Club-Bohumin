package com.jzaoralek.scb.ui.common.dto;

/**
 * Project: scb-ui-zk
 *
 * Created: 30. 11. 2018
 *
 * @author Ales Wojnar | ales@wojnar.cz | http://ales.wojnar.cz
 */
public class FoldMenuData {

    private String size;
    private boolean doFold;

    public FoldMenuData(String size, boolean doFold) {
        this.size = size;
        this.doFold = doFold;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public boolean isDoFold() {
        return doFold;
    }

    public void setDoFold(boolean doFold) {
        this.doFold = doFold;
    }

}
