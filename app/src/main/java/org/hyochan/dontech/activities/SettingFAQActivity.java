package org.hyochan.dontech.activities;

import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.hyochan.dontech.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingFAQActivity extends AppCompatActivity {

    private final String TAG = "SettingFAQActivity";

    /**  타이틀 뷰 **/
    @BindView(R.id.rel_back)
    RelativeLayout relBack;
    @BindView(R.id.rel_title)
    RelativeLayout relTitle;
    @BindView(R.id.txt_title)
    TextView txtTitle;

    /** 버디 뷰 **/
    @BindView(R.id.exp_listview)
    ExpandableListView expandableListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_faq);

        ButterKnife.bind(this);

        // 롤리팝 이상이면 status bar 색상 변경
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            this.getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorSubPrimary));
        }

        // 제목 세팅
        relTitle.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorSubPrimary));
        txtTitle.setText("FAQ");


    }

    @OnClick(R.id.rel_back) void onBackClicked(){
        onBackPressed();
    }
}
