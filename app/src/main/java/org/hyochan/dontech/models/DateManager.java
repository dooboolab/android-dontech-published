package org.hyochan.dontech.models;

import org.hyochan.dontech.adapters.GridDayAdapter;
import org.hyochan.dontech.utils.MyLog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by hyochan on 2016-02-17.
 */
public class DateManager {

    private final String TAG = "DateManager";

    private int year;
    private int month;

    private static DateManager dateManager;

    public DateManager(){

    }

    public static DateManager getInstance(){
        if(dateManager == null) {
            dateManager = new DateManager();
            DateManager.getInstance().setYear(Calendar.getInstance().get(Calendar.YEAR));
            DateManager.getInstance().setMonth(Calendar.getInstance().get(Calendar.MONTH));
        }
        return dateManager;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    private DaySet generateDaySet(Calendar cal, boolean isPreMon, boolean isNextMon){

        DaySet daySet = new DaySet(false, false, isPreMon, isNextMon, cal);

        if( cal.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR) &&
                cal.get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH) &&
                cal.get(Calendar.DAY_OF_MONTH) == Calendar.getInstance().get(Calendar.DAY_OF_MONTH)){
            daySet.setToday(true);
            // MyLog.d(TAG, "date is today : " + cl.get(Calendar.YEAR) + "-" + cl.get(Calendar.MONTH)+1 + "-" + cl.get(Calendar.DATE));
        }
        return daySet;
    }

    public void fillCalendar(GridDayAdapter gridDayAdapter) {
        ArrayList<DaySet> arrDay = new ArrayList<>();
        Date current = new Date(year - 1900, month, 1);
        int day = current.getDay(); // 요일로 처음 날짜 계산
        MyLog.d(TAG, "요일 : " + day);

        /** 이전 달 날짜 출력 **/
        for (int i = 0; i < day; i++) {
            Calendar cal = Calendar.getInstance();
            cal.set(year, month - 1, 1);
            cal.add(Calendar.DATE, -(day-i));
            arrDay.add(generateDaySet(cal, true, false));
        }

        current.setDate(32); // 32일까지 입력하면 1일로 바꿔준다.
        int last = 32 - current.getDate();
        // Calendar today = Calendar.getInstance();

        MyLog.d(TAG, "current : " + current);
        MyLog.d(TAG, "month : " + month);
        MyLog.d(TAG, "last : " + last);

        /** 이번 달 날짜 출력 **/
        for (int i = 1; i <= last; i++) {
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, i);
            arrDay.add(generateDaySet(cal, false, false));
        }
        /** 다음 달 날짜 출력 **/
        int nextDay = 42 - arrDay.size();
        for(int i=1; i<=nextDay; i++){
            Calendar cal = Calendar.getInstance();
            cal.set(year, month+1, i);
            arrDay.add(generateDaySet(cal, false, true));
        }
        MyLog.i(TAG, "size : " + arrDay.size());



        gridDayAdapter.drawAdapter(arrDay);
    }

}
