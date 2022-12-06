package com.example.tubularanalytics.pages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import android.os.Bundle;

import com.example.tubularanalytics.R;
import com.example.tubularanalytics.pages.overview_pages.ChannelOverview;
import com.example.tubularanalytics.pages.overview_pages.VideoOverview;

public class MainActivity extends AppCompatActivity {
    private Button vidAnalyticsButton;
    private Button channAnalyticsButton;
    private Button settingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup global shared preferences
        SharedPrefs.getInstance().Initialize(getApplicationContext());

        vidAnalyticsButton = (Button) findViewById(R.id.vidButton);
        vidAnalyticsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), VideoOverview.class);
                startActivity(intent);
            }
        });

        channAnalyticsButton = (Button) findViewById(R.id.channelButton);
        channAnalyticsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ChannelOverview.class);
                startActivity(intent);
            }
        });

        settingsButton = (Button) findViewById(R.id.settingsBtn);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Settings.class);
                startActivity(intent);
            }
        });
    }

}

/* YT API EXAMPLE
        new Thread(() -> { new ChannelDB(this).saveStatsFromURL("https://www.youtube.com/user/MrBeast6000/featured"); }).start();
        new Thread(() -> { new VideoDB(this).saveStatsFromURL("https://www.youtube.com/watch?v=kX3nB4PpJko"); }).start();
        Log.d("tubular-analytics", new VideoDB(this).getVideoSnapshots("i7PX9gyZwW8").toString());
        Log.d("tubular-analytics", new ChannelDB(this).getAllChannelStats().toString());
         */