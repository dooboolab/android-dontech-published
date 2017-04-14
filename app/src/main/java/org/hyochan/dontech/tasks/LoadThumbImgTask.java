package org.hyochan.dontech.tasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.ImageView;

import org.hyochan.dontech.utils.ImgCacheUtil;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Created by hyochan on 2016-08-18.
 */
public class LoadThumbImgTask extends AsyncTask<String, Void, Bitmap> {
    private String url;
    private final WeakReference<ImageView> imageViewReference;

    public LoadThumbImgTask(ImageView imageView) {
        imageViewReference = new WeakReference<>(imageView);
    }

    @Override
    // Actual download method, run in the task thread
    protected Bitmap doInBackground(String... params) {
        // params comes from the execute() call: params[0] is the url.
        File tmpPath = new File(Environment.getExternalStorageDirectory(), "/dontech");
        File myPath = new File(tmpPath, params[0]);

        Bitmap bitmap = ImgCacheUtil.getInstance().getBitmap(myPath.getAbsolutePath());

        // 캐시에 bitmap이 없으면 캐시에 저장
        if(bitmap == null){
            bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(myPath.getAbsolutePath()), 60, 60);
            ImgCacheUtil.getInstance().addBitmap(myPath.getAbsolutePath(), bitmap);
        }

        return bitmap;
    }

    @Override
    // Once the image is downloaded, associates it to the imageView
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
        }

        if (imageViewReference != null) {
            ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}