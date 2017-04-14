package org.hyochan.dontech.functions;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.hyochan.dontech.R;
import org.hyochan.dontech.global_variables.MyString;
import org.hyochan.dontech.preferences.AppPref;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by hyochan on 2016. 10. 10..
 */

public class AppFunction {
    private static final String TAG = "AppFunction";

    private static AppFunction appFunction;
    private SharedPreferences sharedPreferences;
    private Context context;

    private AppFunction(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.context = context;
    }

    public static AppFunction getInstance(Context context) {
        if (appFunction == null) appFunction = new AppFunction(context);
        return appFunction;
    }

    /**
     * 선택된 가계부 북 가져오기
     */
    public String getCurrentGagebuBook(){
        return AppPref.getInstance(context).getValue(MyString.PREF_SELECTED_GAGEBU, context.getString(R.string.my_gagebu));
    }

    /**
     * 현재 선택된 가계부 북 업데이트
     */
    public void setCurrentGagebuBook(String name){
        AppPref.getInstance(context).put(MyString.PREF_SELECTED_GAGEBU, name);
    }

    /**
     * param : year, month, day
     * result : miliseconds
     */
    public long getMilisecDate(int year, int month, int day){
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, 0 ,0 ,0);
        // calendar.add(Calendar.DATE, -1);
        return calendar.getTimeInMillis();
    }

    /**
     * param : year, month, day
     * result : miliseconds
     */
    public long getMilisecDay(int year, int month, int day){
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return calendar.getTimeInMillis();
    }

    public String putCommasToMoney(int money){
        return String.format(Locale.KOREA, "%,d", Math.abs(money));
    }

    /**
     * Convert to won string
     * param : money (10000)
     * result : 10,000원
     */
    public String convertMoneyToWonString(int money){
        return context.getString(R.string.money_won, String.format(Locale.KOREA, "%,d", Math.abs(money)));
    }
}
