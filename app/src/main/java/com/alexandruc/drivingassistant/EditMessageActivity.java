package com.alexandruc.drivingassistant;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.alexandruc.drivingassistant.Utils.DataUtils;


public class EditMessageActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_message);

        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        SharedPreferences prefs = getSharedPreferences(DataUtils.sharedPrefsName, Context.MODE_PRIVATE);
        String message = prefs.getString(DataUtils.busyMessageKey, getString(R.string.default_busy_message));
        EditText et = (EditText)findViewById(R.id.edit_text);
        et.setText(message);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_message, menu);
        return true;
    }
}
