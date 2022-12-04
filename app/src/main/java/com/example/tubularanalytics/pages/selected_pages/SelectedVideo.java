package com.example.tubularanalytics.pages.selected_pages;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tubularanalytics.R;
import com.example.tubularanalytics.api.YoutubeAPI;
import com.example.tubularanalytics.database.VideoDB;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;

public class SelectedVideo extends AppCompatActivity {

    private SelectedVideoAdapter selectedVideoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_selection);
        String video_id = getIntent().getStringExtra("video_id");
        String videoName = getIntent().getStringExtra("videoName");
        String thumbnailUrl = getIntent().getStringExtra("thumbnailUrl");
        ArrayList<JSONObject> snapshots = new VideoDB(this).getVideoSnapshots(video_id);
        Collections.reverse(snapshots);
        this.selectedVideoAdapter = new SelectedVideoAdapter(
                SelectedVideo.this,this, snapshots);
        setRecyclerView();

        // Display video title and thumbnail
        TextView nameTextview = findViewById(R.id.video_name);
        ImageView thumbnailTextview = findViewById(R.id.video_thumbnail);
        nameTextview.setText(videoName);
        YoutubeAPI.showThumbnailFromUrl(thumbnailTextview, thumbnailUrl);

        // New snapshot
        Button newSnapshotBtn = findViewById(R.id.new_video_snapshot);
        newSnapshotBtn.setOnClickListener((View view) -> {
            new Thread(() -> {
                new VideoDB(this).saveStatsFromID(video_id);
                Intent i = new Intent(SelectedVideo.this, SelectedVideo.class);
                i.putExtra("video_id", video_id);
                i.putExtra("videoName", videoName);
                i.putExtra("thumbnailUrl", thumbnailUrl);
                finish();
                overridePendingTransition(0, 0);
                startActivity(i);
                overridePendingTransition(0, 0);
            }).start();
        });
    }

    private void setRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.videoSelection);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(selectedVideoAdapter);
    }

    public class SelectedVideoAdapter extends RecyclerView.Adapter<SelectedVideoAdapter.MyViewHolder> {

        private Context context;
        private Activity activity;
        private ArrayList<JSONObject> snapshots;

        SelectedVideoAdapter(Activity activity, Context context, ArrayList<JSONObject> snapshots) {
            this.activity = activity;
            this.context = context;
            this.snapshots = snapshots;
        }

        @NonNull
        @Override
        public SelectedVideoAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.video_selection_row, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            try {
                String snapshotID = snapshots.get(position).getString("ID");

                // Get date in local time
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    DateFormat df = new SimpleDateFormat("dd MMM yyyy, hh:mma");
                    holder.snapshotDate.setText(df.format(snapshots.get(position).getLong("snapshotDate")));
                }
                holder.numViews.setText(snapshots.get(position).getString("numViews"));
                holder.numLikes.setText(snapshots.get(position).getString("numLikes"));
                holder.numComments.setText(snapshots.get(position).getString("numComments"));

                // Calculate % change
                if (position < snapshots.size()-1) {
                    double viewsBefore = snapshots.get(position+1).getInt("numViews");
                    double viewsAfter = snapshots.get(position).getInt("numViews");
                    double likesBefore = snapshots.get(position+1).getInt("numLikes");
                    double likesAfter = snapshots.get(position).getInt("numLikes");
                    double commentsBefore = snapshots.get(position+1).getInt("numComments");
                    double commentsAfter = snapshots.get(position).getInt("numComments");

                    double viewsPChange = getPchange(viewsBefore, viewsAfter);
                    double likesPChange = getPchange(likesBefore, likesAfter);
                    double commentsPChange = getPchange(commentsBefore, commentsAfter);

                    holder.viewsPchange.setText(viewsPChange + "%");
                    holder.likesPchange.setText(likesPChange + "%");
                    holder.commentsPchange.setText(commentsPChange + "%");
                    if (viewsPChange >= 0) { holder.viewsPchange.setText("+" + viewsPChange + "%"); }
                    if (likesPChange >= 0) { holder.likesPchange.setText("+" + likesPChange + "%"); }
                    if (commentsPChange >= 0) { holder.commentsPchange.setText("+" + commentsPChange + "%"); }
                }

                holder.deleteSnapshotBtn.setOnClickListener((View view) -> {
                    if (snapshots.size() == 1) {
                        Toast.makeText(context, "Must have at least one snapshot!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    new VideoDB(context).deleteVideoSnapshot(snapshotID);
                    snapshots.remove(position);
                    notifyDataSetChanged();
                });


            } catch (Exception e) {
                for (StackTraceElement el : e.getStackTrace()) {
                    Log.d("tubular-analytics", el.toString());
                }
            }
        }

        private double getPchange(double before, double after) {
            double pChange = ((after - before) / before) * 100.00;
            return Math.round(pChange * 100.00) / 100.00;
        }

        @Override
        public int getItemCount() { return snapshots.size(); }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView snapshotDate;
            private TextView numViews;
            private TextView numLikes;
            private TextView numComments;
            private TextView viewsPchange;
            private TextView likesPchange;
            private TextView commentsPchange;
            private Button deleteSnapshotBtn;

            public MyViewHolder(@NonNull View view) {
                super(view);
                snapshotDate = view.findViewById(R.id.video_date);
                numViews = view.findViewById(R.id.video_views);
                numLikes = view.findViewById(R.id.video_likes);
                numComments = view.findViewById(R.id.video_comments);
                viewsPchange = view.findViewById(R.id.video_views_pchange);
                likesPchange = view.findViewById(R.id.video_likes_pchange);
                commentsPchange = view.findViewById(R.id.video_comments_pchange);
                deleteSnapshotBtn = view.findViewById(R.id.delete_video_snapshot);
            }
        }
    }
}
