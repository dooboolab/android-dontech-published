package org.hyochan.dontech.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

import org.hyochan.dontech.models.GagebuBook;
import org.hyochan.dontech.utils.MyLog;

import java.util.ArrayList;

/**
 * Created by hyochan on 2014. 7. 29..
 */
public class TableGagebuBook {

    private final String TAG = "TableGagebuBook";
    private static TableGagebuBook mInstance;
    private SharedPreferences pref;
    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private Cursor cursor;
    private Context context;

        private TableGagebuBook(Context context){
            pref = PreferenceManager.getDefaultSharedPreferences(context);
            this.context = context;
        }

    public static TableGagebuBook getInstance(Context context){
        if(mInstance == null) mInstance = new TableGagebuBook(context);
        return mInstance;
    }


    /** 내 가계부 리스트 불러오기 **/
    public ArrayList<GagebuBook> selectMyGagebuBooks() {

        if(dbHelper == null){
            dbHelper = new DBHelper(context, DBHelper.DB_NAME, null, DBHelper.DB_VERSION);
        }

        db = dbHelper.getReadableDatabase();
        cursor = db.query(DBHelper.TABLE_GAGEBU_BOOK, null, null, null, null, null, "created");

        ArrayList<GagebuBook> arrGagebu = new ArrayList<>();
        for(int i=0; i < cursor.getCount(); i++){
            cursor.moveToNext();
            arrGagebu.add(new GagebuBook(cursor.getString(1), cursor.getString(2), cursor.getString(3)));
        }

        cursor.close();
        dbHelper.close();

        return arrGagebu;
    }

    /** 내 가계부 계수 불러오기 **/
    public int countMyGagebus(){

        int cnt;

        if(dbHelper == null){
            dbHelper = new DBHelper(context, DBHelper.DB_NAME, null, DBHelper.DB_VERSION);
        }

        db = dbHelper.getReadableDatabase();
        cursor = db.rawQuery("select count(*) from " + DBHelper.TABLE_GAGEBU_BOOK, null);
        cursor.moveToFirst();
        cnt = cursor.getInt(0);

        cursor.close();
        dbHelper.close();

        MyLog.d(TAG, "cnt : " + cnt);

        return cnt;
    }

    /** 가계부 중복이름 확인하기 **/
    public int checkMyGagebuName(String updateName, String currentName){
        int cnt;

        if(dbHelper == null){
            dbHelper = new DBHelper(context, DBHelper.DB_NAME, null, DBHelper.DB_VERSION);
        }

        db = dbHelper.getReadableDatabase();
        String[] cols = {"count(*)"};
        String[] args = {updateName, currentName};
        cursor = db.query(DBHelper.TABLE_GAGEBU_BOOK, cols, "name = ? and name != ?", args, null, null, "created");

        cursor.moveToFirst();
        cnt = cursor.getInt(0);

        cursor.close();
        dbHelper.close();

        MyLog.d(TAG, "cnt : " + cnt);

        return cnt;
    }

    /** 내 가계부 불러오기 **/
    public GagebuBook selecyMyGagebu(String name){
        if(dbHelper == null){
            dbHelper = new DBHelper(context, DBHelper.DB_NAME, null, DBHelper.DB_VERSION);
        }

        db = dbHelper.getReadableDatabase();
        String[] args = {String.valueOf(name)};
        cursor = db.query(DBHelper.TABLE_GAGEBU_BOOK, null, "name=?", args, null, null, "created");

        cursor.moveToNext();
        GagebuBook gagebuGroup = new GagebuBook(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));

        cursor.close();
        dbHelper.close();

        return gagebuGroup;
    }

    /** 가계부 북 추가하기 **/
    public int insertMyGagebu(GagebuBook gagebuBook){
        if(dbHelper == null){
            dbHelper = new DBHelper(context, DBHelper.DB_NAME, null, DBHelper.DB_VERSION);
        }

        db = dbHelper.getWritableDatabase();

        ContentValues recordValues = new ContentValues();
        recordValues.put("icon_name", gagebuBook.getIconName());
        recordValues.put("name", gagebuBook.getName());
        recordValues.put("created", gagebuBook.getCreated());

        int insertId = (int) db.insert(DBHelper.TABLE_GAGEBU_BOOK, null, recordValues);

        dbHelper.close();

        return insertId;
    }

    /** 가계부 수정하기 **/
    public int updateMyGagebu(int _id, GagebuBook gagebuBook){
        if(dbHelper == null){
            dbHelper = new DBHelper(context, DBHelper.DB_NAME, null, DBHelper.DB_VERSION);
        }
        db = dbHelper.getWritableDatabase();

        ContentValues recordValues = new ContentValues();
        recordValues.put("icon_name", gagebuBook.getIconName());
        recordValues.put("name", gagebuBook.getName());
        recordValues.put("created", gagebuBook.getCreated());

        String[] whereArgs = {String.valueOf(_id)};
        int rowAffected = db.update(DBHelper.TABLE_GAGEBU_BOOK, recordValues,  "_id=?", whereArgs);
        dbHelper.close();
        return rowAffected;

    }

    /** 가계부 삭제하기 **/
    public int deleteMyGagebu(int _id){
        if(dbHelper == null){
            dbHelper = new DBHelper(context, DBHelper.DB_NAME, null, DBHelper.DB_VERSION);
        }

        db = dbHelper.getWritableDatabase();

        String[] whereArgs = {String.valueOf(_id)};
        int rowAffected = db.delete(DBHelper.TABLE_GAGEBU_BOOK, "_id=?", whereArgs);
        dbHelper.close();
        return rowAffected;
    }

    /**
     * 모든 테이블 다 지울 때 사용
     */
    public void deleteAll(){
        if(dbHelper == null){
            dbHelper = new DBHelper(context, DBHelper.DB_NAME, null, DBHelper.DB_VERSION);
        }
        db = dbHelper.getWritableDatabase();

        db.delete(DBHelper.TABLE_GAGEBU_BOOK,  null , null);
        dbHelper.close();
    }
}
