package org.hyochan.dontech.events;

/**
 * Created by hyochan on 2016. 10. 7..
 */

public class GagebuBookUpdateEvent {
    private String event;
    private String name;
    private String beforeName;

    /*
        event : add (Intent.ACTION_DEFAULT), update(Intent.ACTION_EDIT), delete(Intent.ACTION_DELETE)
        name : gagebu name
     */

    public GagebuBookUpdateEvent(String event, String name) {
        this.event = event;
        this.name = name;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBeforeName() {
        return beforeName;
    }

    public void setBeforeName(String beforeName) {
        this.beforeName = beforeName;
    }
}
