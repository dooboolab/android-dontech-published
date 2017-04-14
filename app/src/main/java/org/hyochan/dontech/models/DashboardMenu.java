package org.hyochan.dontech.models;

/**
 * Created by hyochan on 2016. 10. 6..
 */

public class DashboardMenu {
    private int viewType;
    private String imgName;
    private String strTxt;
    private boolean selected;

    public DashboardMenu(int viewType) {
        this.viewType = viewType;
    }

    public DashboardMenu(int viewType, String strTxt) {
        this.viewType = viewType;
        this.strTxt = strTxt;
    }

    public DashboardMenu(int viewType, String imgName, String strTxt, boolean selected) {
        this.viewType = viewType;
        this.imgName = imgName;
        this.strTxt = strTxt;
        this.selected = selected;
    }

    public DashboardMenu(int viewType, String imgName, String strTxt) {
        this.viewType = viewType;
        this.imgName = imgName;
        this.strTxt = strTxt;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public String getStrTxt() {
        return strTxt;
    }

    public void setStrTxt(String strTxt) {
        this.strTxt = strTxt;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
