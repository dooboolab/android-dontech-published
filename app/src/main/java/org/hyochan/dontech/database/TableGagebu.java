package org.hyochan.dontech.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

import org.hyochan.dontech.functions.AppFunction;
import org.hyochan.dontech.models.DateManager;
import org.hyochan.dontech.models.Gagebu;
import org.hyochan.dontech.models.TotalIncomeConsume;
import org.hyochan.dontech.utils.MyLog;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by hyochan on 2016. 10. 9..
 */

public class TableGagebu {
    private final String TAG = "TableGagebu";
    private static TableGagebu mInstance;
    private SharedPreferences pref;
    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private Cursor cursor;
    private Context context;

    private final String RECORD0 = "_id";
    private final String RECORD1 = "name";
    private final String RECORD2 = "date";
    private final String RECORD3 = "category";
    private final String RECORD4 = "money";
    private final String RECORD5 = "detail";
    private final String RECORD6 = "address";
    private final String RECORD7 = "location_x";
    private final String RECORD8 = "location_y";
    private final String RECORD9 = "img";

    private TableGagebu(Context context){
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        this.context = context;
    }

    public static TableGagebu getInstance(Context context){
        if(mInstance == null) mInstance = new TableGagebu(context);
        return mInstance;
    }

    /** START : 가계부북 명이 변경되었을 때 작업 **/
    public int gagebuBookHasUpdated(String updatedName, String beforeName){

        MyLog.d(TAG, "gagebuBookHasUpdated");
        MyLog.d(TAG, "updatedName : " + updatedName + ", beforeName : " + beforeName);

        if(dbHelper == null){
            dbHelper = new DBHelper(context, DBHelper.DB_NAME, null, DBHelper.DB_VERSION);
        }

        db = dbHelper.getWritableDatabase();

        ContentValues recordValues = new ContentValues();
        recordValues.put(RECORD1, updatedName);


        String[] whereArgs = {beforeName};
        int rowAffected = db.update(DBHelper.TABLE_GAGEBU, recordValues, "name = ?", whereArgs);
        dbHelper.close();
        return rowAffected;
    }


    /** END : 가계부북 명이 변경되었을 때 작업 **/

