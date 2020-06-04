package com.example.safetyapp.user;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.safetyapp.Globals;
import com.example.safetyapp.R;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayerBridge;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;


public class TutorialActivity extends AppCompatActivity {

    public static String TAG = TutorialActivity.class.getSimpleName();
    RecyclerView listView;
    TutorialsAdapter tutorialsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        listView = findViewById(R.id.listview_tutorial);
        listView.setHasFixedSize(true);
        listView.setLayoutManager(new LinearLayoutManager(this));
        tutorialsAdapter = new TutorialsAdapter();
        listView.setAdapter(tutorialsAdapter);

    }

    public class TutorialsAdapter extends RecyclerView.Adapter<TutorialsAdapter.VideoHolder>{

        ArrayList<tutorial> tutoriallist ;
        public String TAG = TutorialsAdapter.class.getSimpleName();

        public TutorialsAdapter() {
            tutoriallist = new ArrayList<>();
            for(int i = 0;i<Globals.tutorialIDs.length;i++){
                tutorial tut = new tutorial(Globals.tutorialIDs[i],Globals.tutorialTITLEs[i]);
                tutoriallist.add(tut);
            }
        }

        @NonNull
        @Override
        public VideoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tutorial_card,parent,false);
            return new VideoHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull VideoHolder holder, final int position) {
            holder.title.setText(tutoriallist.get(position).getTitle());
            holder.youTubePlayerView.addYouTubePlayerListener(new YouTubePlayerListener() {
                @Override
                public void onReady(YouTubePlayer youTubePlayer) {
                    youTubePlayer.cueVideo(tutoriallist.get(position).getId(),0);
                }

                @Override
                public void onStateChange(YouTubePlayer youTubePlayer, PlayerConstants.PlayerState playerState) {

                }

                @Override
                public void onPlaybackQualityChange(YouTubePlayer youTubePlayer, PlayerConstants.PlaybackQuality playbackQuality) {

                }

                @Override
                public void onPlaybackRateChange(YouTubePlayer youTubePlayer, PlayerConstants.PlaybackRate playbackRate) {

                }

                @Override
                public void onError(YouTubePlayer youTubePlayer, PlayerConstants.PlayerError playerError) {

                }

                @Override
                public void onCurrentSecond(YouTubePlayer youTubePlayer, float v) {

                }

                @Override
                public void onVideoDuration(YouTubePlayer youTubePlayer, float v) {

                }

                @Override
                public void onVideoLoadedFraction(YouTubePlayer youTubePlayer, float v) {

                }

                @Override
                public void onVideoId(YouTubePlayer youTubePlayer, String s) {

                }

                @Override
                public void onApiChange(YouTubePlayer youTubePlayer) {

                }
            });
        }

        @Override
        public int getItemCount() {
            Log.d("Size", String.valueOf(tutoriallist.size()));
            return tutoriallist.size();
        }

        public class VideoHolder extends RecyclerView.ViewHolder{
            YouTubePlayerView youTubePlayerView;
            TextView title ;
            public VideoHolder(@NonNull View itemView) {
                super(itemView);
                youTubePlayerView = itemView.findViewById(R.id.youtube_player_view);
                title = itemView.findViewById(R.id.tutorial_title);
            }

        }
    }

    public class tutorial{
        String id;
        String title;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public tutorial(String id, String title) {
            this.id = id;
            this.title = title;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public tutorial(String id) {
            this.id = id;
        }
    }

}


    