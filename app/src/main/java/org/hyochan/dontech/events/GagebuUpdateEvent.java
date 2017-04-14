package org.hyochan.dontech.events;

import org.hyochan.dontech.models.Gagebu;

/**
 * Created by hyochan on 2016. 10. 10..
 */

public class GagebuUpdateEvent {
    String action;
    Gagebu gagebu;

    public GagebuUpdateEvent(String action, Gagebu gagebu) {
        this.action = action;
        this.gagebu = gagebu;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Gagebu getGagebu() {
        return gagebu;
    }

    public void setGagebu(Gagebu gagebu) {
        this.gagebu = gagebu;
    }
}
