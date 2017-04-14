package org.hyochan.dontech.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

import org.hyochan.dontech.models.GagebuBook;
import org.hyochan.dontech.models.GagebuCategory;
import org.hyochan.dontech.utils.MyLog;

import java.util.ArrayList;

/**
 * Created by hyochan on 2016. 10. 9..
 */

public class TableGagebuCategory {
    private final String TAG = "TableGagebuCategory";
    private static TableGagebuCategory mInstance;
    private SharedPreferences pref;
    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private Cursor cursor;
    private Context context;

    private TableGagebuCategory(Context context){
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        this.context = context;
    }

    public static TableGagebuCategory getInstance(Context context){
        if(mInstance == null) mInstance = new TableGagebuCategory(context);
        return mInstance;
    }
    /** 내 가계부 리스트 불러오기 **/
    public ArrayList<GagebuCategory> selectCategories(boolean isIncome) {

        int num = isIncome ? 1 : 0;

        if(dbHelper == null){
            dbHelper = new DBHelper(context, DBHelper.DB_NAME, null, DBHelper.DB_VERSION);
        }

        db = dbHelper.getReadableDatabase();
        cursor = db.query(DBHelper.TABLE_GAGEBU_CATEGORY, null, "is_income=" + num, null, null, null, null);

        ArrayList<GagebuCategory> arrGagebuCategory = new ArrayList<>();
        for(int i=0; i < cursor.getCount(); i++){
            cursor.moveToNext();
            arrGagebuCategory.add(new GagebuCategory(
                    cursor.getInt(0),
                    cursor.getString(1), cursor.getString(2), (cursor.getInt(3) != 0)));
        }

        cursor.close();
        dbHelper.close();

        MyLog.d(TAG, "arrGagebuCategorySize : " + arrGagebuCategory.size());

        return arrGagebuCategory;
    }

    /** 테이블 가계부 추가하기 **/
    public int insertGagebuCategories(GagebuCategory gagebuCategory){
        if(dbHelper == null){
            dbHelper = new DBHelper(context, DBHelper.DB_NAME, null, DBHelper.DB_VERSION);
        }

        db = dbHelper.getWritableDatabase();

        ContentValues recordValues = new ContentValues();
        recordValues.put("category", gagebuCategory.getCategory());
        recordValues.put("icon_name", gagebuCategory.getIconName());
        recordValues.put("is_income", gagebuCategory.isIncome());

        int insertId = (int) db.insert(DBHelper.TABLE_GAGEBU_CATEGORY, null, recordValues);

        dbHelper.close();

        return insertId;
    }

    /** 카테고리 명으로 아이콘 가져오기 **/
    public String getIconName(String category){

        String iconName;

        if(dbHelper == null){
            dbHelper = new DBHelper(context, DBHelper.DB_NAME, null, DBHelper.DB_VERSION);
        }

        db = dbHelper.getReadableDatabase();
        String[] columns = {"icon_name"};
        cursor = db.query(DBHelper.TABLE_GAGEBU_CATEGORY, columns, "category='" + category + "'", null, null, null, null);
        cursor.moveToFirst();
        iconName = cursor.getString(0);

        cursor.close();
        dbHelper.close();

        return iconName;
    }

    /**
     * 모든 테이블 다 지울 때 사용
     */
    public void deleteAll(){
        if(dbHelper == null){
            dbHelper = new DBHelper(context, DBHelper.DB_NAME, null, DBHelper.DB_VERSION);
        }
        db = dbHelper.getWritableDatabase();

        db.delete(DBHelper.TABLE_GAGEBU_CATEGORY,  null , null);
        dbHelper.close();
    }
}
