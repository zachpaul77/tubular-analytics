package com.example.tubularanalytics.pages;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import com.example.tubularanalytics.R;
import com.example.tubularanalytics.database.ChannelDB;
import com.example.tubularanalytics.pages.overview_pages.ChannelOverview;


public class AddChannel extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_channel);
    }

    public void addChannelBtn(View view) {
        new Thread(() -> {
            EditText channelLink = findViewById(R.id.channelLink);
            new ChannelDB(this).saveStatsFromURL(channelLink.getText().toString());
            Intent intent = new Intent(getApplicationContext(), ChannelOverview.class);
            startActivity(intent);
        }).start();
    }

    public void cancelBtn(View view) {
        Intent intent = new Intent(getApplicationContext(), ChannelOverview.class);
        startActivity(intent);
    }

}
