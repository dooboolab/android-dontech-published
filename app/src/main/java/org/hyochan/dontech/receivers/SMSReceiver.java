package org.hyochan.dontech.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.provider.Telephony;
import android.telephony.SmsMessage;

import org.hyochan.dontech.R;
import org.hyochan.dontech.activities.AddUpdateGagebuActivity;
import org.hyochan.dontech.database.TableGagebu;
import org.hyochan.dontech.functions.AppFunction;
import org.hyochan.dontech.global_variables.MyNumber;
import org.hyochan.dontech.models.Gagebu;
import org.hyochan.dontech.preferences.AppPref;
import org.hyochan.dontech.utils.MyLog;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SMSReceiver extends BroadcastReceiver{

    private static final String TAG = "SMSReceiver";

    public SMSReceiver() {
    }

    @Override
    public void onReceive(final Context context, Intent intent) {

        MyLog.d(TAG, "sms received");

        if (AppPref.getInstance(context).getValue(AppPref.SMS_PARSE, false)){

            final Gagebu gagebu = new Gagebu();
            gagebu.set_id(0);
            gagebu.setName(
                    AppPref.getInstance(context).getValue("sms_gagebu",
                    AppFunction.getInstance(context).getCurrentGagebuBook()));
            gagebu.setCategory(context.getResources().getString(R.string.others));
            gagebu.setAddress("");

            try{
                // SMS 메시지를 파싱합니다.
                SmsMessage[] smsMessage = Telephony.Sms.Intents.getMessagesFromIntent(intent);
                String format = intent.getStringExtra("format");

                // SMS 수신 시간 확인
                Date curDate = new Date(smsMessage[0].getTimestampMillis());
                MyLog.d("문자 수신 시간", curDate.toString());
                Calendar cal = Calendar.getInstance();
                cal.setTime(curDate);
                gagebu.setDate(String.valueOf(cal.getTimeInMillis()));

                // SMS 발신 번호 확인
                String strNumber = smsMessage[0].getOriginatingAddress();
                String message = smsMessage[0].getMessageBody();

                // 국민은행 번호
                // if(strNumber.contains("15776200") || strNumber.contains("98889987")){ // for debug
                if(!strNumber.contains("010") && (strNumber.contains("15776200"))){

                    // SMS 메시지 확인
                    message.replaceAll("\r\n", "\n");
                    MyLog.d(TAG, "line number : "+strNumber+", content : \n" + message);
                    String[] msgSplit = message.split("\n");

                    MyLog.d(TAG, "line numbers : " + msgSplit.length);

                    // 문자열이 7줄이 아니면 리턴
                    if(msgSplit.length != 7) return;

                    for(String str : msgSplit){
                        MyLog.d(TAG, "str : " + str);
                    }

                    // 금액 숫자로 변환
                    msgSplit[3] = msgSplit[3].replace(",","");
                    // gagebu.setMoney(Integer.valueOf(msgSplit[3].replaceAll("[\\D]", "")));
                    try {
                        gagebu.setMoney(NumberFormat.getInstance().parse(msgSplit[3]).intValue());
                        // 위에 주석은 뒷 숫자까지 다 나오는거고 이건 앞에꺼만 : dsa123as345 = 123
                    } catch (ParseException pe){
                        MyLog.d(TAG, "parse exception");
                        return;
                    }
                    MyLog.d(TAG, "분석 금액 : " + gagebu.getMoney());

                    // 항목 입력
                    gagebu.setDetail(msgSplit[4]);

                    // intent 생성
                    intent = new Intent(context, AddUpdateGagebuActivity.class);

                    int insertId = 0;
                    int reqCode = 0;
                    String action = "";
                    if(msgSplit[1].contains("승인")){
                        gagebu.setMoney(-gagebu.getMoney()); // 지출이므로 음수로 책정
                        insertId = TableGagebu.getInstance(context).insertGagebu(gagebu);
                        reqCode = MyNumber.REQ_GAGEBU_ADD_UPDATE;
                        action = context.getString(R.string.card_withdraw);
                    } else if (msgSplit[1].contains("취소")){
                        insertId = TableGagebu.getInstance(context).insertGagebu(gagebu);
                        reqCode = MyNumber.REQ_GAGEBU_ADD_UPDATE;
                        action = context.getString(R.string.card_deposit);
                    }
                    MyLog.d(TAG, "insert id : " + insertId);

                    if(insertId != 0){
                        // notification 생성
                        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        Notification.Builder mBuilder = new Notification.Builder(context);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            mBuilder.setSmallIcon(R.drawable.ic_credit_card_black_24px);
                        } else {
                            mBuilder.setSmallIcon(R.drawable.ic_credit_card_black_24px);
                        }

                        mBuilder.setWhen(System.currentTimeMillis());
                        mBuilder.setNumber(1);
                        mBuilder.setTicker(context.getResources().getString(R.string.app_name));
                        mBuilder.setContentTitle(context.getResources().getString(R.string.sms_data_received));
                        mBuilder.setContentText(gagebu.getDetail() + " : " + String.format(Locale.KOREA, "%,d", gagebu.getMoney()) + context.getString(R.string.won) + " " + action);
                        mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                        mBuilder.setAutoCancel(true);

                        intent.setAction(Intent.ACTION_EDIT);
                        gagebu.set_id(insertId);
                        intent.putExtra("gagebu", gagebu);
                        intent.putExtra("sms", true);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        PendingIntent pendingIntent = PendingIntent.getActivity(context, reqCode, intent,
                                PendingIntent.FLAG_UPDATE_CURRENT);
                        mBuilder.setContentIntent(pendingIntent);
                          nm.notify(111, mBuilder.build());

                        // main activity로 broadcast
                        Intent mainIntent = new Intent("SMS_AMOUNT_CHANGED");
                        mainIntent.putExtra("gagebu", gagebu);
                        context.sendBroadcast(mainIntent);

                        // badge broadcast
                        String launcherClassName = getLauncherClassName(context);
                        if (launcherClassName == null) {
                            return;
                        }
                        Intent badgeIntent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
                        int count = AppPref.getInstance(context).getValue("badge", 0) + 1;
                        AppPref.getInstance(context).put("badge", count);
                        badgeIntent.putExtra("badge_count", count);
                        badgeIntent.putExtra("badge_count_package_name", context.getPackageName());
                        badgeIntent.putExtra("badge_count_class_name", launcherClassName);
                        context.sendBroadcast(badgeIntent);
                    }
                }
            } catch (NullPointerException ne){
                MyLog.d(TAG, "null exception : " + ne.getMessage());
            }

        }
    }

    private String getLauncherClassName(Context context) {

        PackageManager pm = context.getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resolveInfos) {
            String pkgName = resolveInfo.activityInfo.applicationInfo.packageName;
            if (pkgName.equalsIgnoreCase(context.getPackageName())) {
                String className = resolveInfo.activityInfo.name;
                return className;
            }
        }
        return null;
    }
}