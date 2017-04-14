package org.hyochan.dontech.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hyochan on 2016. 10. 9..
 */

public class GagebuCategory {

    private int _id;
    private String category;
    private String iconName;
    private boolean isIncome;

    public GagebuCategory(int _id, String category, String iconName, boolean isIncome) {
        this._id = _id;
        this.category = category;
        this.iconName = iconName;
        this.isIncome = isIncome;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public boolean isIncome() {
        return isIncome;
    }

    public void setIncome(boolean income) {
        isIncome = income;
    }
}
