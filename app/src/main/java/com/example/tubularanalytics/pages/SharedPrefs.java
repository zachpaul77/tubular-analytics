package com.example.tubularanalytics.pages;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPrefs {
    private static SharedPrefs instance;
    private Context context;
    private SharedPreferences preferences;

    public static SharedPrefs getInstance(){
        if (instance == null) instance = new SharedPrefs();
        return instance;
    }

    public void Initialize(Context context) {
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean get(String key) {
        return preferences.getBoolean(key, false);
    }

    public void set(String key, boolean value){
        SharedPreferences.Editor e = preferences.edit();
        e.putBoolean(key, value);
        e.commit();
    }
}
