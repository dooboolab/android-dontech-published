package org.hyochan.dontech.events;

/**
 * Created by hyochan on 2016. 10. 11..
 * Usage : MonthlyFragment, GagebuFragment
 * From : MainActivity
 */

public class GagebuBookSelectedEvent {
    String name;

    public GagebuBookSelectedEvent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
