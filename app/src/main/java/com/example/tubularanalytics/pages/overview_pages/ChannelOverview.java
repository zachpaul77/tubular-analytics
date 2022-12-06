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
import com.example.tubularanalytics.database.ChannelDB;
import com.example.tubularanalytics.pages.AddChannel;
import com.example.tubularanalytics.pages.selected_pages.SelectedChannel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChannelOverview extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.channel_overview);

        ArrayList<JSONObject> allChannels = new ChannelDB(this).getAllChannelStats();
        ChannelOverviewAdapter channelOverviewAdapter = new ChannelOverviewAdapter(ChannelOverview.this,this, allChannels);

        RecyclerView recyclerView = findViewById(R.id.channelOverview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(channelOverviewAdapter);
    }

    public void gotoAddChannel(View view) {
        finish();
        Intent intent = new Intent(getApplicationContext(), AddChannel.class);
        startActivity(intent);
    }

    public class ChannelOverviewAdapter extends RecyclerView.Adapter<ChannelOverviewAdapter.MyViewHolder> {

        private Context context;
        private Activity activity;
        private ArrayList<JSONObject> allData;

        ChannelOverviewAdapter(Activity activity, Context context, ArrayList<JSONObject> allData) {
            this.activity = activity;
            this.context = context;
            this.allData = allData;
        }

        @NonNull
        @Override
        public ChannelOverviewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.channel_overview_row, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ChannelOverviewAdapter.MyViewHolder holder, int position) {
            try {
                String channel_id  = allData.get(position).getString("_id");
                String channelName = allData.get(position).getString("channelName");
                String thumbnailUrl = allData.get(position).getString("thumbnail");

                holder.channelName.setText(channelName);
                YoutubeAPI.showThumbnailFromUrl(holder.thumbnail, thumbnailUrl);

                holder.deleteChannelButton.setOnClickListener((View view) -> {
                    new ChannelDB(context).deleteChannel(channel_id);
                    allData.remove(position);
                    notifyDataSetChanged();
                });

                holder.channelOverviewRow.setOnClickListener((View view) -> {
                    Intent intent = new Intent(context, SelectedChannel.class);
                    intent.putExtra("channel_id", channel_id);
                    intent.putExtra("channelName", channelName);
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
            private TextView channelName;
            private Button deleteChannelButton;
            private LinearLayout channelOverviewRow;

            public MyViewHolder(@NonNull View view) {
                super(view);
                thumbnail = view.findViewById(R.id.channel_thumbnail);
                channelName = view.findViewById(R.id.channel_name);
                deleteChannelButton = view.findViewById(R.id.deleteChannel);
                channelOverviewRow = view.findViewById(R.id.channelOverviewRow);
            }
        }
    }
}
