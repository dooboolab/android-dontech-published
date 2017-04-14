package org.hyochan.dontech.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.content.FileProvider;
import android.util.Log;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.hyochan.dontech.R;
import org.hyochan.dontech.database.TableGagebu;
import org.hyochan.dontech.database.TableGagebuBook;
import org.hyochan.dontech.database.TableGagebuCategory;
import org.hyochan.dontech.models.Gagebu;
import org.hyochan.dontech.models.GagebuBook;
import org.hyochan.dontech.models.GagebuCategory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by hyochan on 2016-03-07.
 */
public class ExcelUtil {
    private static final String TAG = "ExcelUtil";

    private static ExcelUtil excelUtil;
    private SharedPreferences sharedPreferences;
    private Context context;

    private ExcelUtil(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.context = context;
    }

    public static ExcelUtil getInstance(Context context) {
        if (excelUtil == null) excelUtil = new ExcelUtil(context);
        return excelUtil;
    }

    public String exportExcel(){
        String result = "Error";
        ArrayList<GagebuBook> gagebuBooks = TableGagebuBook.getInstance(context).selectMyGagebuBooks();
        ArrayList<GagebuCategory> gagebuCategories = TableGagebuCategory.getInstance(context).selectCategories(true);
        gagebuCategories.addAll(TableGagebuCategory.getInstance(context).selectCategories(false));
        ArrayList<Gagebu> gagebus = TableGagebu.getInstance(context).selectAll();

        // 가계부 북 : icon, name, created
        // 카테고리 : category, name, isIncome
        // 가계부 : _id, name, date, category, money, detail, address, location_x, location_y, img


        // Workbook 생성
        try{
            File tmpPath = new File(Environment.getExternalStorageDirectory(), context.getString(R.string.app_file_path));
            if(tmpPath.exists() != true){
                // Log.d(TAG, "tmp path not exists");
                tmpPath.mkdirs();
            } else {
                // Log.d(TAG, "tmp path EXISTS : " + tmpPath.toString());
            }

            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheetGagebuBooks = workbook.createSheet("gagebu_books");
            HSSFSheet sheetGagebuCategories= workbook.createSheet("gagebu_categories");
            HSSFSheet sheetGagebus = workbook.createSheet("gagebus");

            HSSFRow rowHeadGagebuBooks = sheetGagebuBooks.createRow((short) 0);
            rowHeadGagebuBooks.createCell(0).setCellValue("icon");
            rowHeadGagebuBooks.createCell(1).setCellValue("name");
            rowHeadGagebuBooks.createCell(2).setCellValue("created");

            HSSFRow rowHeadCategories = sheetGagebuCategories.createRow((short) 0);
            rowHeadCategories.createCell(0).setCellValue("category");
            rowHeadCategories.createCell(1).setCellValue("icon_name");
            rowHeadCategories.createCell(2).setCellValue("income");

            HSSFRow rowHeadGagebu = sheetGagebus.createRow((short) 0);
            rowHeadGagebu.createCell(0).setCellValue("_id");
            rowHeadGagebu.createCell(1).setCellValue("name");
            rowHeadGagebu.createCell(2).setCellValue("date");
            rowHeadGagebu.createCell(3).setCellValue("category");
            rowHeadGagebu.createCell(4).setCellValue("money");
            rowHeadGagebu.createCell(5).setCellValue("detail");
            rowHeadGagebu.createCell(6).setCellValue("address");
            rowHeadGagebu.createCell(7).setCellValue("locationX");
            rowHeadGagebu.createCell(8).setCellValue("locationY");
            rowHeadGagebu.createCell(9).setCellValue("img");

            int i = 1;

            for (GagebuBook gagebuBook: gagebuBooks){
                HSSFRow row = sheetGagebuBooks.createRow((short)i++);
                row.createCell(0).setCellValue(gagebuBook.getIconName());
                row.createCell(1).setCellValue(gagebuBook.getName());
                row.createCell(2).setCellValue(gagebuBook.getCreated());
            }

            i = 1;
            for (GagebuCategory gagebuCategory : gagebuCategories){
                HSSFRow row = sheetGagebuCategories.createRow((short)i++);
                row.createCell(0).setCellValue(gagebuCategory.getCategory());
                row.createCell(1).setCellValue(gagebuCategory.getIconName());
                row.createCell(2).setCellValue(gagebuCategory.isIncome() ? 1 : 0);
            }

            i = 1;
            for (Gagebu gagebu : gagebus){
                // row 생성
                HSSFRow row = sheetGagebus.createRow((short)i++);
                row.createCell(0).setCellValue(gagebu.get_id());
                row.createCell(1).setCellValue(gagebu.getName());
                row.createCell(2).setCellValue(gagebu.getDate());
                row.createCell(3).setCellValue(gagebu.getCategory());
                row.createCell(4).setCellValue(gagebu.getMoney());
                row.createCell(5).setCellValue(gagebu.getDetail());
                row.createCell(6).setCellValue(gagebu.getAddress());
                row.createCell(7).setCellValue(gagebu.getLocationX());
                row.createCell(8).setCellValue(gagebu.getLocationY());
                row.createCell(9).setCellValue(gagebu.getImg());
            }

            // 1. download 폴더에서 추가로 하나 생성 (접근성)
            // tmpPath = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DOWNLOADS);
            // Uri photoURI = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", tmpPath);
            FileOutputStream fileOut = new FileOutputStream(tmpPath + "/dontech.xls");
            workbook.write(fileOut);
            fileOut.close();
            result = context.getString(R.string.backup_completed);
            MyLog.d(TAG, "gagebu excel export success!!!");
        } catch (Exception ex){
            System.out.println(ex);
        }
        return result;
    }
    public void importExcel(){
        try{
            // 1. 파일을 불러옴.
            File tmpPath = new File(Environment.getExternalStorageDirectory(), context.getString(R.string.app_file_path));
            FileInputStream fileInputStream = new FileInputStream(tmpPath + "/dontech.xls");
            // 2. Workbook 형태의 데이터로 바꿈.
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook(fileInputStream);
            // 3. 데이터로 바뀌었는지 확인
            if (hssfWorkbook != null){
                // 4. gagebu라는 시트를 불러온다.
                HSSFSheet sheetGagebuBooks = hssfWorkbook.getSheet("gagebu_books");
                HSSFSheet sheetGagebuCategories= hssfWorkbook.getSheet("gagebu_categories");
                HSSFSheet sheetGagebus = hssfWorkbook.getSheet("gagebus");

                // 5. 해당 시트가 있는지 확인
                /** 가계부 북 **/
                if(sheetGagebuBooks != null){
                    TableGagebuBook.getInstance(context).deleteAll();
                    Iterator<Row> rows = sheetGagebuBooks.rowIterator();
                    rows.next(); // 재목 row는 거른다.

                    while(rows.hasNext()){
                        Row row = rows.next();
                        DataFormatter formatter = new DataFormatter();
                        Cell cellIconName = row.getCell(0);
                        Cell cellName = row.getCell(1);
                        Cell cellCreated = row.getCell(2);

                        String  iconName =  formatter.formatCellValue(cellIconName);
                        String name = formatter.formatCellValue(cellName);
                        String created = formatter.formatCellValue(cellCreated);

                        GagebuBook gagebubook = new GagebuBook(iconName, name, created);

                        TableGagebuBook.getInstance(context).insertMyGagebu(gagebubook);
                    }
                }

                /** 카테고리 **/
                if(sheetGagebuCategories != null){
                    TableGagebuCategory.getInstance(context).deleteAll();
                    Iterator<Row> rows = sheetGagebuCategories.rowIterator();
                    rows.next(); // 재목 row는 거른다.

                    while(rows.hasNext()){
                        Row row = rows.next();
                        DataFormatter formatter = new DataFormatter();
                        Cell cellCategory = row.getCell(0);
                        Cell cellIconName = row.getCell(1);
                        Cell cellIsIncome = row.getCell(2);

                        String category = formatter.formatCellValue(cellCategory);
                        String iconName = formatter.formatCellValue(cellIconName);
                        int num = Integer.valueOf(formatter.formatCellValue(cellIsIncome));
                        GagebuCategory gagebuCategory = new GagebuCategory(0, category, iconName, num == 1 ? true : false);

                        MyLog.d(TAG, "category : " + category + ", isIncome : " + num);
                        TableGagebuCategory.getInstance(context).insertGagebuCategories(gagebuCategory);
                    }
                }

                /** 가계부 **/
                if(sheetGagebus != null){
                    TableGagebu.getInstance(context).deleteAll();
                    Iterator<Row> rows = sheetGagebus.rowIterator();
                    rows.next(); // 재목 row는 거른다.

                    while(rows.hasNext()){
                        Row row = rows.next();
                        DataFormatter formatter = new DataFormatter();
                        Cell cellId = row.getCell(0);
                        Cell cellName = row.getCell(1);
                        Cell cellDate = row.getCell(2);
                        Cell cellCategory = row.getCell(3);
                        Cell cellMoney = row.getCell(4);
                        Cell cellDetail = row.getCell(5);
                        Cell cellAddress = row.getCell(6);
                        Cell cellLocationX = row.getCell(7);
                        Cell cellLocationY = row.getCell(8);
                        Cell cellImg = row.getCell(9);

                        int _id =  Integer.valueOf(formatter.formatCellValue(cellId));
                        String name = formatter.formatCellValue(cellName);
                        String date = formatter.formatCellValue(cellDate);
                        String category = formatter.formatCellValue(cellCategory);
                        int money = Integer.valueOf(formatter.formatCellValue(cellMoney));
                        String detail = formatter.formatCellValue(cellDetail);
                        String address = formatter.formatCellValue(cellAddress);
                        double locationX = Double.valueOf(formatter.formatCellValue(cellLocationX));
                        double locationY = Double.valueOf(formatter.formatCellValue(cellLocationY));
                        String img = formatter.formatCellValue(cellImg);


                        Gagebu gagebu = new Gagebu(
                                _id , name, date, category, money, detail, address, locationX, locationY, img
                        );

                        MyLog.d(TAG, "row not exists");
                        TableGagebu.getInstance(context).insertGagebu(gagebu);
                    }
                }
            }
        } catch (FileNotFoundException e){
            MyLog.e(TAG, e.getMessage());
        } catch (IOException e){
            MyLog.e(TAG, e.getMessage());
        }
    }
}
