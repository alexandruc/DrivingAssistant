package com.alexandruc.drivingassistant.core.service;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.alexandruc.drivingassistant.R;

public class LocalServiceClient extends Activity {
    private LocalService mBoundService;
    private Context context = null;
    private boolean mIsBound = false;

    public LocalServiceClient(Context context)
    {
        this.context = context;
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {

            mBoundService = ((LocalService.LocalBinder)service).getService();

            Log.i("LocalServiceClient", "Client connected");
        }

        public void onServiceDisconnected(ComponentName className) {
            mBoundService = null;
            Log.i("LocalServiceClient", "Client disconnected");
        }
    };

    public void doBindService() {
        context.bindService(new Intent(context, LocalService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    public void doUnbindService() {
        if (mIsBound) {
            context.unbindService(mConnection);
            mIsBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }
}
