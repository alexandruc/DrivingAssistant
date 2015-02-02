package com.alexandruc.drivingassistant.core.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.alexandruc.drivingassistant.MainActivity;
import com.alexandruc.drivingassistant.R;

public class LocalService extends Service {
    private NotificationManager mNM;

    private int NOTIFICATION = R.string.service_name;

    public class LocalBinder extends Binder {
        LocalService getService() {
            return LocalService.this;
        }
    }

    @Override
    public void onCreate() {
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        showNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mNM.cancel(NOTIFICATION);

        // Tell the user we stopped.
        Toast.makeText(this, R.string.service_name + " stopped", Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        CharSequence text = getText(R.string.service_name);

        Notification notification = new Notification.Builder(getBaseContext())
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Handling calls")
                .setSmallIcon(R.drawable.drive_assistant_white)
                //.setLargeIcon(R.drawable.drive_assistant_white)
                .build();

        notification.flags |= Notification.FLAG_ONGOING_EVENT; //TODO: FLAG_FOREGROUND_SERVICE

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        // Send the notification.
        mNM.notify(NOTIFICATION, notification);
    }
}
