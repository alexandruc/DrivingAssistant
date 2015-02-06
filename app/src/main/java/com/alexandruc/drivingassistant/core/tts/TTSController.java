package com.alexandruc.drivingassistant.core.tts;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.widget.Toast;

import com.alexandruc.drivingassistant.R;
import com.alexandruc.drivingassistant.Utils.DataUtils;

import java.util.HashMap;
import java.util.Locale;

public class TTSController extends UtteranceProgressListener implements TextToSpeech.OnInitListener {
    private final Service mContext;
    private TextToSpeech mTTS = null;
    private String mCurrentSpeakStr = null;

    private enum TTSStates {
        STOPPED,
        SPEAKING
    }

    private TTSStates mTTSState = TTSStates.STOPPED;


    public TTSController(Service context)
    {
        this.mContext = context;
        //at this point tts should be enabled
        mTTS = new TextToSpeech(mContext, this);
    }

    public boolean isInitialized(){
        return mTTS!=null;
    }

    public void speak(String sentence){
        //mTTS.speak(sentence, TextToSpeech.QUEUE_FLUSH, null, "dummy_utt_id"); //TODO: add this for api 21
        if (isInitialized()) {
            mTTS.setOnUtteranceProgressListener(this);
            mCurrentSpeakStr = sentence;
            HashMap<String, String> paramMap = new HashMap<>();
            paramMap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, DataUtils.TTS_PARAM_UTTERANCE_ID);
            mTTS.speak(mCurrentSpeakStr + " " + mContext.getString(R.string.caller_id_suffix) + "...", TextToSpeech.QUEUE_FLUSH, paramMap);
            mTTSState = TTSStates.SPEAKING;
        }
    }

    public void stop(){
        mTTSState = TTSStates.STOPPED;
        if(mTTS != null){
            mTTS.stop();
        }
    }

    public void shutdown(){
        mTTSState = TTSStates.STOPPED;
        if (mTTS!= null){
            mTTS.shutdown();
        }
    }

    @Override
    public void onInit(int status) {
        if(status == TextToSpeech.SUCCESS)
        {
            if(TextToSpeech.LANG_COUNTRY_AVAILABLE == mTTS.isLanguageAvailable(Locale.US)){
                mTTS.setLanguage(Locale.US);
            }
            else {
                Toast.makeText(mContext, "US Locale is not available...", Toast.LENGTH_LONG).show();
            }

        }else if (status == TextToSpeech.ERROR) {
            Toast.makeText(mContext, "TTS init failed...", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onStart(String utteranceId) {
        Log.d("TTSController", "Utterance started");
    }

    @Override
    public void onDone(String utteranceId) {
        Log.d("TTSController", "Utterance done");
        //replay last caller id
        if (utteranceId.equals(DataUtils.TTS_PARAM_UTTERANCE_ID) &&
                mTTSState != TTSStates.STOPPED) {
            speak(mCurrentSpeakStr);
        }
    }

    @Override
    public void onError(String utteranceId) {
        Log.d("TTSController", "Utterance error");
    }
}
