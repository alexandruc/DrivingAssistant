package com.alexandruc.drivingassistant.core.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.alexandruc.drivingassistant.MainActivity;
import com.alexandruc.drivingassistant.R;

public class LocalService extends Service {
    private NotificationManager mNM;
    private static boolean mbIsRunning = false;

    private int NOTIFICATION = R.string.service_name;

    @Override
    public void onCreate() {
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        Log.i("LocalService", "Service started");
        mbIsRunning = true;
        showNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mNM.cancel(NOTIFICATION);

        // Tell the user we stopped.
        Log.i("LocalService", "Service stopped");
        mbIsRunning = false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; //mBinder;
    }

    public static boolean isServiceRunning(){
        return mbIsRunning;
    }

    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        CharSequence text = getText(R.string.service_name);

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

        Notification notification = new Notification.Builder(getBaseContext())
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Handling calls")
                .setSmallIcon(R.drawable.drive_assistant_white)
                //.setLargeIcon(R.drawable.drive_assistant_white)
                .setContentIntent(contentIntent)
                .build();

        notification.flags |= Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR; //TODO: FLAG_FOREGROUND_SERVICE

        // Send the notification.
        mNM.notify(NOTIFICATION, notification);
    }
}
