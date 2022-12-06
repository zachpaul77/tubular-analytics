package com.example.tubularanalytics.pages;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.widget.Switch;
import com.example.tubularanalytics.R;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        togglePreference("Likes", findViewById(R.id.Likes));
        togglePreference("Comments", findViewById(R.id.Comments));
        togglePreference("Views", findViewById(R.id.Views));
        togglePreference("Subscribers", findViewById(R.id.Subscribers));
        togglePreference("Videos", findViewById(R.id.Videos));
    }

    public void togglePreference(String key, Switch preference) {
        preference.setChecked(SharedPrefs.getInstance().get(key));

        preference.setOnClickListener(view -> {
            SharedPrefs.getInstance().set(key, preference.isChecked());
        });
    }

}