    /** 가계부 북 명에 해당하는 가계부 가져오기 **/
    public ArrayList<Gagebu> selectGagebuInBook(String name){

        ArrayList<Gagebu> gagebus = new ArrayList<>();

        if(dbHelper == null){
            dbHelper = new DBHelper(context, DBHelper.DB_NAME, null, DBHelper.DB_VERSION);
        }

        db = dbHelper.getReadableDatabase();
        // 테이블 불러오기
        String[] args = {name};
        cursor = db.query(DBHelper.TABLE_GAGEBU, null, "name=?", args, null, null, "date");

        for(int i=0; i < cursor.getCount(); i++){
            cursor.moveToNext();
            gagebus.add(new Gagebu(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getInt(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getDouble(7),
                    cursor.getDouble(8),
                    cursor.getString(9)
            ));
        }

        cursor.close();
        dbHelper.close();

        return gagebus;
    }

    /** 가계부 북 명에 해당하는 가계부 가져오기 **/
    public ArrayList<Gagebu> selectGagebuInBookInMonth(String name){

        int year = DateManager.getInstance().getYear();
        int month = DateManager.getInstance().getMonth();
        long startDate = AppFunction.getInstance(context).getMilisecDate(year, month, 1);
        long endDate = AppFunction.getInstance(context).getMilisecDate(year, month+1, 1);

        ArrayList<Gagebu> gagebus = new ArrayList<>();

        if(dbHelper == null){
            dbHelper = new DBHelper(context, DBHelper.DB_NAME, null, DBHelper.DB_VERSION);
        }

        db = dbHelper.getReadableDatabase();
        // 테이블 불러오기
        String[] args = {name, String.valueOf(startDate), String.valueOf(endDate)};

        cursor = db.query(DBHelper.TABLE_GAGEBU, null, "name=? and date between ? and ?", args, null, null, "date asc");

        for(int i=0; i < cursor.getCount(); i++){
            cursor.moveToNext();
            gagebus.add(new Gagebu(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getInt(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getDouble(7),
                    cursor.getDouble(8),
                    cursor.getString(9)
            ));
        }

        cursor.close();
        dbHelper.close();

        return gagebus;
    }

    /** 엑셀용 가계부 전체 불러오기 **/
    public ArrayList<Gagebu> selectAll(){

        ArrayList<Gagebu> gagebus = new ArrayList<>();

        if(dbHelper == null){
            dbHelper = new DBHelper(context, DBHelper.DB_NAME, null, DBHelper.DB_VERSION);
        }

        db = dbHelper.getReadableDatabase();
        // 테이블 불러오기
        cursor = db.query(DBHelper.TABLE_GAGEBU, null, null, null, null, null, "name asc, date asc");

        for(int i=0; i < cursor.getCount(); i++){
            cursor.moveToNext();
            gagebus.add(new Gagebu(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getInt(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getDouble(7),
                    cursor.getDouble(8),
                    cursor.getString(9)
            ));
        }

        cursor.close();
        dbHelper.close();

        return gagebus;
    }

    /**********    GAGEBU QUERY    **********/
    public Gagebu selectGagebu(int _id){

        if(dbHelper == null){
            dbHelper = new DBHelper(context, DBHelper.DB_NAME, null, DBHelper.DB_VERSION);
        }

        db = dbHelper.getReadableDatabase();
        String[] args = {String.valueOf(_id)};
        cursor = db.query(DBHelper.TABLE_GAGEBU, null, "_id=?", args, null, null, null);
        cursor.moveToFirst();

        Gagebu gagebu = new Gagebu(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getInt(4),
                cursor.getString(5),
                cursor.getString(6),
                cursor.getDouble(7),
                cursor.getDouble(8),
                cursor.getString(9)
        );

        cursor.close();
        dbHelper.close();

        return gagebu;
    }

    public int insertGagebu(Gagebu gagebu){
        if(dbHelper == null){
            dbHelper = new DBHelper(context, DBHelper.DB_NAME, null, DBHelper.DB_VERSION);
        }

        db = dbHelper.getWritableDatabase();

        ContentValues recordValues = new ContentValues();
        recordValues.put(RECORD1, gagebu.getName());
        recordValues.put(RECORD2, gagebu.getDate());
        recordValues.put(RECORD3, gagebu.getCategory());
        recordValues.put(RECORD4, gagebu.getMoney());
        recordValues.put(RECORD5, gagebu.getDetail());
        recordValues.put(RECORD6, gagebu.getAddress());
        recordValues.put(RECORD7, gagebu.getLocationX());
        recordValues.put(RECORD8, gagebu.getLocationY());
        recordValues.put(RECORD9, gagebu.getImg());

        int insertId = (int) db.insert(DBHelper.TABLE_GAGEBU, null, recordValues);

        dbHelper.close();

        return insertId;
    }

    public int updateGagebu(int _id, Gagebu gagebu){
        if(dbHelper == null){
            dbHelper = new DBHelper(context, DBHelper.DB_NAME, null, DBHelper.DB_VERSION);
        }

        db = dbHelper.getWritableDatabase();

        ContentValues recordValues = new ContentValues();
        recordValues.put(RECORD1, gagebu.getName());
        recordValues.put(RECORD2, gagebu.getDate());
        recordValues.put(RECORD3, gagebu.getCategory());
        recordValues.put(RECORD4, gagebu.getMoney());
        recordValues.put(RECORD5, gagebu.getDetail());
        recordValues.put(RECORD6, gagebu.getAddress());
        recordValues.put(RECORD7, gagebu.getLocationX());
        recordValues.put(RECORD8, gagebu.getLocationY());
        recordValues.put(RECORD9, gagebu.getImg());

        MyLog.d("hyochan", "_id(" + _id + ")update money to " + gagebu.getMoney());

        String[] whereArgs = {String.valueOf(_id)};
        int rowAffected = db.update(DBHelper.TABLE_GAGEBU, recordValues, "_id = ?", whereArgs);
        dbHelper.close();
        return rowAffected;
    }

    public int deleteGagebu(int _id){
        if(dbHelper == null){
            dbHelper = new DBHelper(context, DBHelper.DB_NAME, null, DBHelper.DB_VERSION);
        }

        db = dbHelper.getWritableDatabase();

        String[] whereArgs = {String.valueOf(_id)};
        int rowAffected = db.delete(DBHelper.TABLE_GAGEBU,  "_id = ?", whereArgs);
        dbHelper.close();
        return rowAffected;
    }

    /** 가계부 북에 있는 가계부 지우기 */
    public void deleteGagebuInBook(String name){
        if(dbHelper == null){
            dbHelper = new DBHelper(context, DBHelper.DB_NAME, null, DBHelper.DB_VERSION);
        }

        db = dbHelper.getWritableDatabase();

        String[] whereArgs = {name};
        db.delete(DBHelper.TABLE_GAGEBU,  "name = ?", whereArgs);
        dbHelper.close();
    }

    /**********   이월 금액 QUERY   **********/
    public int selectBalanceCarriedOver(String name){

        if(dbHelper == null){
            dbHelper = new DBHelper(context, DBHelper.DB_NAME, null, DBHelper.DB_VERSION);
        }

        db = dbHelper.getReadableDatabase();

        int year = DateManager.getInstance().getYear();
        int month = DateManager.getInstance().getMonth();
        long startDate = AppFunction.getInstance(context).getMilisecDate(year, month, 1);

        int balance=0;

        String[] args = {name, String.valueOf(startDate)};
        cursor = db.rawQuery("select sum(money) from " + DBHelper.TABLE_GAGEBU +
                " where name=? and (date<?)", args);
        if(cursor.moveToFirst())
            balance = cursor.getInt(0);


        MyLog.d(TAG, "balance : " + balance);

        cursor.close();
        dbHelper.close();

        return balance;
    }

    /** 총액 */
    public TotalIncomeConsume getThisMonthTotalIncomConsume(String name, long startDate, long endDate){

        if(dbHelper == null){
            dbHelper = new DBHelper(context, DBHelper.DB_NAME, null, DBHelper.DB_VERSION);
        }

        db = dbHelper.getReadableDatabase();

        /** TEST **/
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTimeInMillis(startDate);
        cal1.set(Calendar.HOUR, 0);
        cal1.set(Calendar.MINUTE, 0);
        cal1.set(Calendar.SECOND, 0);
        MyLog.d(TAG, "day of month : " + cal1.get(Calendar.DAY_OF_MONTH));
        cal2.setTimeInMillis(endDate);
        cal2.add(Calendar.DATE, -1);
        cal2.set(Calendar.HOUR, 23);
        cal2.set(Calendar.MINUTE, 59);
        cal2.set(Calendar.SECOND, 59);
        MyLog.d(TAG, "day of month : " + cal2.get(Calendar.DAY_OF_MONTH));
        /** END TEST **/

        String[] cols = {"money"};
        String[] args = {name, String.valueOf(cal1.getTimeInMillis()), String.valueOf(cal2.getTimeInMillis())};

        cursor = db.query(DBHelper.TABLE_GAGEBU, cols, "name=? and (date between ? and ?)", args, null, null, null);

        int total = 0;
        int income = 0;
        int consume = 0;

        for(int i=0; i < cursor.getCount(); i++){
            cursor.moveToNext();
            total += cursor.getInt(0);
            if(cursor.getInt(0) < 0 ) consume += cursor.getInt(0);
            else if (cursor.getInt(0) > 0) income += cursor.getInt(0);
        }
        cursor.close();
        dbHelper.close();

        return new TotalIncomeConsume(total, income, consume);
    }

    /** 날짜별 총액, 수입액, 지출액  **/
    public TotalIncomeConsume getThisDayTotalIncomConsume(String name, long startDate){

        if(dbHelper == null){
            dbHelper = new DBHelper(context, DBHelper.DB_NAME, null, DBHelper.DB_VERSION);
        }

        db = dbHelper.getReadableDatabase();

        /** TEST **/
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(startDate);
        cal1.set(Calendar.HOUR, 0);
        cal1.set(Calendar.MINUTE, 0);
        cal1.set(Calendar.SECOND, 0);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTimeInMillis(startDate);
        cal2.set(Calendar.HOUR, 23);
        cal2.set(Calendar.MINUTE, 59);
        cal1.set(Calendar.SECOND, 59);

        /** END TEST **/

        String[] cols = {"money"};
        String[] args = {name, String.valueOf(cal1.getTimeInMillis()), String.valueOf(cal2.getTimeInMillis())};

        cursor = db.query(DBHelper.TABLE_GAGEBU, cols, "name=? and (date between ? and ?)", args, null, null, null);

        int total = 0;
        int income = 0;
        int consume = 0;

        for(int i=0; i < cursor.getCount(); i++){
            cursor.moveToNext();
            total += cursor.getInt(0);
            if(cursor.getInt(0) < 0 ) consume += cursor.getInt(0);
            else if (cursor.getInt(0) > 0) income += cursor.getInt(0);
        }
        cursor.close();
        dbHelper.close();

        return new TotalIncomeConsume(total, income, consume);
    }

    /**
     * 모든 테이블 다 지울 때 사용
     */
    public void deleteAll(){
        if(dbHelper == null){
            dbHelper = new DBHelper(context, DBHelper.DB_NAME, null, DBHelper.DB_VERSION);
        }
        db = dbHelper.getWritableDatabase();

        db.delete(DBHelper.TABLE_GAGEBU,  null , null);
        dbHelper.close();
    }
}
