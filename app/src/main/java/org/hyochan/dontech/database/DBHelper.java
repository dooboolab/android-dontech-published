package org.hyochan.dontech.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.hyochan.dontech.R;
import org.hyochan.dontech.global_variables.MyString;
import org.hyochan.dontech.models.Gagebu;
import org.hyochan.dontech.models.GagebuBook;
import org.hyochan.dontech.preferences.AppPref;
import org.hyochan.dontech.utils.MyLog;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by hyochan on 2014. 7. 29..
 */
public class DBHelper extends SQLiteOpenHelper {  //데이터베이스 클래스

    private final String TAG = "DBHelper";
    public static final String DB_NAME = "gagetalk.db";
    public static final int DB_VERSION = 1;

    private  Context context;

    public static final String TABLE_GAGEBU_BOOK = "gagebu_book";
    public static final String TABLE_GAGEBU_CATEGORY = "gagebu_category";
    public static final String TABLE_GAGEBU = "gagebu";

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                    int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        MyLog.d(TAG, "createdb");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_GAGEBU_BOOK +
                        "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "icon_name TEXT, " +
                        "name TEXT," +
                        "created TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_GAGEBU +
                        "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "name TEXT, " // name 은 가계부 이름을 지칭한다.
                        + "date TEXT, "
                        + "category TEXT, "
                        + "money INTEGER , "
                        + "detail TEXT, "
                        + "address TEXT, "
                        + "location_x REAL, "
                        + "location_y REAL, "
                        + "img TEXT)");

        insertRecord(db);

    }

    /* 초기 레코드 값들을 입력한다. */
    private void insertRecord(SQLiteDatabase db){
        MyLog.d(TAG, "insertRecord");

        // default 가계부 입력

        Cursor cursor = db.query(DBHelper.TABLE_GAGEBU_BOOK, null, null, null, null, null, "created");
        if(cursor.getCount() == 0){
            db.execSQL("insert into " + TABLE_GAGEBU_BOOK +
                    "(name, icon_name,created) values ('" + context.getString(R.string.my_gagebu) + "'," +
                    "'ic_gagebu_male','" + Calendar.getInstance().getTimeInMillis() + "')");
        }

        // 카테고리 입력
        createCategory(db);

        // 처음 설정된 가계부 이름
        AppPref.getInstance(context).put(MyString.PREF_SELECTED_GAGEBU, context.getString(R.string.my_gagebu));
    }


    private void createCategory(SQLiteDatabase db){
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_GAGEBU_CATEGORY +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "category TEXT, " +
                "icon_name TEXT," +
                "is_income BOOLEAN)");


        // default 수입 카테고리 입력
/**
 *       용돈 : R.drawable.ic_money_pig
 *       월급 : R.drawable.ic_wallet,
 *       보너스 : R.drawable.ic_circle_dollar,
 *       이자수입 : R.drawable.ic_money,
 *       특별수당 : R.drawable.ic_gift,
 *       물품판매 : R.drawable.ic_sell,
 *       기타 : R.drawable.ic_question
 **/
        final String[] arrIncome = context.getResources().getStringArray(R.array.category_income);
        final String[] arrIncomeDrawable = context.getResources().getStringArray(R.array.category_income_drawables);
        for(int i=0; i<arrIncome.length; i++){
            db.execSQL("insert into " + TABLE_GAGEBU_CATEGORY + "(category, icon_name, is_income) values (" +
                    "'" + arrIncome[i] + "'," +
                    "'" + arrIncomeDrawable[i] + "'," +
                    "1)");
        }
        // default 지출 가테고리 입력
/**
 *       식사 : R.drawable.ic_eating,
 *       간식 : R.drawable.ic_snack,
 *       숙박 : R.drawable.ic_room,
 *       데이트 : R.drawable.ic_heart,
 *       건강 : R.drawable.ic_health,
 *       경조사 : R.drawable.ic_gift,
 *       교통비 : R.drawable.ic_taxi,
 *       문화생활 : R.drawable.ic_brush,
 *       미용 : R.drawable.ic_salon,
 *       생필품 : R.drawable.ic_towel,
 *       애완용품 : R.drawable.ic_bone,
 *       영화 : R.drawable.ic_movie,
 *       운동 : R.drawable.ic_health,
 *       의류 : R.drawable.ic_tshirt,
 *       저축 : R.drawable.ic_money_pig,
 *       통신비 : R.drawable.ic_phone,
 *       커피 : R.drawable.ic_coffee,
 *       술 : R.drawable.ic_drinking,
 *       가전제품 : R.drawable.ic_home_appliance,
 *       전자제품 : R.drawable.ic_labtop,
 *       회비 : R.drawable.ic_users,
 *       기타 : R.drawable.ic_question
 */
        final String[] arrConsume = context.getResources().getStringArray(R.array.category_consume);
        final String[] arrConsumeDrawable = context.getResources().getStringArray(R.array.category_consume_drawables);
        for(int i=0; i<arrConsume.length; i++){
            db.execSQL("insert into " + TABLE_GAGEBU_CATEGORY + "(category, icon_name, is_income) values ( " +
                    "'" + arrConsume[i] + "'," +
                    "'" + arrConsumeDrawable[i] + "'," +
                    "0)");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        // db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAGEBU+ ";");
        // db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAGEBU_BOOK + ";");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAGEBU_CATEGORY+ ";");
        createCategory(db);
        onCreate(db);
    }

}