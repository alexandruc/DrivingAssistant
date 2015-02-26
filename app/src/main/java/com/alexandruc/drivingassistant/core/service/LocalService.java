package com.alexandruc.drivingassistant.core.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.alexandruc.drivingassistant.MainActivity;
import com.alexandruc.drivingassistant.R;
import com.alexandruc.drivingassistant.Utils.DataUtils;
import com.alexandruc.drivingassistant.bl.DriveAssistantController;

import java.util.HashMap;
import java.util.Map;

public class LocalService extends Service {
    private NotificationManager mNM;
    private TelephonyManager mTelephonyManager;
    private static boolean mbIsRunning = false;
    private DriveAssistantController mDrvController = new DriveAssistantController(this);

    //data passed from the main activity to the service
    private Map<String, Boolean> mBooleanOptions = new HashMap<>();
    private Map<String, String> mStringOptions = new HashMap<>();

    private int NOTIFICATION = R.string.service_name;

    @Override
    public void onCreate() {
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        mTelephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        mDrvController.initialize(mTelephonyManager);

        Log.i("LocalService", "Service started");
        showNotification();
        mbIsRunning = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId);
        //get the data sent to the service
        mBooleanOptions.put(getString(R.string.caller_id), intent.getBooleanExtra(getString(R.string.caller_id), false));
        mBooleanOptions.put(getString(R.string.busy_message), intent.getBooleanExtra(getString(R.string.busy_message), false));
        mBooleanOptions.put(getString(R.string.whitelist), intent.getBooleanExtra(getString(R.string.whitelist), false));
        mBooleanOptions.put(getString(R.string.blacklist), intent.getBooleanExtra(getString(R.string.blacklist), false));
        mStringOptions.put(DataUtils.busyMessageKey, intent.getStringExtra(DataUtils.busyMessageKey));

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mNM.cancel(NOTIFICATION);
        mDrvController.uninitialize(mTelephonyManager);

        Log.i("LocalService", "Service stopped");
        mbIsRunning = false;
        mBooleanOptions.clear();
        mStringOptions.clear();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
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

        //bitmap conversion for the large icon
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.drive_assistant_white);

        Notification notification = new Notification.Builder(getBaseContext())
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Handling calls")
                .setSmallIcon(R.drawable.drive_assistant_notification)
                .setLargeIcon(bm)
                .setContentIntent(contentIntent)
                .build();

        notification.flags |= Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR; //TODO: FLAG_FOREGROUND_SERVICE

        // Send the notification.
        mNM.notify(NOTIFICATION, notification);
    }

    public boolean getBoolean(String key, boolean defaultValue){
        Boolean val = mBooleanOptions.get(key);
        if(val != null){
            return val.booleanValue();
        }
        return defaultValue;
    }

    public String getString(String key, String defaultValue){
        String val = mStringOptions.get(key);
        if(val != null){
            return val;
        }
        return defaultValue;
    }

}
