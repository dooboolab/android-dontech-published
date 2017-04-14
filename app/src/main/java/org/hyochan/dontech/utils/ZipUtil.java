package org.hyochan.dontech.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import org.hyochan.dontech.R;

import java.io.File;

/**
 * Created by hyochan on 2016-03-07.
 */
public class ZipUtil {
    private static final String TAG = "ZipUtil";

    private static ZipUtil zipUtil;
    private SharedPreferences sharedPreferences;
    private Context context;
    private final int COMPRESSION_LEVEL = 8;
    private final int BUFFER_SIZE = 1024 * 2;

    private ZipUtil(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.context = context;
    }

    public static ZipUtil getInstance(Context context) {
        if (zipUtil == null) zipUtil = new ZipUtil(context);
        return zipUtil;
    }

    public String exportZip(){  // /Downloads 폴더에 memocast_backup.zip 생성
        String result = "Error";
        try {
            // zip 파라미터 설정
            ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
            // 1. zip파일은 download 폴더에 생성
            File tmpPath = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DOWNLOADS);
            ZipFile zipfile = new ZipFile(tmpPath + "/dontech.zip");
            File appPath = new File(Environment.getExternalStorageDirectory(), context.getString(R.string.app_file_path));
            if(!appPath.exists()){
                MyLog.d(TAG, "tmp path not exists");
                appPath.mkdirs();
            } else {
                MyLog.d(TAG, "appPath EXISTS : " + appPath.toString());
            }
            zipfile.addFolder(appPath, parameters);
            result = tmpPath + "/dontech.zip";
        } catch (Exception e) {
            MyLog.d(TAG, "exception : " + e.getMessage());
        }

        return result;
    }

    public void importZip(){
        try {
            File tmpPath = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DOWNLOADS);
            ZipFile zipfile = new ZipFile(tmpPath + "/dontech.zip");

            // 폴더 생성
            File appPath = new File(Environment.getExternalStorageDirectory() + "/");
            if(appPath.exists() != true){
                MyLog.d(TAG, "appPath not exists");
                appPath.mkdirs();
            } else {
                MyLog.d(TAG, "appPath EXISTS : " + appPath.toString());
            }

            // 압축 풀기
            zipfile.extractAll(appPath.toString());
        } catch (Exception e){
            MyLog.d(TAG, e.getMessage());
        }
    }
}
