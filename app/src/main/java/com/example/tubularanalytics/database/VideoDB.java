package com.example.tubularanalytics.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.tubularanalytics.api.YoutubeAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class VideoDB extends SQLiteOpenHelper {
    private static final String VIDEO_STATS_TABLE = "video_stats";
    private static final String VIDEO_INFO_TABLE = "video_info";

    public VideoDB(@Nullable Context context) {
        super(context, VIDEO_INFO_TABLE, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable1 = "CREATE TABLE " + VIDEO_INFO_TABLE + " (_id TEXT PRIMARY KEY, " +
                "videoName TEXT, channelName TEXT, thumbnail TEXT)";

        String createTable2 = "CREATE TABLE " + VIDEO_STATS_TABLE + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "_id TEXT, numViews INTEGER, numLikes INTEGER, numComments INTEGER, snapshotDate LONG)";

        db.execSQL(createTable1);
        db.execSQL(createTable2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + VIDEO_STATS_TABLE);
        onCreate(db);
    }

    public void saveStatsFromURL(String videoURL) {
        String videoId = YoutubeAPI.getVideoId(videoURL);
        saveStatsFromID(videoId);
    }
    public void saveStatsFromID(String videoId) {
        JSONObject videoStats = YoutubeAPI.getVideoStats(videoId);
        saveStats(videoId, videoStats);
    }

    public void deleteVideo(String _id) {
        this.getWritableDatabase().execSQL("DELETE FROM " + VIDEO_INFO_TABLE + " WHERE _id='" + _id + "'");
        this.getWritableDatabase().execSQL("DELETE FROM " + VIDEO_STATS_TABLE + " WHERE _id='" + _id + "'");
    }
    public void deleteVideoSnapshot(String ID) {
        this.getWritableDatabase().execSQL("DELETE FROM " + VIDEO_STATS_TABLE + " WHERE ID='" + ID + "'");
    }

    private void saveStats(String id, JSONObject stats) {
        this.getReadableDatabase();
        this.getWritableDatabase();
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        try {
            cv.put("_id", id);
            cv.put("videoName", stats.getString("videoName"));
            cv.put("channelName", stats.getString("channelName"));
            cv.put("thumbnail", stats.getString("thumbnail"));
            db.insert(VIDEO_INFO_TABLE, null, cv);

            cv = new ContentValues();
            cv.put("_id", id);
            cv.put("numViews", stats.getInt("numViews"));
            cv.put("numLikes", stats.getInt("numLikes"));
            cv.put("numComments", stats.getInt("numComments"));
            cv.put("snapshotDate", stats.getLong("snapshotDate"));
            db.insert(VIDEO_STATS_TABLE, null, cv);
        } catch (Exception e) {}

        db.close();
    }

    public ArrayList<JSONObject> getAllVideoStats() {
        Cursor c = this.getWritableDatabase().rawQuery("SELECT * FROM " + VIDEO_INFO_TABLE, null);

        ArrayList<JSONObject> videoList = new ArrayList<>();
        while (c.moveToNext()) {
            try {
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("_id", c.getString(0));
                jsonObj.put("videoName", c.getString(1));
                jsonObj.put("channelName", c.getString(2));
                jsonObj.put("thumbnail", c.getString(3));

                JSONArray snapshotArray = new JSONArray();
                Cursor c1 = this.getWritableDatabase().rawQuery(
                        "SELECT * FROM " + VIDEO_STATS_TABLE + " WHERE _id = '" + c.getString(0) + "'", null);

                while (c1.moveToNext()) {
                    JSONObject snapshotObj = new JSONObject();
                    snapshotObj.put("ID", c1.getInt(0));
                    snapshotObj.put("numViews", c1.getInt(2));
                    snapshotObj.put("numLikes", c1.getInt(3));
                    snapshotObj.put("numComments", c1.getInt(4));
                    snapshotObj.put("snapshotDate", c1.getLong(5));
                    snapshotArray.put(snapshotObj);
                }
                jsonObj.put("snapshots", snapshotArray);
                videoList.add(jsonObj);

            } catch (JSONException e) {}
        }

        return videoList;
    }

    public ArrayList<JSONObject> getVideoSnapshots(String _id) {
        try {
            ArrayList<JSONObject> snapshotArray = new ArrayList<>();
            Cursor c1 = this.getWritableDatabase().rawQuery(
                    "SELECT * FROM " + VIDEO_STATS_TABLE + " WHERE _id = '" + _id + "'", null);

            while (c1.moveToNext()) {
                JSONObject snapshotObj = new JSONObject();
                snapshotObj.put("ID", c1.getInt(0));
                snapshotObj.put("numViews", c1.getInt(2));
                snapshotObj.put("numLikes", c1.getInt(3));
                snapshotObj.put("numComments", c1.getInt(4));
                snapshotObj.put("snapshotDate", c1.getLong(5));
                snapshotArray.add(snapshotObj);
            }
            return snapshotArray;
        } catch (JSONException e) {}

        return null;
    }

}
