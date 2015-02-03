package com.alexandruc.drivingassistant.core.phone;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.alexandruc.drivingassistant.R;
import com.alexandruc.drivingassistant.Utils.DataUtils;
import com.alexandruc.drivingassistant.core.tts.TTSController;

import java.util.ArrayList;

public class CallListener extends PhoneStateListener {

    private TTSController mTTS;
    private Activity mContext;
    private PhoneUtils mPUtils;

    public CallListener(Activity context, TTSController tts){
        mContext = context;
        mTTS = tts;
        mPUtils = new PhoneUtils(context);
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
                    String callerName = mPUtils.getContactDisplayNameByNumber(incomingNumber);
                    if(!callerName.isEmpty()){
                        mTTS.speak(callerName);
                    }
                    else {
                        mTTS.speak(incomingNumber);
                    }
                }
                break;
            }
            case TelephonyManager.CALL_STATE_IDLE:{
                Log.d("CallListener", "Call state idle");
                mTTS.stop();
                //send busy message
                //TODO: should put this in a separate class, but it's too simple
                SharedPreferences prefs = mContext.getSharedPreferences(DataUtils.sharedPrefsName, Context.MODE_PRIVATE);
                if (prefs.getBoolean(mContext.getString(R.string.busy_message), false)) {
                    SmsManager smsManager = SmsManager.getDefault();

                    String message = prefs.getString(DataUtils.busyMessageKey, mContext.getString(R.string.default_busy_message));

                    ArrayList<String> messages = smsManager.divideMessage(message);
                    try {
                        smsManager.sendMultipartTextMessage(incomingNumber, null, messages, null, null);
                    } catch (IllegalArgumentException e) {
                        Log.d("CallListener", "Exception when sending message");
                    }
                }
                //NOTE: should check if they were sent ok?
                break;
            }
            default:
                break;
        }
    }
}
