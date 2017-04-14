package org.hyochan.dontech.models;

import java.util.Calendar;

/**
 * Created by hyochan on 2016. 10. 12..
 */

public class DaySet {
    private boolean isSaved;
    private boolean isToday;
    private boolean isPrevMon;
    private boolean isNextMon;
    private Calendar cal;

    public DaySet(boolean isSaved, boolean isToday, boolean isPrevMon, boolean isNextMon, Calendar cal) {
        this.isSaved = isSaved;
        this.isToday = isToday;
        this.isPrevMon = isPrevMon;
        this.isNextMon = isNextMon;
        this.cal = cal;
    }

    public boolean isSaved() {
        return isSaved;
    }

    public void setSaved(boolean saved) {
        isSaved = saved;
    }

    public boolean isToday() {
        return isToday;
    }

    public void setToday(boolean today) {
        isToday = today;
    }

    public boolean isPrevMon() {
        return isPrevMon;
    }

    public void setPrevMon(boolean prevMon) {
        isPrevMon = prevMon;
    }

    public boolean isNextMon() {
        return isNextMon;
    }

    public void setNextMon(boolean nextMon) {
        isNextMon = nextMon;
    }

    public Calendar getCal() {
        return cal;
    }

    public void setCal(Calendar cal) {
        this.cal = cal;
    }
}
