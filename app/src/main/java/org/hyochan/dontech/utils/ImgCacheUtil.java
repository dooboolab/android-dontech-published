package org.hyochan.dontech.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;

import java.io.File;

/**
 * Created by hyochan on 2016-08-18.
 */


public class ImgCacheUtil implements ImageCache {

    private final int MAX_CNT = 50;  // 캐시 저장 개수
    private LruCache<String, Bitmap> lruCache;

    private static ImgCacheUtil instance = new ImgCacheUtil();

    public static ImgCacheUtil getInstance() {
        return instance;
    }

    public ImgCacheUtil() {
        lruCache = new LruCache<>(MAX_CNT);
    }
    @Override
    public void addBitmap(String key, Bitmap bitmap) {
        if (bitmap == null)
            return;
        lruCache.put(key, bitmap);
    }

    @Override
    public void addBitmap(String key, File bitmapFile) {
        if (bitmapFile == null)
            return;
        if (!bitmapFile.exists())
            return;

        Bitmap bitmap = BitmapFactory.decodeFile(bitmapFile.getAbsolutePath());
        lruCache.put(key, bitmap);
    }

    @Override
    public Bitmap getBitmap(String key) {
        return lruCache.get(key);
    }

    @Override
    public void clear() {
        lruCache.evictAll();
    }

}