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
                }
                else
                {
                    Toast.makeText(getBaseContext(), "OFF", Toast.LENGTH_LONG).show(); //TODO remove me
                }
            }
        });

        return true;
    }
}
