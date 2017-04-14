package org.hyochan.dontech.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.hyochan.dontech.R;
import org.hyochan.dontech.fragments_dialog.DialogBtn2;
import org.hyochan.dontech.functions.PermissionFunction;
import org.hyochan.dontech.tasks.ExportExcelTask;
import org.hyochan.dontech.tasks.ExportZipTask;
import org.hyochan.dontech.tasks.ImportExcelTask;
import org.hyochan.dontech.tasks.ImportZipTask;
import org.hyochan.dontech.utils.CustomToast;
import org.hyochan.dontech.utils.MyLog;
import org.hyochan.dontech.utils.MyPermission;

import java.io.File;

public class BackupRestoreActivity extends AppCompatActivity implements View.OnClickListener{

    private final String TAG = "BackupRestoreActivity";

    private Activity activity;
    private Context context;
    private RelativeLayout relBack;
    private TextView txtTitle;
    private Button btnBackup; // 백업 버튼
    private Button btnRestore; // 복원 버튼

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_restore);

        activity = this;
        context = this;

        relBack = (RelativeLayout) findViewById(R.id.rel_back);
        relBack.setOnClickListener(this);
        txtTitle = (TextView) findViewById(R.id.txt_title);
        txtTitle.setText(getString(R.string.backup_and_restore));

        btnBackup = (Button) findViewById(R.id.btn_backup);
        btnRestore = (Button) findViewById(R.id.btn_restore);

        btnBackup.setOnClickListener(this);
        btnRestore.setOnClickListener(this);
    }

    // TODO : 시작전에 권한 확인
    @Override
    public void onClick(View v) {
        final Bundle args = new Bundle();
        final DialogBtn2 dialogBtn2 = new DialogBtn2();
        switch (v.getId()){
            case R.id.rel_back:
                onBackPressed();
                break;
            case R.id.btn_backup:
                if(PermissionFunction.getInstance(context).isStoragePermissionGranted(activity)){
                    args.putString("html", "backup");
                    dialogBtn2.setArguments(args);
                    dialogBtn2.show(getSupportFragmentManager(), "fragment_backup");
                    dialogBtn2.setDialogBtn2(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {// 취소
                                    dialogBtn2.dismiss();
                                }
                            },
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    /**
                                     1. ExportExcelTask를 이용해서 excel 만듬
                                     2. 1번이 끝나면 ExportZipTask로 Zip 파일 만듬
                                     **/
                                    new ExportExcelTask(context, new ExportExcelTask.OnTaskCompleted() {
                                        @Override
                                        public void onTaskCompleted(String result) {
                                            MyLog.d(TAG, "result : " + result);
                                            new ExportZipTask(context, new ExportZipTask.OnTaskCompleted() {
                                                @Override
                                                public void onTaskCompleted(String result) {
                                                    CustomToast.getInstance(context).createToast(context.getString(R.string.backup_guide_announce));
                                                }
                                            }).execute(null, null, null);
                                        }
                                    }).execute(null, null, null);
                                    dialogBtn2.dismiss();
                                }
                            });
                }
                break;
            case R.id.btn_restore:
                if(PermissionFunction.getInstance(context).isStoragePermissionGranted(activity)){
                args.putString("html", "restore");
                dialogBtn2.setArguments(args);
                dialogBtn2.show(getSupportFragmentManager(), "fragment_restore");
                dialogBtn2.setDialogBtn2(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {// 취소
                                dialogBtn2.dismiss();
                            }
                        },
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                /**
                                 1. ImportZipTask
                                 2. ImportExcelTask
                                 */
                                new ImportZipTask(context, new ImportZipTask.OnTaskCompleted() {
                                    @Override
                                    public void onTaskCompleted(String result) {
                                        new ImportExcelTask(context, new ImportExcelTask.OnTaskCompleted() {
                                            @Override
                                            public void onTaskCompleted(String result) {
                                                CustomToast.getInstance(context).createToast(getString(R.string.restore_completed));
                                                setResult(RESULT_OK);
                                                onBackPressed();
                                            }
                                        }).execute(null, null, null);
                                    }
                                }).execute(null, null, null);
                                dialogBtn2.dismiss();
                            }
                        });
                }
                break;
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
            }
        }
    }
}
