package com.example.tubularanalytics.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.tubularanalytics.api.YoutubeAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChannelDB extends SQLiteOpenHelper {
    private static final String CHANNEL_STATS_TABLE = "channel_stats";
    private static final String CHANNEL_INFO_TABLE = "channel_info";

    public ChannelDB(@Nullable Context context) {
        super(context, CHANNEL_INFO_TABLE, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable1 = "CREATE TABLE " + CHANNEL_INFO_TABLE + " (_id TEXT PRIMARY KEY, " +
                "channelName TEXT, thumbnail TEXT)";

        String createTable2 = "CREATE TABLE " + CHANNEL_STATS_TABLE + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "_id TEXT, numViews INTEGER, numSubscribers INTEGER, numVideos INTEGER, snapshotDate LONG)";

        db.execSQL(createTable1);
        db.execSQL(createTable2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + CHANNEL_STATS_TABLE);
        onCreate(db);
    }

    public void saveStatsFromURL(String channelURL) {
        String channelId = YoutubeAPI.getChannelId(channelURL);
        saveStatsFromID(channelId);
    }
    public void saveStatsFromID(String channelId) {
        JSONObject channelStats = YoutubeAPI.getChannelStats(channelId);
        saveStats(channelId, channelStats);
    }

    public void deleteChannel(String _id) {
        this.getWritableDatabase().execSQL("DELETE FROM " + CHANNEL_INFO_TABLE + " WHERE _id='" + _id + "'");
        this.getWritableDatabase().execSQL("DELETE FROM " + CHANNEL_STATS_TABLE + " WHERE _id='" + _id + "'");
    }
    public void deleteChannelSnapshot(String ID) {
        this.getWritableDatabase().execSQL("DELETE FROM " + CHANNEL_STATS_TABLE + " WHERE ID='" + ID + "'");
    }

    private void saveStats(String id, JSONObject stats) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        try {
            cv.put("_id", id);
            cv.put("channelName", stats.getString("channelName"));
            cv.put("thumbnail", stats.getString("thumbnail"));
            db.insert(CHANNEL_INFO_TABLE, null, cv);

            cv = new ContentValues();
            cv.put("_id", id);
            cv.put("numViews", stats.getInt("numViews"));
            cv.put("numSubscribers", stats.getInt("numSubscribers"));
            cv.put("numVideos", stats.getInt("numVideos"));
            cv.put("snapshotDate", stats.getLong("snapshotDate"));
            db.insert(CHANNEL_STATS_TABLE, null, cv);
        } catch (Exception e) {
            Log.d("tubular-analytics", e.toString());
        }

        db.close();
    }

    public ArrayList<JSONObject> getAllChannelStats() {
        Cursor c = this.getWritableDatabase().rawQuery("SELECT * FROM " + CHANNEL_INFO_TABLE, null);

        ArrayList<JSONObject> videoList = new ArrayList<>();
        while (c.moveToNext()) {
            try {
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("_id", c.getString(0));
                jsonObj.put("channelName", c.getString(1));
                jsonObj.put("thumbnail", c.getString(2));

                JSONArray snapshotArray = new JSONArray();
                Cursor c1 = this.getWritableDatabase().rawQuery(
                        "SELECT * FROM " + CHANNEL_STATS_TABLE + " WHERE _id = '" + c.getString(0) + "'", null);

                while (c1.moveToNext()) {
                    JSONObject snapshotObj = new JSONObject();
                    snapshotObj.put("ID", c1.getInt(0));
                    snapshotObj.put("numViews", c1.getInt(2));
                    snapshotObj.put("numSubscribers", c1.getInt(3));
                    snapshotObj.put("numVideos", c1.getInt(4));
                    snapshotObj.put("snapshotDate", c1.getLong(5));
                    snapshotArray.put(snapshotObj);
                }
                jsonObj.put("snapshots", snapshotArray);
                videoList.add(jsonObj);
            } catch (JSONException e) {}
        }
        return videoList;
    }

    public ArrayList<JSONObject> getChannelSnapshots(String _id) {
        try {
            ArrayList<JSONObject> snapshotArray = new ArrayList<>();
            Cursor c1 = this.getWritableDatabase().rawQuery(
                    "SELECT * FROM " + CHANNEL_STATS_TABLE + " WHERE _id = '" + _id + "'", null);

            while (c1.moveToNext()) {
                JSONObject snapshotObj = new JSONObject();
                snapshotObj.put("ID", c1.getInt(0));
                snapshotObj.put("numViews", c1.getInt(2));
                snapshotObj.put("numSubscribers", c1.getInt(3));
                snapshotObj.put("numVideos", c1.getInt(4));
                snapshotObj.put("snapshotDate", c1.getLong(5));
                snapshotArray.add(snapshotObj);
            }
            return snapshotArray;
        } catch (JSONException e) {}

        return null;
    }

}
