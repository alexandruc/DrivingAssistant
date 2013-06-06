package com.superflace.core.tts;

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;

public class TTSControl implements OnInitListener {
	private static final String TTS_LOG_ID = "DrivingAssistant.TTSControl";
	private int TTS_DATA_CHECK_CODE = 0;
	private TextToSpeech m_TTSEngine = null;
	
	public TTSControl() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Checks if TTS data is available on the device 
	 */
	public void checkTTSAvailability(Activity activity ){
		if(activity != null){
			Intent checkTTSIntent = new Intent();
			checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
			activity.startActivityForResult(checkTTSIntent, TTS_DATA_CHECK_CODE);
		}
	}
	
	public void handleActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
		if(requestCode == TTS_DATA_CHECK_CODE){
			if(resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS){
				m_TTSEngine = new TextToSpeech(activity, this);
				Log.i(TTS_LOG_ID, "TTS data is available");
			}
			else{
				//TODO: return failure
				Log.i(TTS_LOG_ID, "TTS data is not available");
				Intent installTTSIntent = new Intent();
				installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
				activity.startActivity(installTTSIntent);
			}
		}
	}
	
	public void speak(String str){
		if(m_TTSEngine != null && 
				str != null && 
				str.length() > 0
				){
			m_TTSEngine.speak(str, TextToSpeech.QUEUE_FLUSH, null);
		}
		else{
			//TODO: return error/throw exception
			Log.e(TTS_LOG_ID, "Error in speak() - invalid parameters");
		}
	}

	@Override
	public void onInit(int arg0) {
		//TODO: check for language availability
		if(arg0 == TextToSpeech.SUCCESS){
			Log.i(TTS_LOG_ID, "TTS initialized correctly");
			m_TTSEngine.setLanguage(Locale.US);
		}
	}

}
