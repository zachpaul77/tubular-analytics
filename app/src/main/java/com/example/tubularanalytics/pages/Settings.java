package com.example.tubularanalytics.pages;

import androidx.appcompat.app.AppCompatActivity;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Switch;
import android.widget.Toast;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        Switch likes = findViewById(R.id.Likes);
        Switch comments = findViewById(R.id.Comments);
        Switch views = findViewById(R.id.Views);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor e = settings.edit();
        likes.setOnClickListener(view -> {
            e.putBoolean("likes", likes.isChecked());
            e.commit();
            String text = "Likes = " +likes.isChecked();
            Toast toast =Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
            toast.show();
        });
        comments.setOnClickListener(v -> {
            e.putBoolean("comments", comments.isChecked());
            e.commit();
            String text = "Comments = " +comments.isChecked();
            Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
            toast.show();
        });
        views.setOnClickListener(v -> {
            e.putBoolean("subscribers", views.isChecked());
            e.commit();
            String text = "Views = " +views.isChecked();
            Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
            toast.show();
        });


    }
}
