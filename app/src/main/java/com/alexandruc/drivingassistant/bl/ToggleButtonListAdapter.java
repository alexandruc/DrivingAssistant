package com.alexandruc.drivingassistant.bl;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.alexandruc.drivingassistant.R;
import com.alexandruc.drivingassistant.Utils.DataUtils;
import com.alexandruc.drivingassistant.core.service.LocalService;

public class ToggleButtonListAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;

    public ToggleButtonListAdapter(Context context, String[] values) {
        super(context, R.layout.activity_main, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row_layout, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.settings_text);
        ToggleButton toggleButton = (ToggleButton) rowView.findViewById(R.id.toggle_button);

        toggleButton.setOnClickListener(new View.OnClickListener() {
            private final String[] values = getContext().getResources().getStringArray(R.array.Strings);

            @Override
            public void onClick(View v) {
                ToggleButton tgl = (ToggleButton) v;
                SharedPreferences prefs = getContext().getSharedPreferences(DataUtils.sharedPrefsName, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(values[position], tgl.isChecked());
                editor.apply();

                if(LocalService.isServiceRunning()){
                    Toast.makeText(getContext(), getContext().getString(R.string.toast_option_changed), Toast.LENGTH_LONG).show();
                }
            }
        });

        SharedPreferences prefs = getContext().getSharedPreferences(DataUtils.sharedPrefsName, Context.MODE_PRIVATE);
        toggleButton.setChecked(prefs.getBoolean(values[position], false));

        textView.setText(values[position]);

        return rowView;
    }
}
