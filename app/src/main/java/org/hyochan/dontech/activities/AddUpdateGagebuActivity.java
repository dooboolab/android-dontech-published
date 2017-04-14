package org.hyochan.dontech.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.hyochan.dontech.R;
import org.hyochan.dontech.activities_dialogs.DatePickerDialog;
import org.hyochan.dontech.activities_dialogs.TimePickerDialog;
import org.hyochan.dontech.adapters.SpinnerCategoryAdapter;
import org.hyochan.dontech.database.TableGagebu;
import org.hyochan.dontech.database.TableGagebuCategory;
import org.hyochan.dontech.events.GagebuUpdateEvent;
import org.hyochan.dontech.fragments_dialog.DialogPhotoActPicker;
import org.hyochan.dontech.functions.AppFunction;
import org.hyochan.dontech.functions.PermissionFunction;
import org.hyochan.dontech.global_variables.MyNumber;
import org.hyochan.dontech.models.Gagebu;
import org.hyochan.dontech.models.GagebuCategory;
import org.hyochan.dontech.tasks.SaveImgTask;
import org.hyochan.dontech.utils.BusProvider;
import org.hyochan.dontech.utils.CustomToast;
import org.hyochan.dontech.functions.Function;
import org.hyochan.dontech.utils.MyLog;
import org.hyochan.dontech.utils.MyPermission;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class AddUpdateGagebuActivity extends AppCompatActivity {

    private final String TAG = "AddUpdateGagebuActivity";

    private Activity activity;
    private Context context;

    private DialogPhotoActPicker dialogPhotoActPicker;

    @BindView(R.id.rel_title) RelativeLayout relTitle;
    @BindView(R.id.txt_title) TextView txtTitle;
    @BindView(R.id.rel_back) RelativeLayout relBack;
    @BindView(R.id.rel_save) RelativeLayout relSave;
    @BindView(R.id.rel_delete) RelativeLayout relDelete;
    @BindView(R.id.txt_date) TextView txtDate;
    @BindView(R.id.txt_time) TextView txtTime;
    @BindView(R.id.spin_category) Spinner spinCategory;
    @BindView(R.id.img_cost_lbl) ImageView imgCostLbl;
    @BindView(R.id.edit_cost) EditText editCost;
    @BindView(R.id.img_detail_lbl) ImageView imgDetailLbl;
    @BindView(R.id.edit_detail) EditText editDetail;
    @BindView(R.id.img_addr_lbl) ImageView imgAddrLbl;
    @BindView(R.id.edit_address) EditText editAddress;
    @BindView(R.id.rel_location) RelativeLayout relLocation;
    @BindView(R.id.img_location) ImageView imgLocation;
    @BindView(R.id.rel_camera) RelativeLayout relCamera;
    @BindView(R.id.img_camera) ImageView imgCamera;
    @BindView(R.id.img_view) ImageView imgView;

    private ArrayList<GagebuCategory> gagebuCategories;
    private SpinnerCategoryAdapter spinAdapter;
    private Calendar cal;
    private double locationX = 0.0;

    private Bitmap bitmap;

    private double locationY = 0.0;
    private String address;

    private boolean isIncome;
    private boolean isUpdate;
    private File tmpPath;
    private String img;
    private boolean isImgChanged;
    private Gagebu gagebu;

    private Intent returnedGagebuIntent;
    private final int IMG_SIZE = 1280;

    private boolean isFromSMS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_update_gagebu);
        ButterKnife.bind(this);

        activity = this;
        context = this;

        editCost.addTextChangedListener(myTextWatcher);
        editDetail.addTextChangedListener(myTextWatcher);
        editAddress.addTextChangedListener(myTextWatcher);
        cal = Calendar.getInstance();

        // 액션 : INSERT
        if(getIntent().getAction() != null && getIntent().getAction().equals(Intent.ACTION_INSERT)){
            if(isIncome = getIntent().getBooleanExtra("is_income", false)){
                // 롤리팝 이상이면 status bar 색상 변경
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    this.getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.income));
                }
                relTitle.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.income));
                txtTitle.setText(getString(R.string.income));
            } else {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    this.getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.consume));
                }
                relTitle.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.consume));
                txtTitle.setText(getString(R.string.consume));
            }

            MyLog.d(TAG, "isIncome : " + isIncome);
            gagebuCategories = TableGagebuCategory.getInstance(context).selectCategories(isIncome);
            spinCategory.setAdapter(spinAdapter = new SpinnerCategoryAdapter(this, gagebuCategories));
        }

        // 액션 : UPDATE
        if(getIntent().getAction() != null && getIntent().getAction().equals(Intent.ACTION_EDIT)){
            isUpdate = true;
            isFromSMS = getIntent().getBooleanExtra("sms", false);
            gagebu = getIntent().getParcelableExtra("gagebu");
            MyLog.d(TAG, "gagebu name : " + gagebu.getName());
            MyLog.d(TAG, "_id : " + gagebu.get_id());
            cal.setTimeInMillis(Long.valueOf(gagebu.getDate()));

            relDelete.setVisibility(View.VISIBLE);

            if(gagebu.getMoney() >= 0){ // income
                isIncome = true;
                // 롤리팝 이상이면 status bar 색상 변경
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    this.getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.income));
                }
                relTitle.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.income));
                txtTitle.setText(getString(R.string.income));
            } else { // consume
                isIncome = false;
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    this.getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.consume));
                }
                relTitle.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.consume));
                txtTitle.setText(getString(R.string.consume));
            }
            gagebuCategories = TableGagebuCategory.getInstance(context).selectCategories(isIncome);
            spinCategory.setAdapter(spinAdapter = new SpinnerCategoryAdapter(this, gagebuCategories));
            for (int i=0;i<spinCategory.getCount();i++){
                // MyLog.d(TAG, "spinnercategories : " + gagebuCategories.get(i).getCategory() + ", gagebuCategories : " + gagebu.getCategory());
                if (gagebuCategories.get(i).getCategory().equals(gagebu.getCategory())){
                    spinCategory.setSelection(i);
                    break;
                }
            }
            editCost.setText(String.valueOf(Math.abs(gagebu.getMoney())));
            editDetail.setText(gagebu.getDetail());
            editAddress.setText(gagebu.getAddress());

            if(gagebu.getLocationX() != 0.0){
                imgLocation.setColorFilter(R.color.colorSubPrimary);
            }
            if(!Function.getInstance(context).isEmptyStr(gagebu.getImg())){
                imgCamera.setColorFilter(R.color.colorSubPrimary);
                try{
                    File tmpPath = new File(Environment.getExternalStorageDirectory(), "/dontech");
                    File myPath = new File(tmpPath, gagebu.getImg());
                    img = gagebu.getImg();
                    if (myPath.exists() && gagebu.getImg() != null && !gagebu.getImg().equals("")) {
                        MyLog.d(TAG, "imgExists : " + myPath);
                        // 파일이 있으면 imgView에 띄우기
                        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                        bitmap = BitmapFactory.decodeFile(myPath.getAbsolutePath(), bmOptions);
                        imgView.setImageBitmap(bitmap);
                        imgCamera.setColorFilter(R.color.colorPrimary);
                    }
                } catch (NullPointerException ne){
                    MyLog.d(TAG, "NullPointerException : " + ne.getMessage());
                }
            }
        }

        // 날짜 입력
        txtDate.setText(
                cal.get(Calendar.YEAR) + getResources().getString(R.string.year) + " " +
                        (cal.get(Calendar.MONTH)+1) + getResources().getString(R.string.month) + " " +
                        cal.get(Calendar.DAY_OF_MONTH) + getResources().getString(R.string.day) + " " +
                        getResources().getString(R.string.arr_down));

        txtTime.setText(String.format(Locale.KOREA, "%02d", cal.get(Calendar.HOUR_OF_DAY)) + " : " +
                String.format(Locale.KOREA, "%02d", cal.get(Calendar.MINUTE)));
    }

    @OnClick({R.id.rel_back, R.id.rel_save, R.id.rel_delete, R.id.txt_date, R.id.txt_time, R.id.rel_location, R.id.rel_camera, R.id.img_view})
    void onClick(View view){
        switch (view.getId()){
            case R.id.rel_back:
                onBackPressed();
                overridePendingTransition(0, R.anim.slide_out_bottom);
                break;
            case R.id.rel_delete:
                tmpPath = new File(Environment.getExternalStorageDirectory(), "/dontech");
                try{
                    File myPath = new File(tmpPath, TableGagebu.getInstance(context).selectGagebu(gagebu.get_id()).getImg());
                    MyLog.d(TAG, "file delete : " + myPath);
                    if (myPath.exists()) {
                        // 파일이 존재하면 삭제
                        myPath.delete();
                    }
                } catch (NullPointerException ne){
                    MyLog.d(TAG, "null pointer exception");
                } catch (CursorIndexOutOfBoundsException ce){
                    MyLog.d(TAG, "cursor out of bound exception");
                } finally {
                    MyLog.d(TAG, "delete : " + gagebu.get_id());
                    TableGagebu.getInstance(context).deleteGagebu(gagebu.get_id());
                    CustomToast.getInstance(context).createToast(getResources().getString(R.string.deleted));
                    /** 가계부 리턴 **/
                    returnedGagebuIntent = new Intent();
                    returnedGagebuIntent.setAction(Intent.ACTION_DELETE);
                    returnedGagebuIntent.putExtra("gagebu", gagebu);

                    /** sms receiver로 부터 실행 되었으면 **/
                    if (isFromSMS)
                        BusProvider.getInstance().post(new GagebuUpdateEvent(Intent.ACTION_DELETE, gagebu));

                    setResult(RESULT_OK, returnedGagebuIntent);
                    onBackPressed();
                }
                break;
            case R.id.rel_save:
                // TODO :
                // 가격을 입력했는지 체크
                if(editCost.getText().toString().equals("")){
                    CustomToast.getInstance(context).createToast(getResources().getString(R.string.plz_write_cost));
                    return;
                }
                // 가격이 올바로 입력됐는지 체크
                else if(Integer.valueOf(editCost.getText().toString()) <= 0){
                    CustomToast.getInstance(context).createToast(getResources().getString(R.string.plz_verify_cost));
                    return;
                }
                // 1. 기존에 사진이 있고 이미지가 안 바꼈으면 걍 값만 바꾸고 넘어감
                if(isUpdate && imgView != null && !isImgChanged){
                    gagebu= new Gagebu(gagebu.get_id(), AppFunction.getInstance(context).getCurrentGagebuBook(),
                            String.valueOf(cal.getTimeInMillis()), spinAdapter.getItem(spinCategory.getSelectedItemPosition()).getCategory(),
                            Integer.valueOf(editCost.getText().toString()), editDetail.getText().toString(), editAddress.getText().toString(),
                            locationX, locationY, img
                    );
                    saveDB(context, gagebu, isUpdate, isIncome);
                    return;
                // 2. 기존에 사진이 있고 이미지가 바꼈으면 삭제
                } else if (isUpdate && imgView != null && isImgChanged){
                    try{
                        tmpPath = new File(Environment.getExternalStorageDirectory(), "/dontech");
                        File myPath = new File(tmpPath, img);
                        if (myPath.exists()) {
                            myPath.delete();
                        }
                    } catch (NullPointerException ne){
                        MyLog.d(TAG, "no file exists to delete");
                    }
                }
                // 3. 오브젝트 저장
                MyLog.d(TAG, "spinCategory selected : " + spinAdapter.getItem(spinCategory.getSelectedItemPosition()).getCategory());
                gagebu= new Gagebu((gagebu != null) ? gagebu.get_id() : 0, AppFunction.getInstance(context).getCurrentGagebuBook(),
                        String.valueOf(cal.getTimeInMillis()), spinAdapter.getItem(spinCategory.getSelectedItemPosition()).getCategory(),
                        Integer.valueOf(editCost.getText().toString()), editDetail.getText().toString(), editAddress.getText().toString(),
                        locationX, locationY, img
                );
                // 4. 사진이 있으면 저장
                if(imgView.getDrawable() != null){
                    String timeStamp;
                    try{
                        timeStamp = new SimpleDateFormat("YYYY-MM-DD HH:MM:SS.SSS", Locale.KOREA).format(new Date());
                    } catch (IllegalArgumentException ie){
                        MyLog.d(TAG, "ie : " + ie.getMessage());
                        timeStamp = new SimpleDateFormat("YYYY-MM-DD HH:MM:SS.SSS", Locale.ENGLISH).format(new Date());
                    }
                    gagebu.setImg("IMG_" + timeStamp + ".jpg");
                    Bitmap bitmap = ((BitmapDrawable) imgView.getDrawable()).getBitmap();
                    new SaveImgTask(context, gagebu.getImg(), bitmap, new SaveImgTask.OnTaskCompleted() {
                        @Override
                        public void onTaskCompleted(String result) {
                            saveDB(context, gagebu, isUpdate, isIncome);
                        }
                    }).execute(null, null, null);
                } else{
                    saveDB(context, gagebu, isUpdate, isIncome);
                }
                break;
            case R.id.txt_date:
                Intent dateIntent = new Intent(this, DatePickerDialog.class);
                dateIntent.putExtra("year", cal.get(Calendar.YEAR));
                dateIntent.putExtra("month", cal.get(Calendar.MONTH));
                dateIntent.putExtra("day", cal.get(Calendar.DAY_OF_MONTH));
                dateIntent.putExtra("show_day", true);
                startActivityForResult(dateIntent, MyNumber.REQ_DATEPICKER_ACT);
                break;
            case R.id.txt_time:
                Intent timeIntent = new Intent(this, TimePickerDialog.class);
                timeIntent.putExtra("hour", cal.get(Calendar.HOUR_OF_DAY));
                timeIntent.putExtra("minute", cal.get(Calendar.MINUTE));
                startActivityForResult(timeIntent, MyNumber.REQ_TIMEPICKER_ACT);
                break;
            case R.id.rel_location:
                if(PermissionFunction.getInstance(context).isLocationPermissionGranted(activity)){
                    Intent locIntent = new Intent(getApplicationContext(), MapsActivity.class);
                    if(isUpdate){
                        locIntent.putExtra("lon", locationY);
                        locIntent.putExtra("lat", locationX);
                    }
                    startActivityForResult(locIntent, MyNumber.REQ_LOCATION_ACT);
                }
                break;
            case R.id.rel_camera:
                if(PermissionFunction.getInstance(context).isStoragePermissionGranted(activity)){
                    dialogPhotoActPicker = new DialogPhotoActPicker();
                    dialogPhotoActPicker.show(getSupportFragmentManager(), "fragment_pick_photo_act");
                    Bundle args = new Bundle();
                    args.putString("content", getString(R.string.pick_photo_act));
                    dialogPhotoActPicker.setArguments(args);
                    dialogPhotoActPicker.setDialogBtn(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // 사진 찍기
                                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    File photoPath = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DOWNLOADS + "/dontech.jpg");
                                    Uri photoURI = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", photoPath);
                                    cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoURI);
                                    startActivityForResult(cameraIntent, MyNumber.REQ_PHOTO_ACT);
                                }
                            },
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // 사진 고르기
                                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                                    photoPickerIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    photoPickerIntent.setType("image/*");
                                    startActivityForResult(photoPickerIntent, MyNumber.REQ_IMAGE_ACT);
                                }
                            }
                    );
                }
                break;
            case R.id.img_view:
                if(img != null){
                    Intent intent = new Intent(context, FullScreenImageActivity.class);
                    // TODO : pass imgs
                    intent.putExtra("img", img);
                    startActivity(intent);
                }
                break;
        }
    }

    @OnLongClick({R.id.rel_camera, R.id.rel_location})
    boolean onLongClick(View view){
        switch (view.getId()){
            case R.id.rel_camera:
                createCameraDeleteDialog();
                break;
            case R.id.rel_location:
                createAddressDeleteDialog();
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case MyNumber.REQ_DATEPICKER_ACT:
                    cal.set(
                            data.getIntExtra("year", Calendar.getInstance().get(Calendar.YEAR)),
                            data.getIntExtra("month", Calendar.getInstance().get(Calendar.MONTH)),
                            data.getIntExtra("day", Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
                    );
                    txtDate.setText(
                            cal.get(Calendar.YEAR) + getResources().getString(R.string.year) + " " +
                                    (cal.get(Calendar.MONTH) + 1) + getResources().getString(R.string.month) + " " +
                                    cal.get(Calendar.DAY_OF_MONTH) + getResources().getString(R.string.day) + " " +
                                    getResources().getString(R.string.arr_down));
                break;
                case MyNumber.REQ_TIMEPICKER_ACT:
                    cal.set(Calendar.HOUR_OF_DAY, data.getIntExtra("hour", Calendar.getInstance().get(Calendar.HOUR_OF_DAY)));
                    cal.set(Calendar.MINUTE, data.getIntExtra("minute", Calendar.getInstance().get(Calendar.MINUTE)));
                    txtTime.setText(String.format(Locale.KOREA, "%02d", cal.get(Calendar.HOUR_OF_DAY)) + " : " +
                            String.format(Locale.KOREA, "%02d", cal.get(Calendar.MINUTE)));
                    break;
                case MyNumber.REQ_LOCATION_ACT:
                    imgLocation.setColorFilter(R.color.colorPrimary);
                    locationY = data.getDoubleExtra("lon", 0.0);
                    locationX = data.getDoubleExtra("lat", 0.0);
                    address = data.getStringExtra("address");
                    editAddress.setText(address);
                    if (address != null && !address.equals("")) {
                        // 위치 정보가 있으면 색상칠하고 롱클릭 달기
                        imgLocation.setColorFilter(R.color.colorPrimary);
                    }
    /*
                        String msg = "주소 : " + address + "\nlon: "+locationY+"\nlat: "+locationX;
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();*/
                    break;
                case MyNumber.REQ_PHOTO_ACT:
                    if(resultCode == RESULT_OK) {
                        imgCamera.setColorFilter(R.color.colorPrimary);
                        isImgChanged = true;

                        // 카메라에서 받아지는 이미지가 작기 때문에 걸러주지 않고 바로 불러온다.
                        try {
                            // 썸네일 받아오기 : Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                            File photoPath = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DOWNLOADS + "/dontech.jpg");
                            Uri photoURI = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", photoPath);
                            bitmap = Function.getInstance(context).getBitmapFromUri(photoURI);
                            Log.d(TAG, "width : " + bitmap.getWidth() + ", height : " + bitmap.getHeight());
                            if((bitmap.getHeight() > IMG_SIZE && bitmap.getHeight() >= bitmap.getWidth()) ||
                                    (bitmap.getWidth() > IMG_SIZE && bitmap.getWidth() >= bitmap.getHeight())) {
                                int height;
                                int width;
                                if (bitmap.getHeight() > bitmap.getWidth()) {
                                    height = IMG_SIZE;
                                    double div = bitmap.getWidth() / (double) bitmap.getHeight();
                                    width = (int) (IMG_SIZE * div);
                                } else {
                                    width = IMG_SIZE;
                                    double div = bitmap.getHeight() / (double) bitmap.getWidth();
                                    height = (int) (IMG_SIZE * div);
                                }
                                Bitmap resized = Bitmap.createScaledBitmap(bitmap, width, height, true);
                                imgView.setImageBitmap(resized);
                            } else{
                                imgView.setImageBitmap(bitmap);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if(dialogPhotoActPicker != null) dialogPhotoActPicker.dismiss();
                    }
                    break;
                case MyNumber.REQ_IMAGE_ACT:
                    if(resultCode == RESULT_OK){
                        imgCamera.setColorFilter(R.color.colorPrimary);
                        isImgChanged = true;
                        final Uri imageUri = data.getData();
                        try{
                            bitmap = Function.getInstance(context).getBitmapFromUri(imageUri);
                            Log.d(TAG, "width : " + bitmap.getWidth() + ", height : " + bitmap.getHeight());
                            if((bitmap.getHeight() > IMG_SIZE && bitmap.getHeight() >= bitmap.getWidth()) ||
                                    (bitmap.getWidth() > IMG_SIZE && bitmap.getWidth() >= bitmap.getHeight())) {
                                int height;
                                int width;
                                if (bitmap.getHeight() > bitmap.getWidth()) {
                                    height = IMG_SIZE;
                                    double div = bitmap.getWidth() / (double) bitmap.getHeight();
                                    width = (int) (IMG_SIZE * div);
                                    Log.d(TAG, "div : " + div);
                                } else {
                                    width = IMG_SIZE;
                                    double div = bitmap.getHeight() / (double)  bitmap.getWidth();
                                    height = (int) (IMG_SIZE * div);
                                }
                                Log.d(TAG, "width : " + width + ", height : " + height);
                                Bitmap resized = Bitmap.createScaledBitmap(bitmap, width, height, true);
                                imgView.setImageBitmap(resized);
                            } else {
                                imgView.setImageBitmap(bitmap);
                            }
                        } catch (IOException e){
                            e.printStackTrace();
                        }
                        if(dialogPhotoActPicker != null) dialogPhotoActPicker.dismiss();
                    }
                    break;
            }
        }

    }

    // edittext textwatcher
    private TextWatcher myTextWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if (editCost.getText().hashCode() == s.hashCode()) {
                if(!editCost.getText().toString().equals("")){
                    imgCostLbl.setColorFilter(R.color.colorPrimary);
                    editCost.setSelection(editCost.getText().length());
                } else {
                    imgCostLbl.clearColorFilter();
                }
            }
            else if (editDetail.getText().hashCode() == s.hashCode()) {
                if(!editDetail.getText().toString().equals("")){
                    imgDetailLbl.setColorFilter(R.color.colorPrimary);
                } else {
                    imgDetailLbl.clearColorFilter();
                }
            }
            else if (editAddress.getText().hashCode() == s.hashCode()){
                if(!editAddress.getText().toString().equals("")){
                    imgAddrLbl.setColorFilter(R.color.colorPrimary);
                } else {
                    imgAddrLbl.clearColorFilter();
                }
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void afterTextChanged(Editable s) {}
    };


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

    private void saveDB(Context context, Gagebu gagebu, boolean isUpdate, boolean isIncome){
        MyLog.d(TAG, "saveDB : img - " + gagebu.getImg());
        MyLog.d(TAG, "gagebu : " + gagebu.toString());
        returnedGagebuIntent = new Intent();

        if(isIncome)
            gagebu.setMoney(gagebu.getMoney());
        else
            gagebu.setMoney(-gagebu.getMoney());

        if(!isUpdate){
            returnedGagebuIntent.setAction(Intent.ACTION_INSERT);
            gagebu.set_id(TableGagebu.getInstance(context).insertGagebu(gagebu));
        }
        else {
            returnedGagebuIntent.setAction(Intent.ACTION_EDIT);
            TableGagebu.getInstance(context).updateGagebu(gagebu.get_id(), gagebu);
        }

        /** sms receiver로 부터 실행 되었으면 **/
        if (isFromSMS)
            BusProvider.getInstance().post(new GagebuUpdateEvent(Intent.ACTION_EDIT, gagebu));

        /** 가계부 리턴 **/
        returnedGagebuIntent.putExtra("gagebu", gagebu);
        setResult(RESULT_OK, returnedGagebuIntent);
        CustomToast.getInstance(context).createToast(getResources().getString(R.string.saved));
        onBackPressed();
        overridePendingTransition(0, R.anim.slide_out_bottom);
    }

    private void createCameraDeleteDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // 여기서 부터는 알림창의 속성 설정
        builder.setTitle(getString(R.string.delete_img))        // 제목 설정
                .setMessage(getString(R.string.ask_delete_img))        // 메세지 설정
                .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    // 확인 버튼 클릭시 설정
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (isUpdate) {
                            tmpPath = new File(Environment.getExternalStorageDirectory(), "/dontech");
                            try{
                                File myPath = new File(tmpPath, TableGagebu.getInstance(context).selectGagebu(gagebu.get_id()).getImg());
                                if (myPath.exists()) {
                                    // 파일이 존재하면 삭제
                                    myPath.delete();
                                }
                            } catch (NullPointerException ne){
                                Log.d(TAG, ne.getMessage());
                            }
                        }
                        imgCamera.clearColorFilter();
                        imgView.setImageBitmap(null);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    // 취소 버튼 클릭시 설정
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = builder.create();    // 알림창 객체 생성
        dialog.show();    // 알림창 띄우기
    }

    private void createAddressDeleteDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // 여기서 부터는 알림창의 속성 설정
        builder.setTitle(getString(R.string.delete_address))        // 제목 설정
                .setMessage(getString(R.string.ask_delete_address))        // 메세지 설정
                .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    // 확인 버튼 클릭시 설정
                    public void onClick(DialogInterface dialog, int whichButton) {
                        locationX = 0.0;
                        locationY = 0.0;
                        address = "";
                        editAddress.setText("");
                        imgLocation.clearColorFilter();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    // 취소 버튼 클릭시 설정
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = builder.create();    // 알림창 객체 생성
        dialog.show();    // 알림창 띄우기
    }
}
