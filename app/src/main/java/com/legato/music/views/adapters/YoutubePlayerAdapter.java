package com.legato.music.views.adapters;


import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;

import com.legato.music.R;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.List;

public class YoutubePlayerAdapter extends RecyclerView.Adapter<YoutubePlayerAdapter.ViewHolder> {
    private List<String> videoIds;
    private Lifecycle lifecycle;

    public YoutubePlayerAdapter(List<String> videoIds, Lifecycle lifecycle) {
        this.videoIds = videoIds;
        this.lifecycle = lifecycle;
    }

    @NonNull
    @Override
    public YoutubePlayerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        YouTubePlayerView youTubePlayerView = (YouTubePlayerView) LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_youtube_view, parent, false);
        lifecycle.addObserver(youTubePlayerView);

        return new ViewHolder(youTubePlayerView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.cueVideo(videoIds.get(position));

    }

    @Override
    public int getItemCount() {
        return videoIds.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private YouTubePlayerView youTubePlayerView;
        @Nullable
        private YouTubePlayer youTubePlayer;
        @Nullable
        private String currentVideoId;

        ViewHolder(YouTubePlayerView playerView) {
            super(playerView);
            youTubePlayerView = playerView;

            youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady(@NonNull YouTubePlayer initializedYouTubePlayer) {
                    youTubePlayer = initializedYouTubePlayer;
                    youTubePlayer.cueVideo(currentVideoId, 0);
                }
            });
        }

        void cueVideo(String videoId) {
            currentVideoId = videoId;

            if(youTubePlayer == null)
                return;

            youTubePlayer.cueVideo(videoId, 0);
        }
    }
}