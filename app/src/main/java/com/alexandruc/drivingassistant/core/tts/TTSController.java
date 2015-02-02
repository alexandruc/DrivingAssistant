package com.alexandruc.drivingassistant.core.tts;

import android.app.Activity;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import com.alexandruc.drivingassistant.R;
import com.alexandruc.drivingassistant.Utils.DataUtils;

import java.util.Locale;

public class TTSController implements TextToSpeech.OnInitListener {
    private final Activity mContext;
    private TextToSpeech mTTS = null;


    public TTSController(Activity context)
    {
        this.mContext = context;
    }

    public void checkTTSDataAvailabilityRequest(){
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        mContext.startActivityForResult(checkTTSIntent, DataUtils.TTS_DATA_CHECK_ID);
    }

    /**
     * Will be called to handle request for tts data
     * @param requestCode upstream onActivityResult parameter
     * @param resultCode upstream onActivityResult parameter
     * @return true for TTS data available and TTS initialization OK, false otherwise
     */
    public boolean checkTTSDataAvailabilityRequestResult(int requestCode, int resultCode){
        boolean retVal = false;
        if (requestCode == DataUtils.TTS_DATA_CHECK_ID) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                mTTS = new TextToSpeech(mContext, this);
                retVal = true;
            }
        }
        return retVal;
    }

    public void requestTTSDataInstallation(){
        Intent installTTSIntent = new Intent();
        installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
        mContext.startActivity(installTTSIntent);
    }

    public boolean isInitialized(){
        return mTTS!=null;
    }

    public void speak(String sentence){
        //mTTS.speak(sentence, TextToSpeech.QUEUE_FLUSH, null, "dummy_utt_id"); //TODO: add this for api 21
        if (mTTS!=null) {
            mTTS.speak(sentence, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    public void shutdown(){
        if (mTTS!= null){
            mTTS.shutdown();
        }
    }

    @Override
    public void onInit(int status) {
        if(status == TextToSpeech.SUCCESS)
        {
            mTTS.setLanguage(Locale.US);
        }else if (status == TextToSpeech.ERROR) {
            Toast.makeText(mContext, "TTS init failed...", Toast.LENGTH_LONG).show();
        }
    }
}
