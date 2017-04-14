package org.hyochan.dontech.events;

/**
 * Created by hyochan on 2016. 10. 8..
 */

public class DashboardSlideEvent {
    String event;
    String gagebuName;

    public DashboardSlideEvent(String event, String gagebuName) {
        this.event = event;
        this.gagebuName = gagebuName;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getGagebuName() {
        return gagebuName;
    }

    public void setGagebuName(String gagebuName) {
        this.gagebuName = gagebuName;
    }
}
