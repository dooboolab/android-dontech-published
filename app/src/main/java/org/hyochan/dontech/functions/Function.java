package org.hyochan.dontech.functions;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.hyochan.dontech.R;
import org.hyochan.dontech.global_variables.MyString;
import org.hyochan.dontech.preferences.AppPref;
import org.hyochan.dontech.utils.MyLog;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hyochan on 3/28/15.
 */
public class Function {

    private static final String TAG = "Function";

    private static Function function;
    private SharedPreferences sharedPreferences;
    private Context context;

    private Function(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.context = context;
    }

    public static Function getInstance(Context context) {
        if (function == null) function = new Function(context);
        return function;
    }

    /**
     * 스트링이 null 인지 비었는지 확인
     */
    public boolean isEmptyStr(final String s){
        // Null-safe, short-circuit evaluation.
        return s == null || s.trim().isEmpty();
    }

    public void hideKeyboard(Activity activity){
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String encodeURLIfNeed(String input) {
        Pattern HANGLE_PATTERN = Pattern.compile("[가-힣]");
        // Pattern HANGLE_PATTERN = Pattern.compile("[\\x{ac00}-\\x{d7af}]");
        if (input == null || input.equals("")) {
            return input;
        }

        Matcher matcher = HANGLE_PATTERN.matcher(input);
        while(matcher.find()) {
            String group = matcher.group();

            try {
                input = input.replace(group, URLEncoder.encode(group, "UTF-8"));
            } catch (UnsupportedEncodingException ignore) {
            }
        }

        return input;
    }

    public LinearLayout getEmptyViewForListView(int dp){
        LinearLayout emptyView = new LinearLayout(context);
        emptyView.setOrientation(LinearLayout.HORIZONTAL);
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                Function.getInstance(context).dpToPx(dp));
        emptyView.setLayoutParams(lp);
        return  emptyView;
    }

    public boolean checkEditTextEmpty(List<EditText> editTextList){
        boolean result = false;
        Iterator<EditText> itr = editTextList.iterator();
        while (itr.hasNext()) { // 값이 나올때까지 while문을 돈다
            EditText editText = itr.next();
            if(editText.getText().toString().equals("")){
                return true;
            }
        }
        return result;
    }

    public Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                context.getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    public String saveImgToAppStorage(Bitmap bitmapImage, String name){

        File tmpPath = new File(Environment.getExternalStorageDirectory(), "/dontech");
        if(tmpPath.exists() != true){
            Log.d(TAG, "tmp path not exists");
            tmpPath.mkdirs();
        } else {
            Log.d(TAG, "tmp path EXISTS : " + tmpPath.toString());
        }
        // Create imageDir
        File myPath=new File(tmpPath, name);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myPath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try{
                fos.close();
            }catch (IOException e){
                MyLog.d(TAG, e.getMessage());
            }
        }
        return myPath.getAbsolutePath();
    }

    public int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)); //px
    }

    public int pxToDp(int px) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)); //dp
    }

    // => from here, used when checking editform when signup
    public Boolean checkId(String id) {
        // check if id is alphanumeric
        for (int i = 0; i < id.length(); i++) {
            char c = id.charAt(i);
            if (c < 0x30 || (c >= 0x3a && c <= 0x40) || (c > 0x5a && c <= 0x60) || c > 0x7a)
                return false;
        }
        return true;
    }

    public Boolean checkPw(String password, String password_ok) {
        return password.equals(password_ok); // return true or false
    }

    public Boolean checkName(String name) {
        // return false if it is not fully hangul
        final int HANGUL_UNICODE_START = 0xAC00;
        final int HANGUL_UNICODE_END = 0xD7AF;

        int text_count = name.length();
        int is_hangul_count = 0;

        for (int i = 0; i < text_count; i++) {
            char syllable = name.charAt(i);
            if ((HANGUL_UNICODE_START <= syllable)
                    && (syllable <= HANGUL_UNICODE_END)) {
                is_hangul_count++;
            }
        }

        return (is_hangul_count == text_count); // return true or false
    }

    public Boolean checkPhone(String phone) {
        String regex = "^\\+?[0-9. ()-]{10,25}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phone);

        return matcher.matches(); // return true or false
    }

    public Boolean checkEmail(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        Pattern p = Pattern.compile(ePattern);
        Matcher m = p.matcher(email);
        return m.matches();
    }
}