package com.superflace.core.phone;

import com.superflace.core.tts.TTSControl;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallListener extends PhoneStateListener {
	
	private static final String CALL_LISTENER = "DrivingAssistant.CallListener";

	Context m_context = null;
	TTSControl m_tts = null;
	
	@SuppressWarnings("unused")
	private CallListener() {
		// TODO Auto-generated constructor stub
	}
	
	public CallListener(Context context, TTSControl tts){
		m_tts = tts;
		m_context = context;
	}
	
	@Override
	public void onCallStateChanged(int state, String incomingNumber) {
		switch(state){
		case TelephonyManager.CALL_STATE_RINGING: {
			Log.i(CALL_LISTENER, "incomingNr: " + incomingNumber);
			//TODO: check if another call is in progress
			if(m_tts != null){
				
				//TODO: put this in a thread to speak until the ringing stops
				String callerName = PhoneUtils.getDisplayNameForNumber(m_context, incomingNumber);
				if(callerName != null){
					m_tts.speak(callerName + " is calling");
				}
				else{
					m_tts.speak(incomingNumber + " is calling");
				}
			}

			break;
		}
		case TelephonyManager.CALL_STATE_IDLE: {
			//TODO: stop tts from speaking
			break;
		}
		default: {
			break;
		}
		}

		super.onCallStateChanged(state, incomingNumber);
	}
}
