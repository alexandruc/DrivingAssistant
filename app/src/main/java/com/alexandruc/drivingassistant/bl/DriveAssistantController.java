package com.alexandruc.drivingassistant.bl;


import android.app.Activity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.alexandruc.drivingassistant.core.phone.CallListener;
import com.alexandruc.drivingassistant.core.service.LocalService;
import com.alexandruc.drivingassistant.core.tts.TTSController;

public class DriveAssistantController extends Activity {
    private LocalService mContext;
    private TTSController mTTSController;
    private CallListener mCallListener;

    public DriveAssistantController(LocalService context){
        mContext = context;
    }

    public void initialize(TelephonyManager manager){
        mTTSController = new TTSController(mContext);
        mCallListener  = new CallListener(mContext,mTTSController);
        manager.listen(mCallListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    public void uninitialize(TelephonyManager manager){
        manager.listen(mCallListener, PhoneStateListener.LISTEN_NONE);

        mTTSController.shutdown();
        mTTSController = null;
        mCallListener  = null;
    }
}
