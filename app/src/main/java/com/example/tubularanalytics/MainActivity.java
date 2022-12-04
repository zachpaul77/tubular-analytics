package com.example.tubularanalytics;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.tubularanalytics.database.ChannelDB;
import com.example.tubularanalytics.database.VideoDB;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* YT API EXAMPLE
        new Thread(() -> { new ChannelDB(this).saveStatsFromURL("https://www.youtube.com/user/MrBeast6000/featured"); }).start();
        new Thread(() -> { new VideoDB(this).saveStatsFromURL("https://www.youtube.com/watch?v=kX3nB4PpJko"); }).start();
        Log.d("tubular-analytics", new VideoDB(this).getVideoSnapshots("i7PX9gyZwW8").toString());
        Log.d("tubular-analytics", new ChannelDB(this).getAllChannelStats().toString());
         */
    }
}
