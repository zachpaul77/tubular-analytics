package com.example.tubularanalytics.pages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import android.os.Bundle;

import com.example.tubularanalytics.R;

public class MainActivity extends AppCompatActivity {
    private Button vidAnalyticsButton;
    private Button channAnalyticsButton;
    private Button settingsButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vidAnalyticsButton = (Button) findViewById(R.id.vidButton);
        vidAnalyticsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openVidAnalyticsActivity();
            }
        });

        channAnalyticsButton = (Button) findViewById(R.id.channelButton);
        channAnalyticsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openChannAnalyticsActivity();
            }
        });

        settingsButton = (Button) findViewById(R.id.settings);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSettingsActivity();
            }
        });
    }

    public void openVidAnalyticsActivity(){
        Intent intent = new Intent(this, vidAnalyticsActivity.class);
        startActivity(intent);
    }

    public void openChannAnalyticsActivity(){
        Intent intent = new Intent(this, channelAnalyticsActivity.class);
        startActivity(intent);
    }

    public void openSettingsActivity(){
        Intent intent = new Intent(this, settingsActivity.class);
        startActivity(intent);
    }
}


/* YT API EXAMPLE
        new Thread(() -> { new ChannelDB(this).saveStatsFromURL("https://www.youtube.com/user/MrBeast6000/featured"); }).start();
        new Thread(() -> { new VideoDB(this).saveStatsFromURL("https://www.youtube.com/watch?v=kX3nB4PpJko"); }).start();
        Log.d("tubular-analytics", new VideoDB(this).getVideoSnapshots("i7PX9gyZwW8").toString());
        Log.d("tubular-analytics", new ChannelDB(this).getAllChannelStats().toString());
         */