package com.superflace.drivingassistant;

import com.superflace.core.phone.CallListener;
import com.superflace.core.sound.SoundControl;
import com.superflace.core.tts.TTSControl;

import android.media.AudioManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;

public class MainActivity extends Activity {

	TTSControl m_tts = null;
	SoundControl m_sndCtrl = null;
	CallListener m_callListener = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
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
				if(m_sndCtrl == null){
					AudioManager am = (AudioManager)getSystemService(AUDIO_SERVICE);
					m_sndCtrl = new SoundControl(am);
				}
				if(checked){
					
					m_sndCtrl.setRingVolumeMax();
				}
				else{
					m_sndCtrl.revertRingVolume();
				}
				break;
			}
			case R.id.checkbox_speak_caller_id:{
				if(checked){
					if(m_tts == null){
						m_tts = new TTSControl();
					}
					if(m_callListener == null) {
						m_callListener = new CallListener(this, m_tts);
						TelephonyManager mgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
						mgr.listen(m_callListener, PhoneStateListener.LISTEN_CALL_STATE);
					}
					m_tts.checkTTSAvailability(this);
				}
				else {
					//unregister phone state listener
					TelephonyManager mgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
					mgr.listen(m_callListener, PhoneStateListener.LISTEN_NONE);
					m_callListener = null; //might crash if the system still notifies the call listener
				}
			}
		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		m_tts.handleActivityResult(this, requestCode, resultCode, data);
	}
	
	public void activateAssistant(View view){
		
	}
	
	public void cancelAction(View view){
	}

}
