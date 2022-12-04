package com.example.tubularanalytics.pages.overview_pages;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tubularanalytics.R;
import com.example.tubularanalytics.api.YoutubeAPI;
import com.example.tubularanalytics.database.VideoDB;
import com.example.tubularanalytics.pages.selected_pages.SelectedVideo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class VideoOverview extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_overview);

        ArrayList<JSONObject> allVideos = new VideoDB(this).getAllVideoStats();
        VideoOverviewAdapter videoOverviewAdapter = new VideoOverviewAdapter(VideoOverview.this,this, allVideos);

        RecyclerView recyclerView = findViewById(R.id.videoOverview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(videoOverviewAdapter);
    }
    public class VideoOverviewAdapter extends RecyclerView.Adapter<VideoOverviewAdapter.MyViewHolder> {

        private Context context;
        private Activity activity;
        private ArrayList<JSONObject> allData;

        VideoOverviewAdapter(Activity activity, Context context, ArrayList<JSONObject> allData) {
            this.activity = activity;
            this.context = context;
            this.allData = allData;
        }

        @NonNull
        @Override
        public VideoOverviewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.video_overview_row, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull VideoOverviewAdapter.MyViewHolder holder, int position) {
            try {
                String video_id  = allData.get(position).getString("_id");
                String videoName = allData.get(position).getString("videoName");
                String thumbnailUrl = allData.get(position).getString("thumbnail");

                holder.videoName.setText(videoName);
                YoutubeAPI.showThumbnailFromUrl(holder.thumbnail, thumbnailUrl);

                holder.deleteVideoButton.setOnClickListener((View view) -> {
                        new VideoDB(context).deleteVideo(video_id);
                        allData.remove(position);
                        notifyDataSetChanged();
                });

                holder.videoOverviewRow.setOnClickListener((View view) -> {
                    Intent intent = new Intent(context, SelectedVideo.class);
                    intent.putExtra("video_id", video_id);
                    intent.putExtra("videoName", videoName);
                    intent.putExtra("thumbnailUrl", thumbnailUrl);
                    activity.startActivityForResult(intent, 1);
                });
            } catch (JSONException e) {
                Log.d("tubular-analytics", e.toString());
            }
        }

        @Override
        public int getItemCount() { return allData.size(); }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            private ImageView thumbnail;
            private TextView videoName;
            private Button deleteVideoButton;
            private LinearLayout videoOverviewRow;

            public MyViewHolder(@NonNull View view) {
                super(view);
                thumbnail = view.findViewById(R.id.video_thumbnail);
                videoName = view.findViewById(R.id.video_name);
                deleteVideoButton = view.findViewById(R.id.deleteVideo);
                videoOverviewRow = view.findViewById(R.id.videoOverviewRow);
            }
        }
    }
}
