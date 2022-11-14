package com.example.tubularanalytics.api;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class YoutubeAPI {
    private static String API_KEY = "";
    private static String API_URL = "https://www.googleapis.com/youtube/v3";

    private static String parseLink(String link) {
        return link;
    }

    public static String getVideoFromLink(String link) {

        String videoId;
        if (link.contains("youtu.be/")) {
            videoId = link.split("youtu.be/")[1];
        } else {
            videoId = link.split("\\?v=")[1];
        }

        videoId = videoId.substring(0, 11);

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

            String videoTitle = snippet.getString("title");
            String channelTitle = snippet.getString("channelTitle");
            String numViews = statistics.getString("viewCount");
            String numLikes = statistics.getString("likeCount");
            String numComments = statistics.getString("commentCount");

            return "\n_\nTitle: " + videoTitle + "\nViews: " + numViews + "\nLikes: " + numLikes + "\nComments: " + numComments;
        } catch (Exception e) {
            Log.d("bruh", e.toString());
        }

        return "";
    }

    public static void getChannelFromLink(String link) {
        String channelId;
        if (link.contains("/channel/")) {
            channelId = "&id=" + link.split("/channel/")[1];
        } else if (link.contains("/c/")) {
            channelId = "&forUsername=" + link.split("/c/")[1];
        } else if (link.contains("/user/")) {
            channelId = "&forUsername=" + link.split("/user/")[1];
        } else {
            channelId = "&forUsername=" + link.split("youtube.com/")[1];
        }

        if (channelId.contains("/")) {
            channelId = channelId.split("/")[0];
        }

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

            String channelTitle = snippet.getString("title");
            String numViews = statistics.getString("viewCount");
            String numSubscribers = statistics.getString("subscriberCount");
            String numVideos = statistics.getString("videoCount");
        } catch (Exception e) {
            Log.d("bruh", e.toString());
        }
    }

}
