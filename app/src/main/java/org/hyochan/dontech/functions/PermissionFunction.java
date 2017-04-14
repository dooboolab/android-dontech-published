package org.hyochan.dontech.functions;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;

import org.hyochan.dontech.utils.MyLog;
import org.hyochan.dontech.utils.MyPermission;

/**
 * Created by hyochan on 2016. 10. 10..
 */

public class PermissionFunction {
    private static final String TAG = "PermissionFunction";

    private static PermissionFunction permissionFunction;
    private SharedPreferences sharedPreferences;
    private Context context;

    private PermissionFunction(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.context = context;
    }

    public static PermissionFunction getInstance(Context context) {
        if (permissionFunction == null) permissionFunction= new PermissionFunction(context);
        return permissionFunction;
    }

    /** Storage Access 퍼미션 권한 확인 **/
    public boolean isStoragePermissionGranted(Activity activity){
        boolean result = false;
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] strPermissions = new String[]{
                    MyPermission.WRITE_STORAGE
            };
            if (!MyPermission.getInstance(activity).hasSelfPermissions(strPermissions)) {
                MyLog.d(TAG, "no permissions");
                ActivityCompat.requestPermissions(activity, strPermissions, 1);
            } else {
                MyLog.d(TAG, "has permissions");
                result = true;
            }
        } else{
            result = true;
        }
        return result;
    }

    /** Location Access 퍼미션 권한 확인 **/
    public boolean isLocationPermissionGranted(Activity activity){
        boolean result = false;
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] strPermissions = new String[]{
                    // MyPermission.ACCESS_FINE_LOCATION,
                    MyPermission.ACCESS_COARSE_LOCATION
            };
            if (!MyPermission.getInstance(activity).hasSelfPermissions(strPermissions)) {
                MyLog.d(TAG, "no permissions");
                ActivityCompat.requestPermissions(activity, strPermissions, 1);
            } else {
                MyLog.d(TAG, "has permissions");
                result = true;
            }
        } else{
            result = true;
        }
        return result;
    }

    /** SMS 퍼미션 권한 확인 **/
    public boolean isSMSPermissionGranted(Activity activity){
        boolean result = false;
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] strPermissions = new String[]{
                    MyPermission.RECEIVE_SMS
            };
            if (!MyPermission.getInstance(activity).hasSelfPermissions(strPermissions)) {
                MyLog.d(TAG, "no permissions");
                ActivityCompat.requestPermissions(activity, strPermissions, 1);
            } else {
                MyLog.d(TAG, "has permissions");
                result = true;
            }
        } else{
            result = true;
        }
        return result;
    }

}

    


