package com.superflace.drivingassistant;

import java.util.HashMap;


import com.superflace.core.phone.CallListener;
import com.superflace.core.sound.SoundControl;
import com.superflace.core.tts.TTSControl;

import android.media.AudioManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

public class MainActivity extends Activity {

	public static final String SPEAK_CALLER_ID = "speakCallerId";
	public static final String SMS_FOR_BUSY = "sendSMSForBusy";
	public static final String SET_VOLUME_MAX = "setVolumeMax";
	public static final String AUTO_ACTIVATE = "autoActivate";
	private static HashMap<String, Boolean> m_prefMap = null; 
	
	TTSControl m_tts = null;
	SoundControl m_sndCtrl = null;
	CallListener m_callListener = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		m_prefMap = new HashMap<String, Boolean>();
		
		SharedPreferences prefs = getPreferences(MODE_PRIVATE);
		try{
			m_prefMap.put(AUTO_ACTIVATE, prefs.getBoolean(AUTO_ACTIVATE, false) );
			m_prefMap.put(SPEAK_CALLER_ID, prefs.getBoolean(SPEAK_CALLER_ID, false) );
			m_prefMap.put(SMS_FOR_BUSY, prefs.getBoolean(SMS_FOR_BUSY, false) );
			m_prefMap.put(SET_VOLUME_MAX, prefs.getBoolean(SET_VOLUME_MAX, false) );
		}
		catch(ClassCastException ex){
			m_prefMap.clear();
			m_prefMap.put(AUTO_ACTIVATE, false );
			m_prefMap.put(SPEAK_CALLER_ID, false );
			m_prefMap.put(SMS_FOR_BUSY, false );
			m_prefMap.put(SET_VOLUME_MAX, false );
		}
		finally{
			//update checkboxes and options
			CheckBox chk = (CheckBox)findViewById(R.id.checkbox_auto_activate);
			if(chk != null){
				chk.setChecked(m_prefMap.get(AUTO_ACTIVATE).booleanValue());
			}
			chk = (CheckBox)findViewById(R.id.checkbox_send_sms);
			if(chk != null){
				chk.setChecked(m_prefMap.get(SMS_FOR_BUSY).booleanValue());
			}
			chk = (CheckBox)findViewById(R.id.checkbox_set_volume_max);
			if(chk != null){
				chk.setChecked(m_prefMap.get(SET_VOLUME_MAX).booleanValue());
				if(chk.isChecked()){
					initializeAudioSettings();
					m_sndCtrl.setRingVolumeMax();
				}
			}
			chk = (CheckBox)findViewById(R.id.checkbox_speak_caller_id);
			if(chk != null){
				chk.setChecked(m_prefMap.get(SPEAK_CALLER_ID).booleanValue());
				if(chk.isChecked()){
					initializeTTSObjects();
					initializePhoneObjects();
				}
			}
			
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void onCheckboxClicked(View view){
		boolean checked = ((CheckBox)view).isChecked();
		
		switch(view.getId()){
			case R.id.checkbox_set_volume_max:{
				if(checked){
					m_prefMap.put(SET_VOLUME_MAX, true);
				}
				else{
					m_prefMap.put(SET_VOLUME_MAX, false);
				}
				break;
			}
			case R.id.checkbox_speak_caller_id:{
				if(checked){
					m_prefMap.put(SPEAK_CALLER_ID, true);
				}
				else {
					
					m_prefMap.put(SPEAK_CALLER_ID, false);
				}
			}
			case R.id.checkbox_send_sms:{
				if(checked){
					m_prefMap.put(SMS_FOR_BUSY, true);
				}
				else{
					m_prefMap.put(SMS_FOR_BUSY, false);
				}
				break;
			}
			case R.id.checkbox_auto_activate:{
				if(checked){
					m_prefMap.put(AUTO_ACTIVATE, true);
				}
				else{
					m_prefMap.put(AUTO_ACTIVATE, false);
				}
				break;
			}
			default:{break;}
		}
		
		//enable the save button
		Button saveBtn = (Button)findViewById(R.id.button_save);
		if(saveBtn != null){
			saveBtn.setEnabled(true);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		m_tts.handleActivityResult(this, requestCode, resultCode, data);
	}
	
	public void saveChanges(View view){
		SharedPreferences prefs = getPreferences(MODE_PRIVATE);
		SharedPreferences.Editor prefEdit = prefs.edit();
		//save the current selections
		//TODO: these preferences should be encapsulated in another class
		Boolean value = false;
		if( (value = m_prefMap.get(SET_VOLUME_MAX)) != null){
			prefEdit.putBoolean(SET_VOLUME_MAX, value.booleanValue());
			initializeAudioSettings();
			if(value.booleanValue()){
				m_sndCtrl.setRingVolumeMax();
			}
			else{
				m_sndCtrl.revertRingVolume();
			}
		}
		if( (value = m_prefMap.get(AUTO_ACTIVATE)) != null){
			prefEdit.putBoolean(AUTO_ACTIVATE, value.booleanValue());
		}
		if( (value = m_prefMap.get(SPEAK_CALLER_ID)) != null){
			prefEdit.putBoolean(SPEAK_CALLER_ID, value.booleanValue());
			if(value.booleanValue()){
				initializeTTSObjects();
				initializePhoneObjects();
			}
			else{
				uninitializeTTSObjects();
				uninitializePhoneObjects();
			}
		}
		if( (value = m_prefMap.get(SMS_FOR_BUSY)) != null){
			prefEdit.putBoolean(SMS_FOR_BUSY, value.booleanValue());
		}
		
		prefEdit.apply();
		
		Button saveBtn = (Button)findViewById(R.id.button_save);
		if(saveBtn != null){
			saveBtn.setEnabled(false);
		}
	}
	
	public void cancelAction(View view){
		this.finish();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		uninitializeTTSObjects();
		uninitializePhoneObjects();
	}
	
	private void initializeAudioSettings() {
		if(m_sndCtrl == null){
			AudioManager am = (AudioManager)getSystemService(AUDIO_SERVICE);
			m_sndCtrl = new SoundControl(am);
		}
	}
	
	private void initializeTTSObjects(){
		if(m_tts == null){
			m_tts = new TTSControl();
		}
		
		m_tts.initializeTTS(this);
	}
	
	private void uninitializeTTSObjects(){
		if(m_tts != null){
			m_tts.uninitializeTTS();
			m_tts = null;
		}
	}
	
	private void initializePhoneObjects(){
		if(m_callListener == null) {
			m_callListener = new CallListener(this, m_tts);
			TelephonyManager mgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			mgr.listen(m_callListener, PhoneStateListener.LISTEN_CALL_STATE);
		}
	}
	
	private void uninitializePhoneObjects(){
		//unregister phone state listener
		TelephonyManager mgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		mgr.listen(m_callListener, PhoneStateListener.LISTEN_NONE);
		m_callListener = null; //might crash if the system still notifies the call listener
	}
}
