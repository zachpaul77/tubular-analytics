package com.example.tubularanalytics.api;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.ImageView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.time.Instant;

public class YoutubeAPI {
    private static String API_KEY = "";
    private static String API_URL = "https://www.googleapis.com/youtube/v3";

    public static String getVideoId(String link) {
        String videoId;
        if (link.contains("youtu.be/")) {
            videoId = link.split("youtu.be/")[1];
        } else {
            videoId = link.split("\\?v=")[1];
        }
        videoId = videoId.substring(0, 11);
        return videoId;
    }

    public static String getChannelId(String link) {
        String[] channelId = new String[2];
        if (link.contains("/channel/")) {
            channelId[0] = "&id=";
            channelId[1] = link.split("/channel/")[1];
        } else if (link.contains("/c/")) {
            channelId[0] = "&forUsername=";
            channelId[1] = link.split("/c/")[1];
        } else if (link.contains("/user/")) {
            channelId[0] = "&forUsername=";
            channelId[1] = link.split("/user/")[1];
        } else {
            channelId[0] = "&forUsername=";
            channelId[1] = link.split("youtube.com/")[1];
        }
        if (channelId[1].contains("/")) {
            channelId[1] = channelId[1].split("/")[0];
        }
        channelId[1] = channelId[1].replace("@", "");
        return channelId[0] + channelId[1];
    }

    public static JSONObject getVideoStats(String videoId) {
        try  {
            URL url = new URL(API_URL + "/videos?part=snippet&part=statistics&id=" + videoId + API_KEY);
            URLConnection conn = url.openConnection();
            conn.connect();

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();

            JSONObject jsonObj = new JSONObject(sb.toString());
            JSONObject snippet = jsonObj.getJSONArray("items").getJSONObject(0).getJSONObject("snippet");
            JSONObject statistics = jsonObj.getJSONArray("items").getJSONObject(0).getJSONObject("statistics");

            jsonObj = new JSONObject();
            jsonObj.put("videoName", snippet.getString("title"));
            jsonObj.put("channelName", snippet.getString("channelTitle"));
            jsonObj.put("thumbnail", snippet.getJSONObject("thumbnails").getJSONObject("default").getString("url"));
            jsonObj.put("numViews", statistics.getInt("viewCount"));
            jsonObj.put("numLikes", statistics.getInt("likeCount"));
            jsonObj.put("numComments", statistics.getInt("commentCount"));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                jsonObj.put("snapshotDate", Instant.now().toEpochMilli());
            }

            return jsonObj;
        } catch (Exception e) {}

        return null;
    }

    public static JSONObject getChannelStats(String channelId) {
        try  {
            URL url = new URL(API_URL + "/channels?part=snippet&part=statistics" + channelId + API_KEY);
            URLConnection conn = url.openConnection();
            conn.connect();

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();

            JSONObject jsonObj = new JSONObject(sb.toString());
            JSONObject snippet = jsonObj.getJSONArray("items").getJSONObject(0).getJSONObject("snippet");
            JSONObject statistics = jsonObj.getJSONArray("items").getJSONObject(0).getJSONObject("statistics");

            jsonObj = new JSONObject();
            jsonObj.put("channelName", snippet.getString("title"));
            jsonObj.put("thumbnail", snippet.getJSONObject("thumbnails").getJSONObject("default").getString("url"));
            jsonObj.put("numViews", statistics.getInt("viewCount"));
            jsonObj.put("numSubscribers", statistics.getInt("subscriberCount"));
            jsonObj.put("numVideos", statistics.getInt("videoCount"));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                jsonObj.put("snapshotDate", Instant.now().toEpochMilli());
            }

            return jsonObj;

        } catch (Exception e) {
            for (StackTraceElement el : e.getStackTrace()) {
                Log.d("tubular-analytics", el.toString());
            }
        }

        return null;
    }

    public static void showThumbnailFromUrl(ImageView imageView, String imageURL) {
        new DownloadImageTask(imageView).execute(imageURL);
    }

    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}
