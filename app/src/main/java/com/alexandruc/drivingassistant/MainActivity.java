package com.alexandruc.drivingassistant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;

import com.alexandruc.drivingassistant.Utils.DataUtils;
import com.alexandruc.drivingassistant.bl.ToggleButtonListAdapter;
import com.alexandruc.drivingassistant.core.service.LocalService;

public class MainActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ToggleButtonListAdapter adapter = new ToggleButtonListAdapter(this, getResources().getStringArray(R.array.Strings));
        ListView lv = (ListView) findViewById(R.id.list);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);
                if (item.equals(getString(R.string.busy_message))) {
                    Intent intent = new Intent(getBaseContext(), EditMessageActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == DataUtils.TTS_DATA_CHECK_ID) {
            if (resultCode != TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
            else{
                startLocalService();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_onoff);
        Switch sw = (Switch) searchItem.getActionView().findViewById(R.id.switchForActionBar);
        if(LocalService.isServiceRunning()){
            sw.setChecked(true);
        }

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    SharedPreferences prefs = getSharedPreferences(DataUtils.sharedPrefsName, Context.MODE_PRIVATE);
                    if(prefs.getBoolean(getString(R.string.caller_id),false)) {
                        Intent checkTTSIntent = new Intent();
                        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
                        startActivityForResult(checkTTSIntent, DataUtils.TTS_DATA_CHECK_ID);
                    }
                    else {
                        startLocalService();
                    }
                }
                else{
                    Intent intent = new Intent(getBaseContext(), LocalService.class);
                    stopService(intent);
                }
            }
        });

        return true;
    }

    private void startLocalService(){
        SharedPreferences prefs = getSharedPreferences(DataUtils.sharedPrefsName, Context.MODE_PRIVATE);
        Intent intent = new Intent(getBaseContext(), LocalService.class);

        intent.putExtra(getString(R.string.caller_id), prefs.getBoolean(getString(R.string.caller_id), false));
        intent.putExtra(getString(R.string.busy_message), prefs.getBoolean(getString(R.string.busy_message), false));
        intent.putExtra(getString(R.string.whitelist), prefs.getBoolean(getString(R.string.whitelist), false));
        intent.putExtra(getString(R.string.blacklist), prefs.getBoolean(getString(R.string.blacklist), false));
        intent.putExtra(DataUtils.busyMessageKey, prefs.getString(DataUtils.busyMessageKey, getString(R.string.default_busy_message)));
        startService(intent);
    }
}
