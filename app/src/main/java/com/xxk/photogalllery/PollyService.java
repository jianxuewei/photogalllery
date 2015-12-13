package com.xxk.photogalllery;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.provider.Settings;
import android.util.Log;

public class PollyService extends IntentService {


    private static final int POLLY_INTERVAL=500;

    public PollyService() {
        super("PollyService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("PollyService", "PollyService is created.");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent,flags,startId);
        Log.i("PollyService", "PollyService onStartCommand.");
        return Service.START_STICKY;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ConnectivityManager connectivityManager= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isNetworkAvailbale=connectivityManager.getBackgroundDataSetting()&&connectivityManager.getActiveNetworkInfo()!=null;
        if(!isNetworkAvailbale){
            Log.i("PollyService","Network is not availbale");
        }else {
            Log.i("PollyService","Network is availbale");
        }
        Log.i("PollyService", "PollyService onHandleIntent.");


        Resources r =getResources();
        PendingIntent pi= PendingIntent.getActivity(this,0,new Intent(this,ActivityPhotoGallery.class),0);
        Notification notification=new Notification.Builder(this)
                .setTicker(r.getString(R.string.new_pictures_title))
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle(r.getString(R.string.new_pictures_title))
                .setContentText(r.getString(R.string.new_picture_text))
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();
        NotificationManager manager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(0,notification);
    }

    public static void setAlarmService(Context context,boolean isOn){
        AlarmManager am= (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent=new Intent(context,PollyService.class);
        PendingIntent pi= PendingIntent.getService(context,0,intent,0);
        if (isOn){
            am.setRepeating(AlarmManager.RTC, System.currentTimeMillis(),POLLY_INTERVAL,pi);
        }else {
            am.cancel(pi);
            pi.cancel();
        }

    }
}
