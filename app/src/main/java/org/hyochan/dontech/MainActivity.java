package org.hyochan.dontech;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fsn.cauly.CaulyAdInfo;
import com.fsn.cauly.CaulyAdInfoBuilder;
import com.fsn.cauly.CaulyAdView;
import com.fsn.cauly.CaulyCloseAd;
import com.fsn.cauly.CaulyCloseAdListener;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.squareup.otto.Subscribe;

import org.hyochan.dontech.activities.AddUpdateGagebuActivity;
import org.hyochan.dontech.activities.StatisticsActivity;
import org.hyochan.dontech.activities_dialogs.DatePickerDialog;
import org.hyochan.dontech.adapters.FragPagerAdapter;
import org.hyochan.dontech.models.DateManager;
import org.hyochan.dontech.events.DashboardSlideEvent;
import org.hyochan.dontech.events.GagebuBookSelectedEvent;
import org.hyochan.dontech.events.GagebuUpdateEvent;
import org.hyochan.dontech.events.GagebuBookUpdateEvent;
import org.hyochan.dontech.fragments.MonthlyFragment;
import org.hyochan.dontech.fragments.GagebuFragment;
import org.hyochan.dontech.functions.AppFunction;
import org.hyochan.dontech.global_variables.MyNumber;
import org.hyochan.dontech.global_variables.MyString;
import org.hyochan.dontech.models.Gagebu;
import org.hyochan.dontech.preferences.AppPref;
import org.hyochan.dontech.services.SMSService;
import org.hyochan.dontech.utils.BusProvider;
import org.hyochan.dontech.functions.Function;
import org.hyochan.dontech.utils.MyLog;
import org.hyochan.dontech.utils.MyViewPager;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity{

    private final String TAG = "MainActivity";

    private Activity activity;
    private Context context;
    private Intent intent;

    @BindView(R.id.rel_hamburger) RelativeLayout relHamburger;
    @BindView(R.id.rel_dashboard) RelativeLayout relDashboard;
    @BindView(R.id.rel_main) RelativeLayout relMain;
    @BindView(R.id.txt_date) TextView txtDate;
    @BindView(R.id.txt_gagebu_name) TextView txtGagebuName;
    @BindView(R.id.fam) FloatingActionsMenu fam;
    @BindView(R.id.fab_plus) FloatingActionButton fabPlus;
    @BindView(R.id.fab_minus) FloatingActionButton fabMinus;
    @BindView(R.id.fab_statistics) FloatingActionButton fabStatistics;
    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    @BindView(R.id.view_pager) MyViewPager viewPager;
    private Fragment calendarFragment;
    private Fragment listsFragment;
    private ArrayList<Fragment> arrFragments;
    private FragmentPagerAdapter fragPagerAdapter;
    private int selectedFragment = 0;
    private BroadcastReceiver receiver;

    private float lastTranslate = 0.0f;

    // Cauly 광고
    @BindView(R.id.cauly_ad_view) CaulyAdView caulyAdView;
    CaulyCloseAd caulyCloseAd;

    private SMSAidlInterface smsAidlInterface;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // AIDL 인터페이스
            smsAidlInterface = SMSAidlInterface.Stub.asInterface(service);
            MyLog.d(TAG, "service connected");
            try{
                MyLog.d(TAG, "service status : " + smsAidlInterface.isServiceStarted());
                if(!smsAidlInterface.isServiceStarted()) {
                    startService(new Intent(getApplicationContext(), SMSService.class));
                }
            } catch (RemoteException e){
                MyLog.d(TAG, "remote exception : " + e.getMessage());
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            MyLog.i(TAG, "service disconnected");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        activity = this;
        context = this;

        BusProvider.getInstance().register(this);

        Intent intent = new Intent(this, SMSService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        AppPref.getInstance(context).put("badge", 0);

        // 문자로 입력되는 값을 반영해주기 위한 리시버 등록
        IntentFilter filter = new IntentFilter();
        filter.addAction("SMS_AMOUNT_CHANGED");
        receiver = new SMSParseReceiver();
        registerReceiver(receiver, filter);

        // 메인 페이지 세팅
        setMainViews();

        // 카울리
        // Cauly ad : Close
        caulyAdView.setVisibility(View.GONE);
        final CaulyAdInfo adInfo1 = new CaulyAdInfoBuilder("s5oEaycX").build();
        caulyCloseAd = new CaulyCloseAd();
        caulyCloseAd.setButtonText(getString(R.string.cancel), getString(R.string.yes));
        caulyCloseAd.setDescriptionText(getString(R.string.ask_exit));
        caulyCloseAd.setAdInfo(adInfo1);
        caulyCloseAd.setCloseAdListener(new CaulyCloseAdListener() {
            @Override
            public void onReceiveCloseAd(CaulyCloseAd caulyCloseAd, boolean b) {
            }

            @Override
            public void onShowedCloseAd(CaulyCloseAd caulyCloseAd, boolean b) {
            }

            @Override
            public void onFailedToReceiveCloseAd(final CaulyCloseAd caulyCloseAd, int i, String s) {
                MyLog.d(TAG, "FAILED TO RECEIVE CLOSE AD : " + i + ", " + s);
            }

            @Override
            public void onLeftClicked(CaulyCloseAd caulyCloseAd) {
            }

            @Override
            public void onRightClicked(CaulyCloseAd caulyCloseAd) {
                finish();
            }

            @Override
            public void onLeaveCloseAd(CaulyCloseAd caulyCloseAd) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(relDashboard)){
            drawerLayout.closeDrawer(relDashboard);
            return;
        } else {
            if(fam.isExpanded()){
                fam.collapse();
                return;
            } else {
                if (caulyCloseAd.isModuleLoaded()){
                    MyLog.d(TAG, "show caulyCloseAd");
                    caulyCloseAd.show(this);
                    return;
                }
            }
        }
        super.onBackPressed();
    }

    private void setMainViews(){
        txtGagebuName.setText(AppPref.getInstance(context).getValue(MyString.PREF_SELECTED_GAGEBU, ""));

        // 날짜 세팅
        if(Function.getInstance(context).isEmptyStr(txtDate.getText().toString())){
            txtDate.setText(
                    DateManager.getInstance().getYear() + " / " + (DateManager.getInstance().getMonth()+1) + " " +
                            getResources().getString(R.string.arr_down));
        }

        // drawer setting
        drawerLayout.addDrawerListener(drawerListener);
        drawerLayout.closeDrawer(relDashboard);

        // Floating Button 세팅
        fabPlus.setSize(FloatingActionButton.SIZE_NORMAL);
        fabPlus.setColorNormalResId(R.color.blue_n);
        fabPlus.setColorPressedResId(R.color.blue_s);
        fabPlus.setIcon(R.drawable.ic_add);
        fabPlus.setStrokeVisible(false);

        fabMinus.setSize(FloatingActionButton.SIZE_NORMAL);
        fabMinus.setIcon(R.drawable.ic_sub);
        fabMinus.setColorNormalResId(R.color.pink_n);
        fabMinus.setColorPressedResId(R.color.pink_s);
        fabMinus.setStrokeVisible(false);

        fabStatistics.setSize(FloatingActionButton.SIZE_NORMAL);
        fabStatistics.setIcon(R.drawable.ic_analytics);
        fabStatistics.setStrokeVisible(false);

        fam.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                caulyAdView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onMenuCollapsed() {
                caulyAdView.setVisibility(View.GONE);
            }
        });

        // 뷰 페이저 세팅
        calendarFragment = new MonthlyFragment();
        listsFragment = new GagebuFragment();
        arrFragments = new ArrayList<>();
        arrFragments.add(calendarFragment);
        arrFragments.add(listsFragment);

        fragPagerAdapter = new FragPagerAdapter(getSupportFragmentManager(), getApplicationContext(), arrFragments);
        viewPager.setAdapter(fragPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
            }

            @Override
            public void onPageSelected(int i) {
                selectItem(i);
                selectedFragment = i;
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onDestroy() {
        if(receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
        try{
            unbindService(serviceConnection);
        } catch (IllegalArgumentException e){
            Log.i(TAG ,"exception : " +  e.getMessage());
        }
        BusProvider.getInstance().unregister(this);
        super.onDestroy();
    }

    // set main Fragment
    private void selectItem(int position) {
        // update the main content by replacing fragments
        switch (position){
            case MyNumber.FRAGMENT_FIRST:
                selectedFragment = MyNumber.FRAGMENT_FIRST;
                break;
            case MyNumber.FRAGMENT_SECOND:
                selectedFragment = MyNumber.FRAGMENT_SECOND;
                break;
        }
        // setAmount();
    }

    @OnClick(R.id.txt_date) void onClickToChangeDate(){
        Intent intent = new Intent(this, DatePickerDialog.class);
        intent.putExtra("year", DateManager.getInstance().getYear());
        intent.putExtra("month", DateManager.getInstance().getMonth());
        intent.putExtra("show_day", false);
        startActivityForResult(intent, MyNumber.REQ_DATEPICKER_ACT);
        overridePendingTransition(R.anim.slide_from_top, 0);
    }

    @OnClick({R.id.fab_minus, R.id.fab_plus, R.id.fab_statistics})
    void onFabClicked(View view) {
        switch (view.getId()){
            case R.id.fab_plus:
                // Toast.makeText(getApplicationContext(), "fab plus", Toast.LENGTH_SHORT).show();
                intent = new Intent(this, AddUpdateGagebuActivity.class);
                intent.setAction(Intent.ACTION_INSERT);
                intent.putExtra("is_income", true);
                startActivityForResult(intent, MyNumber.REQ_GAGEBU_ADD_UPDATE);
                break;
            case R.id.fab_minus:
                // Toast.makeText(getApplicationContext(), "fab minus", Toast.LENGTH_SHORT).show();
                intent = new Intent(this, AddUpdateGagebuActivity.class);
                intent.setAction(Intent.ACTION_INSERT);
                intent.putExtra("is_income", false);
                startActivityForResult(intent, MyNumber.REQ_GAGEBU_ADD_UPDATE);
                break;
            case R.id.fab_statistics:
                // CustomToast.getInstance(context).createToast(getString(R.string.preparing_for_next_update));
                startActivity(new Intent(context, StatisticsActivity.class));
                break;
        }
    }

    @OnClick(R.id.rel_hamburger)
    void onHamburgerClicked(){
        if(drawerLayout.isDrawerOpen(relDashboard))
            drawerLayout.closeDrawer(relDashboard);
        else
            drawerLayout.openDrawer(relDashboard);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(fam != null && fam.isExpanded()) fam.collapse();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        MyLog.d(TAG, "requestCode : " + requestCode + ", resultCode : " + resultCode);
        if(resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case MyNumber.REQ_GAGEBU_BOOK_ADD_UPDATE:
                    GagebuBookUpdateEvent gagebuBookUpdateEvent = new GagebuBookUpdateEvent(data.getAction(), data.getStringExtra("name"));
                    if(!Function.getInstance(context).isEmptyStr(data.getStringExtra("before_name"))){
                        gagebuBookUpdateEvent.setBeforeName(data.getStringExtra("before_name"));
                        // 현재 가계부 북으로 선택된 북명이 변경되었으면 current gagebu 변경
                        AppPref.getInstance(context).put(MyString.PREF_SELECTED_GAGEBU, gagebuBookUpdateEvent.getName());
                        txtGagebuName.setText(data.getStringExtra("name"));
                    }
                    BusProvider.getInstance().post(gagebuBookUpdateEvent);
                    break;
                case MyNumber.REQ_DATEPICKER_ACT:
                    if(resultCode == RESULT_OK){
                        DateManager.getInstance().setYear(data.getIntExtra("year", Calendar.getInstance().get(Calendar.YEAR)));
                        DateManager.getInstance().setMonth(data.getIntExtra("month", Calendar.getInstance().get(Calendar.MONTH)));
                        // sendFragmentsChangedData(getTitle().toString());
                        BusProvider.getInstance().post(new GagebuBookSelectedEvent(txtGagebuName.getText().toString()));
                    }
                    break;
                case MyNumber.REQ_GAGEBU_ADD_UPDATE:
                    String action = data.getAction();
                    MyLog.d(TAG, "action : " + action);
                    Gagebu gagebu = data.getParcelableExtra("gagebu");
                    BusProvider.getInstance().post(new GagebuUpdateEvent(data.getAction(), gagebu));
                    break;
                case MyNumber.REQ_RESTORE_ACT:
                    MyLog.d(TAG, "Restored from backup");
                    setMainViews();
                    // dashboard gagebu fragment 새로 고침
                    BusProvider.getInstance().post(new GagebuBookUpdateEvent("", ""));
                    break;
            }
        }
    }

    DrawerLayout.DrawerListener drawerListener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(View view, float v) {
            // Log.i(TAG, "onDrawerSlide : " + String.format("%.2f", v));
            float moveFactor = (relDashboard.getWidth() * v);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            {
                relHamburger.setRotation(moveFactor);
                relMain.setTranslationX(moveFactor);
            }
            else
            {
                TranslateAnimation transAnim = new TranslateAnimation(lastTranslate, moveFactor, 0.0f, 0.0f);
                transAnim.setDuration(0);
                transAnim.setFillAfter(true);
                relMain.startAnimation(transAnim);
                RotateAnimation rotateAnim = new RotateAnimation(lastTranslate, moveFactor);
                rotateAnim.setDuration(0);
                rotateAnim.setFillAfter(true);
                relHamburger.startAnimation(rotateAnim);

                lastTranslate = moveFactor;

            }
        }

        @Override
        public void onDrawerOpened(View view) {
            // Check if no view has focus: hiding keyboard
            Function.getInstance(context).hideKeyboard(activity);
        }

        @Override
        public void onDrawerClosed(View view) {
        }
        @Override
        public void onDrawerStateChanged(int i) {

        }
    };


    // 날짜 변경
    @Subscribe
    public void gagebuSelectedEvent(GagebuBookSelectedEvent event){
        MyLog.d(TAG, "gagebu selected : " + event.getName());
        MyLog.d(TAG, "year : " + DateManager.getInstance().getYear());
        MyLog.d(TAG, "month : " + DateManager.getInstance().getMonth());

        int year = DateManager.getInstance().getYear();
        int month = DateManager.getInstance().getMonth();

        if(month == 12){
            month = 1;
            year++;
        } else {
            month++;
        }

        txtDate.setText(year + " / " + month + " " + getResources().getString(R.string.arr_down));
    }

    // 가계부 북 변경
    @Subscribe
    public void dashboardUpdateEvent(DashboardSlideEvent event) {
        // TODO: React to the event somehow!
        if(event.getEvent().equals("close")){
            drawerLayout.closeDrawer(relDashboard);
        }
        // MonthlyFragment와 GagebuFragment에 가계부가 업데이트 되었음을 알린다.
        if(!txtGagebuName.getText().toString().equals(event.getGagebuName())){
            AppFunction.getInstance(context).setCurrentGagebuBook(event.getGagebuName());
            BusProvider.getInstance().post(new GagebuBookSelectedEvent(event.getGagebuName()));
            txtGagebuName.setText(event.getGagebuName());
        }
    }

    private class SMSParseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("SMS_AMOUNT_CHANGED")){
                MyLog.d(TAG, "AmountChangedReceiver");
                Gagebu gagebu = intent.getParcelableExtra("gagebu");
                BusProvider.getInstance().post(new GagebuUpdateEvent(Intent.ACTION_INSERT, gagebu));
            }

            // 이미 화면으로 진입상태라 배지를 표시할 필요없음.
            AppPref.getInstance(context).put("badge", 0);
        }
    }
}
