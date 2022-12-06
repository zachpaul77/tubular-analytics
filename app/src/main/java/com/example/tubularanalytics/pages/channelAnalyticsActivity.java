package com.example.tubularanalytics.pages;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import android.os.Bundle;

import com.example.tubularanalytics.R;

public class channelAnalyticsActivity extends MainActivity {
    private Button addChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //wasn't working bc it was set to main activity
        setContentView(R.layout.activity_channel_analytics);

        addChannel = (Button) findViewById(R.id.addChannel);
        addChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddChannelActivity();
            }
        });
    }

    public void openAddChannelActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}