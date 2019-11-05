package com.legato.music.youtube;

import android.content.Context;
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

public class YoutubePlayerAdapter extends RecyclerView.Adapter<YoutubePlayerAdapter.ViewHolder> {

    private LayoutInflater mInflator;
    private String[] videoIds;
    private Lifecycle lifecycle;

    public YoutubePlayerAdapter(Context context, String[] videoIds, Lifecycle lifecycle)
    {
        mInflator = LayoutInflater.from(context);
        this.videoIds = videoIds;
        this.lifecycle = lifecycle;
    }

    @NonNull
    @Override
    public YoutubePlayerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        YouTubePlayerView youTubePlayerView = (YouTubePlayerView)mInflator
                .inflate(R.layout.item_youtube_view, parent,false);
        lifecycle.addObserver(youTubePlayerView);

        return new ViewHolder(youTubePlayerView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.cueVideo(videoIds[position]);
    }

    public void setVideoIds(String[] videoIds)
    {
        this.videoIds = videoIds;
    }

    @Override
    public int getItemCount() {
        return videoIds.length;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final YoutubePlayerAdapter mAdapter;

        private YouTubePlayerView youTubePlayerView;
        @Nullable private YouTubePlayer youTubePlayer;
        private String currentVideoId;

        ViewHolder(YouTubePlayerView playerView, YoutubePlayerAdapter adapter) {
            super(playerView);

            youTubePlayerView = playerView;
            this.mAdapter = adapter;

            youTubePlayer = null;
            currentVideoId = "";

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

            if(youTubePlayer == null) {
                return;
            }

            youTubePlayer.cueVideo(videoId, 0);
        }
    }
}
