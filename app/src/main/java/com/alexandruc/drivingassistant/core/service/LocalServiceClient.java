package com.alexandruc.drivingassistant.core.service;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
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

            Toast.makeText(context, R.string.service_name + " connected",
                    Toast.LENGTH_SHORT).show();
        }

        public void onServiceDisconnected(ComponentName className) {
            mBoundService = null;
            Toast.makeText(context, R.string.service_name + " disconnected",
                    Toast.LENGTH_SHORT).show();
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
