package com.superflace.core.tts;

import java.util.HashMap;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;

public class TTSControl  implements OnInitListener, OnUtteranceCompletedListener {
	private static final String TTS_LOG_ID = "DrivingAssistant.TTSControl";
	private int TTS_DATA_CHECK_CODE = 0;
	private static final String UTTERANCE_ID = "1234";
	private TextToSpeech m_TTSEngine = null;
	private static String m_CallerText = null;
	
	public TTSControl() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Checks if TTS data is available on the device 
	 */
	public void initializeTTS(Activity activity ){
		if(activity != null &&
				m_TTSEngine == null){
			Intent checkTTSIntent = new Intent();
			checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
			activity.startActivityForResult(checkTTSIntent, TTS_DATA_CHECK_CODE);
		}
	}
	
	@SuppressWarnings("deprecation")
	public void handleActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
		if(requestCode == TTS_DATA_CHECK_CODE){
			if(resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS){
				m_TTSEngine = new TextToSpeech(activity, this);
				//TODO: how to create a separate listener for the new api
				m_TTSEngine.setOnUtteranceCompletedListener(this);
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
		Log.i(TTS_LOG_ID, "speak called for '" + str + "'");
		if(m_TTSEngine != null && 
				str != null && 
				str.length() > 0
				){
			HashMap<String, String> map = new HashMap<String, String>();
			//attach a custom utterance id 
			map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, UTTERANCE_ID);
			m_CallerText = str;
			
			m_TTSEngine.speak(str, TextToSpeech.QUEUE_FLUSH, map);
		}
		else{
			//TODO: return error/throw exception
			Log.e(TTS_LOG_ID, "Error in speak() - invalid parameters");
		}
	}
	
	public void stop(){
		Log.i(TTS_LOG_ID, "stop called");
		if(m_TTSEngine != null){
			m_TTSEngine.stop();
		}
		m_CallerText = null;
	}

	@Override
	public void onInit(int arg0) {
		//TODO: check for language availability
		if(arg0 == TextToSpeech.SUCCESS){
			Log.i(TTS_LOG_ID, "TTS initialized correctly");
			m_TTSEngine.setLanguage(Locale.US);
		}
	}

//	@Override
//	public void onDone(String utteranceId) {
//		// TODO: re-start utterance, if call is still in progress
//		if(utteranceId.compareTo(UTTERANCE_ID) == 0){
//			speak(m_CallerText);
//		}
//	}
//
//	@Override
//	public void onError(String utteranceId) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void onStart(String utteranceId) {
//		// TODO Auto-generated method stub
//		
//	}

	@Override
	public void onUtteranceCompleted(String utteranceId) {
		Log.i(TTS_LOG_ID, "onUtteranceCompleted called, utteranceId: " + utteranceId);
		if(utteranceId.compareTo(UTTERANCE_ID) == 0){
			speak(m_CallerText);
		}
	}
	
	public void uninitializeTTS(){
		if(m_TTSEngine != null){
			m_TTSEngine.shutdown();
		}
	}

}
