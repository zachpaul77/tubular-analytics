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

        //new Thread(() -> { new VideoDB(this).saveVideoStats("https://www.youtube.com/watch?v=kX3nB4PpJko"); }).start();
        //new Thread(() -> { new ChannelDB(this).saveChannelStats("https://www.youtube.com/user/MrBeast6000/featured"); }).start();

        try {
            Log.d("tubular-analytics", new VideoDB(this).getVideoStats("i7PX9gyZwW8").toString());
            Log.d("tubular-analytics", new ChannelDB(this).getAllChannelStats().toString());
        } catch (Exception e) {}
    }
}
