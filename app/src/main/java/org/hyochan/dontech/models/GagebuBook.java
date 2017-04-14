package org.hyochan.dontech.models;

/**
 * Created by hyochan on 2016. 10. 7..
 */

public class GagebuBook {
    int _id;
    String iconName;
    String name;
    String created;

    public GagebuBook(String iconName, String name, String created) {
        this.iconName = iconName;
        this.name = name;
        this.created = created;
    }

    public GagebuBook(int _id, String iconName, String name, String created) {
        this._id = _id;
        this.iconName = iconName;
        this.name = name;
        this.created = created;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
}
