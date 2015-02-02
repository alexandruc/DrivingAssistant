package com.alexandruc.drivingassistant;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.alexandruc.drivingassistant.bl.ToggleButtonListAdapter;
import com.alexandruc.drivingassistant.core.service.LocalServiceClient;
import com.alexandruc.drivingassistant.core.tts.TTSController;


public class MainActivity extends ActionBarActivity {

    private LocalServiceClient mClient = new LocalServiceClient(this);
    private TTSController mTTSController = new TTSController(this);

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
                } else if(item.equals(getString(R.string.caller_id)))
                {
                    if(mTTSController.isInitialized()){
                        mTTSController.speak(getString(R.string.caller_id));
                    }else {
                        mTTSController.checkTTSDataAvailabilityRequest();
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTTSController.shutdown();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(!mTTSController.checkTTSDataAvailabilityRequestResult(requestCode, resultCode))
        {
            mTTSController.requestTTSDataInstallation();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_onoff);
        Switch sw = (Switch) searchItem.getActionView().findViewById(R.id.switchForActionBar);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    Toast.makeText(getBaseContext(), "ON", Toast.LENGTH_LONG).show(); //TODO remove me
                    mClient.doBindService();
                }
                else
                {
                    Toast.makeText(getBaseContext(), "OFF", Toast.LENGTH_LONG).show(); //TODO remove me
                    mClient.doUnbindService();
                }
            }
        });

        return true;
    }
}
