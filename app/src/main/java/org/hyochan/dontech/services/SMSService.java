package org.hyochan.dontech.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import org.hyochan.dontech.SMSAidlInterface;
import org.hyochan.dontech.receivers.SMSReceiver;
import org.hyochan.dontech.utils.AlarmUtil;
import org.hyochan.dontech.utils.MyLog;

public class SMSService extends Service {

    private final static String TAG = "SMSService";

    private BroadcastReceiver mReceiver;

    public SMSService() {
    }

    @Override
    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
        return super.bindService(service, conn, flags);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private SMSAidlInterface.Stub binder = new SMSAidlInterface.Stub() {

        @Override
        public boolean isServiceStarted() throws RemoteException {
            return (mReceiver != null);
        }

    };


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MyLog.d(TAG, "my service started : " + startId);

        if(mReceiver == null){
            mReceiver = new SMSReceiver();
            MyLog.d(TAG, "sms receiver registered");
            registerReceiver(mReceiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
        }

        /**
         * startForeground 를 사용하면 notification 을 보여주어야 하는데 없애기 위한 코드
         */
/*
        startForeground(1,new Notification());

        NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        Notification notification = new Notification.Builder(getApplicationContext())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("")
                .setContentText("")
                .build();
        nm.notify(startId, notification);
        nm.cancel(startId);
*/

        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AlarmUtil.getInstance(getApplicationContext()).unregisterMyServiceAlarm();
    }

    @Override
    public void onDestroy() {
        MyLog.d(TAG, "onDestroy");
        if(mReceiver != null){
            MyLog.d(TAG, "unregister sms receiver");
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        MyLog.i(TAG, "onTaskRemoved");
        AlarmUtil.getInstance(getApplicationContext()).unregisterMyServiceAlarm();
        AlarmUtil.getInstance(getApplicationContext()).registerMyServiceAlarm(1);
        super.onTaskRemoved(rootIntent);
    }
}
