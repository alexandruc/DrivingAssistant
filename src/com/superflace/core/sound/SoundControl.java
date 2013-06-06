package com.superflace.core.sound;

import android.media.AudioManager;

public class SoundControl {

	private AudioManager m_am = null;
	private int m_iOldVolumeValue = 0;
	
	@SuppressWarnings("unused")
	private SoundControl() {
		// TODO Auto-generated constructor stub
	}
	
	public SoundControl(AudioManager am){
		m_am = am;
		m_iOldVolumeValue = m_am.getStreamVolume(AudioManager.STREAM_RING);
	}
	
	/**
	 * Sets ring volume to max
	 */
	public void setRingVolumeMax(){
		//TODO: throw some exceptions if am is null
		if(m_am != null){
			m_iOldVolumeValue = m_am.getStreamVolume(AudioManager.STREAM_RING);
			int maxCallValue = m_am.getStreamMaxVolume(AudioManager.STREAM_RING);
			m_am.setStreamVolume(AudioManager.STREAM_RING, maxCallValue, 0);
		}
	}
	
	/**
	 * Reverts ring volume to the old value, if it's the case
	 */
	public void revertRingVolume(){
		//TODO: throw some exceptions
		if(m_am != null && m_iOldVolumeValue != 0){
			m_am.setStreamVolume(AudioManager.STREAM_RING, m_iOldVolumeValue, 0);
		}
	}
	
}
