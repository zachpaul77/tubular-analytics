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
import com.example.tubularanalytics.database.ChannelDB;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;

public class SelectedChannel extends AppCompatActivity {

    private SelectedChannelAdapter selectedChannelAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.channel_selection);
        String channel_id = getIntent().getStringExtra("channel_id");
        String channelName = getIntent().getStringExtra("channelName");
        String thumbnailUrl = getIntent().getStringExtra("thumbnailUrl");
        ArrayList<JSONObject> snapshots = new ChannelDB(this).getChannelSnapshots(channel_id);
        Collections.reverse(snapshots);
        this.selectedChannelAdapter = new SelectedChannelAdapter(
                SelectedChannel.this,this, snapshots);
        setRecyclerView();

        // Display channel title and thumbnail
        TextView nameTextview = findViewById(R.id.channel_name);
        ImageView thumbnailTextview = findViewById(R.id.channel_thumbnail);
        nameTextview.setText(channelName);
        YoutubeAPI.showThumbnailFromUrl(thumbnailTextview, thumbnailUrl);

        // New snapshot
        Button newSnapshotBtn = findViewById(R.id.new_channel_snapshot);
        newSnapshotBtn.setOnClickListener((View view) -> {
            new Thread(() -> {
                new ChannelDB(this).saveStatsFromID(channel_id);
                Intent i = new Intent(SelectedChannel.this, SelectedChannel.class);
                i.putExtra("channel_id", channel_id);
                i.putExtra("channelName", channelName);
                i.putExtra("thumbnailUrl", thumbnailUrl);
                finish();
                overridePendingTransition(0, 0);
                startActivity(i);
                overridePendingTransition(0, 0);
            }).start();
        });
    }

    private void setRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.channelSelection);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(selectedChannelAdapter);
    }

    public class SelectedChannelAdapter extends RecyclerView.Adapter<SelectedChannelAdapter.MyViewHolder> {

        private Context context;
        private Activity activity;
        private ArrayList<JSONObject> snapshots;

        SelectedChannelAdapter(Activity activity, Context context, ArrayList<JSONObject> snapshots) {
            this.activity = activity;
            this.context = context;
            this.snapshots = snapshots;
        }

        @NonNull
        @Override
        public SelectedChannelAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.channel_selection_row, parent, false);
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
                holder.numVideos.setText(snapshots.get(position).getString("numVideos"));
                holder.numSubscribers.setText(snapshots.get(position).getString("numSubscribers"));

                // Calculate % change
                if (position < snapshots.size()-1) {
                    double viewsBefore = snapshots.get(position+1).getInt("numViews");
                    double viewsAfter = snapshots.get(position).getInt("numViews");
                    double videosBefore = snapshots.get(position+1).getInt("numVideos");
                    double videosAfter = snapshots.get(position).getInt("numVideos");
                    double subscribersBefore = snapshots.get(position+1).getInt("numSubscribers");
                    double subscribersAfter = snapshots.get(position).getInt("numSubscribers");

                    double viewsPChange = getPchange(viewsBefore, viewsAfter);
                    double videosPChange = getPchange(videosBefore, videosAfter);
                    double subscribersPChange = getPchange(subscribersBefore, subscribersAfter);

                    holder.viewsPchange.setText(viewsPChange + "%");
                    holder.videosPchange.setText(videosPChange + "%");
                    holder.subscribersPchange.setText(subscribersPChange + "%");
                    if (viewsPChange >= 0) { holder.viewsPchange.setText("+" + viewsPChange + "%"); }
                    if (videosPChange >= 0) { holder.videosPchange.setText("+" + videosPChange + "%"); }
                    if (subscribersPChange >= 0) { holder.subscribersPchange.setText("+" + subscribersPChange + "%"); }
                }

                holder.deleteSnapshotBtn.setOnClickListener((View view) -> {
                    if (snapshots.size() == 1) {
                        Toast.makeText(context, "Must have at least one snapshot!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    new ChannelDB(context).deleteChannelSnapshot(snapshotID);
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
            private TextView numVideos;
            private TextView numSubscribers;
            private TextView viewsPchange;
            private TextView videosPchange;
            private TextView subscribersPchange;
            private Button deleteSnapshotBtn;

            public MyViewHolder(@NonNull View view) {
                super(view);
                snapshotDate = view.findViewById(R.id.channel_date);
                numViews = view.findViewById(R.id.channel_views);
                numVideos = view.findViewById(R.id.channel_videos);
                numSubscribers = view.findViewById(R.id.channel_subscribers);
                viewsPchange = view.findViewById(R.id.channel_views_pchange);
                videosPchange = view.findViewById(R.id.channel_videos_pchange);
                subscribersPchange = view.findViewById(R.id.channel_subscribers_pchange);
                deleteSnapshotBtn = view.findViewById(R.id.delete_channel_snapshot);
            }
        }
    }
}
