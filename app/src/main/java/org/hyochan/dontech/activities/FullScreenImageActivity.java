package org.hyochan.dontech.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.hyochan.dontech.R;
import org.hyochan.dontech.utils.MyLog;
import org.hyochan.dontech.utils.TouchImageView;

import java.io.File;

public class FullScreenImageActivity extends AppCompatActivity implements View.OnClickListener{

    private final String TAG = "FullScreenImageActivity";

    private RelativeLayout relBack;
    private TextView txtTitle;
    private TouchImageView touchImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        txtTitle = (TextView) findViewById(R.id.txt_title);
        txtTitle.setText(getString(R.string.see_image));
        relBack = (RelativeLayout) findViewById(R.id.rel_back);
        relBack.setOnClickListener(this);

        Intent intent = getIntent();
        String img = intent.getStringExtra("img");
        touchImageView = (TouchImageView) findViewById(R.id.touch_img);

        Bitmap bitmap;
        try{
            File tmpPath = new File(Environment.getExternalStorageDirectory(), "/dontech");
            File myPath = new File(tmpPath, img);
            if (myPath.exists()) {
                MyLog.d(TAG, "imgExists : " + myPath);
                // 파일이 있으면 imgView에 띄우기
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bitmap = BitmapFactory.decodeFile(myPath.getAbsolutePath(), bmOptions);
                touchImageView.setImageBitmap(bitmap);
            }
        } catch (NullPointerException ne){
            MyLog.d(TAG, "NullPointerException : " + ne.getMessage());
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rel_back:
                onBackPressed();
                break;
        }
    }
}
