package org.hyochan.dontech.utils;

import android.graphics.Bitmap;

import java.io.File;

/**
 * Created by hyochan on 2016. 10. 10..
 */

public interface ImageCache {

    public void addBitmap(String key, Bitmap bitmap);

    public void addBitmap(String key, File bitmapFile);

    public Bitmap getBitmap(String key);

    public void clear();

}