package com.alexandruc.drivingassistant.core.phone;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.alexandruc.drivingassistant.R;
import com.alexandruc.drivingassistant.Utils.DataUtils;
import com.alexandruc.drivingassistant.core.tts.TTSController;

public class CallListener extends PhoneStateListener {

    private TTSController mTTS;
    private Activity mContext;

    public CallListener(Activity context, TTSController tts){
        mContext = context;
        mTTS = tts;
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);

        switch (state){
            case TelephonyManager.CALL_STATE_OFFHOOK:{
                Log.d("CallListener", "Call state offhook");
                mTTS.stop();
                break;
            }
            case TelephonyManager.CALL_STATE_RINGING:{
                Log.d("CallListener", "Call state ringing");
                //TODO: start fetching the caller id to speak
                SharedPreferences prefs = mContext.getSharedPreferences(DataUtils.sharedPrefsName, Context.MODE_PRIVATE);
                if(prefs.getBoolean(mContext.getString(R.string.caller_id), false)) {
                    mTTS.speak(incomingNumber);
                }
                break;
            }
            case TelephonyManager.CALL_STATE_IDLE:{
                Log.d("CallListener", "Call state idle");
                mTTS.stop();
                break;
            }
            default:
                break;
        }
    }
}
