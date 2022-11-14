package com.example.tubularanalytics.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import org.json.JSONObject;

import java.util.ArrayList;

public class VideoDB extends SQLiteOpenHelper {
    private static final String VIDEO_TABLE = "video_table";
    private static final String DESCRIPTION = "description";

    public VideoDB(@Nullable Context context) {
        super(context, VIDEO_TABLE, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + VIDEO_TABLE + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DESCRIPTION + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + VIDEO_TABLE);
        onCreate(db);
    }

    public void insertVideo(JSONObject video) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DESCRIPTION, video.toString());
        db.insert(VIDEO_TABLE, null, cv);
    }

    public ArrayList<String> getAllVideos() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor courseData = db.rawQuery("SELECT * FROM " + VIDEO_TABLE, null);

        ArrayList<String> videoList = new ArrayList<>();
        while (courseData.moveToNext()) {
            videoList.add(courseData.getString(1));
        }
        return videoList;
    }
}
