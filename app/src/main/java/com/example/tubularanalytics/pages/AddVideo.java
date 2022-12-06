package com.example.tubularanalytics.pages;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tubularanalytics.R;
import com.example.tubularanalytics.database.VideoDB;
import com.example.tubularanalytics.pages.overview_pages.VideoOverview;

public class AddVideo extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_video);
    }

    public void addVideoBtn(View view) {
        new Thread(() -> {
            EditText videoLink = findViewById(R.id.videoLink);
            new VideoDB(this).saveStatsFromURL(videoLink.getText().toString());
            Intent intent = new Intent(getApplicationContext(), VideoOverview.class);
            startActivity(intent);
        }).start();
    }

    public void cancelBtn(View view) {
        Intent intent = new Intent(getApplicationContext(), VideoOverview.class);
        startActivity(intent);
    }

}
