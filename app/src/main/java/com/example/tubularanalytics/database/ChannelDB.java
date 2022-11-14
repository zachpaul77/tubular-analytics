package com.example.tubularanalytics.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import org.json.JSONObject;

import java.util.ArrayList;

public class ChannelDB extends SQLiteOpenHelper {
    private static final String CHANNEL_TABLE = "channel_table";
    private static final String DESCRIPTION = "description";

    public ChannelDB(@Nullable Context context) {
        super(context, CHANNEL_TABLE, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + CHANNEL_TABLE + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DESCRIPTION + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + CHANNEL_TABLE);
        onCreate(db);
    }

    public void insertChannel(JSONObject channel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DESCRIPTION, channel.toString());
        db.insert(CHANNEL_TABLE, null, cv);
    }

    public ArrayList<String> getAllChannels() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor courseData = db.rawQuery("SELECT * FROM " + CHANNEL_TABLE, null);

        ArrayList<String> channelList = new ArrayList<>();
        while (courseData.moveToNext()) {
            channelList.add(courseData.getString(1));
        }
        return channelList;
    }
}
