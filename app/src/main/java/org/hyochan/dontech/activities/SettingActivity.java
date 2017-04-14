package org.hyochan.dontech.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.hyochan.dontech.R;
import org.hyochan.dontech.adapters.SettingAdapter;
import org.hyochan.dontech.database.TableGagebuBook;
import org.hyochan.dontech.functions.AppFunction;
import org.hyochan.dontech.functions.PermissionFunction;
import org.hyochan.dontech.models.GagebuBook;
import org.hyochan.dontech.models.SettingData;
import org.hyochan.dontech.preferences.AppPref;
import org.hyochan.dontech.utils.CustomToast;
import org.hyochan.dontech.utils.MyLog;
import org.hyochan.dontech.utils.MyPermission;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class SettingActivity extends AppCompatActivity {

    final String TAG = "SettingActivity";

    private Activity activity;
    private Context context;

    @BindView(R.id.rel_back)
    RelativeLayout relBack;
    @BindView(R.id.rel_title)
    RelativeLayout relTitle;
    @BindView(R.id.txt_title)
    TextView txtTitle;
    @BindView(R.id.list_setting)
    ListView listSetting;

    private Intent intent;
    private SettingAdapter settingAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        activity = this;
        context = this;

        // 롤리팝 이상이면 status bar 색상 변경
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            this.getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorSubPrimary));
        }

        // 제목 세팅
        relTitle.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorSubPrimary));
        txtTitle.setText(getString(R.string.setting));

        // 1. setup data
        String[] arraySetting = getResources().getStringArray(R.array.array_setting);
        ArrayList<SettingData> arrSetting = new ArrayList<>();
        arrSetting.add(new SettingData(SettingData.TYPE_ARR, arraySetting[0], ""));
        String versionName = "";
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo("org.hyochan.dontech", 0);
            versionName = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e){
            MyLog.e(TAG, e.getMessage());
        }
        arrSetting.add(new SettingData(SettingData.TYPE_ARR, arraySetting[1], ""));
        arrSetting.add(new SettingData(SettingData.TYPE_MORE, arraySetting[2], versionName));
        arrSetting.add(new SettingData(SettingData.TYPE_CHK_MORE, arraySetting[3],
                AppPref.getInstance(getApplicationContext()).getValue("sms_gagebu", AppFunction.getInstance(context).getCurrentGagebuBook())));

        // 2. setup adapter
        settingAdapter = new SettingAdapter(this, R.layout.adapter_setting, arrSetting);
        // 3. bind to listview
        listSetting.setAdapter(settingAdapter);
    }

    @OnClick(R.id.rel_back) void onBackClicked(){
        onBackPressed();
    }

    @OnItemClick(R.id.list_setting) void onListSettingItemClicked(AdapterView<?> parent, View view, int position, long id){
        switch (position) {
            case SettingData.POS_ANNOUNCE:
                intent = new Intent(getApplicationContext(), SettingAnnounceActivity.class);
                startActivity(intent);
                break;
            case SettingData.POS_FAQ:
                intent = new Intent(getApplicationContext(), SettingFAQActivity.class);
                startActivity(intent);
                break;
            case SettingData.POS_VERSION_INFO:
                return;
            case SettingData.POS_SMS_PARSE:
                if(PermissionFunction.getInstance(context).isSMSPermissionGranted(activity)){
                    if (AppPref.getInstance(getApplicationContext()).getValue(AppPref.SMS_PARSE, false)){
                        AppPref.getInstance(getApplicationContext()).put(AppPref.SMS_PARSE, false);
                        settingAdapter.getItem(SettingData.POS_SMS_PARSE).setTxtMore("");
                        settingAdapter.notifyDataSetChanged();
                    } else {
                    /*
                        TODO
                        1. 이 항목을 클릭하면 SMS_PARSE일 때 activity를 띄운다.
                        2. popup 메뉴를 선택하면 아래 코드가 실행된다.
                        3. popup 메뉴를 선택하지 않으면 아래 코드를 실행하지 않는다.
                     */
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                        alertBuilder.setIcon(R.mipmap.ic_launcher);
                        alertBuilder.setTitle(getString(R.string.choose_gagebu_used_in_sms_parsing));
                        // List Adapter 생성
                        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                getApplicationContext(),
                                android.R.layout.simple_list_item_single_choice) {
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View view = super.getView(position, convertView, parent);
                                TextView text = (TextView) view.findViewById(android.R.id.text1);
                                text.setTextColor(Color.BLACK);
                                return view;
                            }
                        };
                        ArrayList<GagebuBook> gagebuBooks = TableGagebuBook.getInstance(context).selectMyGagebuBooks();
                        for(GagebuBook gagebuBook : gagebuBooks){
                            adapter.add(gagebuBook.getName());
                        }
                        alertBuilder.setAdapter(adapter,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        AppPref.getInstance(getApplicationContext()).put("sms_gagebu", adapter.getItem(id));
                                        settingAdapter.getItem(SettingData.POS_SMS_PARSE).setTxtMore(adapter.getItem(id));
                                        CustomToast.getInstance(getApplicationContext()).createToast(
                                                getResources().getString(R.string.auto_msg_guide)
                                        );
                                        AppPref.getInstance(getApplicationContext()).put(AppPref.SMS_PARSE, true);
                                        settingAdapter.notifyDataSetChanged();
                                    }
                                });
                        // 버튼 생성
                        alertBuilder.setNegativeButton(getString(R.string.cancel),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertBuilder.show();
                    }
                    break;
                }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG,"Permission: "+permissions[0]+ " : "+grantResults[0]);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            //resume tasks needing this permission
        } else {
            if(permissions[0].equals(MyPermission.ACCESS_COARSE_LOCATION)){
                CustomToast.getInstance(context).createToast(getString(R.string.permission_location_not_granted));
            } else if(permissions[0].equals(MyPermission.WRITE_STORAGE)){
                CustomToast.getInstance(context).createToast(getString(R.string.permission_storage_not_granted));
            } else if(permissions[0].equals(MyPermission.RECEIVE_SMS)){
                CustomToast.getInstance(context).createToast(getString(R.string.permission_sms_not_granted));
            }
        }
    }
}
