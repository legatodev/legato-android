package com.legato.music.views.adapters;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.base.Joiner;
import com.legato.music.AppConstants;
import com.legato.music.R;
import com.legato.music.spotify.Player;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;
import retrofit.client.Response;

public class MediaPlayerAdapter extends RecyclerView.Adapter<MediaPlayerAdapter.MediaViewHolder> {
    public interface TrackRemovedListener {
        void onTrackRemoved(View v, int position);
    }

    protected List<String> trackIds;
    protected Lifecycle lifecycle;
    @Nullable private SpotifyService spotifyService;
    @Nullable private final TrackRemovedListener mOnTrackRemovedListener;
    @Nullable private Player playerService;
    private boolean editing = false;
    private final int YOUTUBE = 0, SPOTIFY = 1;

    public MediaPlayerAdapter(List<String> trackIds,
                              Lifecycle lifecycle,
                              boolean editing,
                              @Nullable TrackRemovedListener onTrackRemovedListener) {
        this.trackIds = trackIds;
        this.lifecycle = lifecycle;
        this.editing = editing;
        this.mOnTrackRemovedListener = onTrackRemovedListener;
    }

    public MediaPlayerAdapter(
            List<String> trackIds,
            Lifecycle lifecycle,
            @Nullable SpotifyService spotifyService,
            @Nullable Player playerService,
            boolean editing,
            @Nullable TrackRemovedListener onTrackRemovedListener) {
        this.trackIds = trackIds;
        this.lifecycle = lifecycle;
        this.spotifyService = spotifyService;
        this.playerService = playerService;
        this.editing = editing;
        this.mOnTrackRemovedListener = onTrackRemovedListener;
    }

    @NonNull
    @Override
    public MediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == YOUTUBE) {
            FrameLayout youtubeLayout = (FrameLayout) LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.item_youtube_view, parent, false);

            YouTubePlayerView youTubePlayerView = youtubeLayout.findViewById(R.id.youtube_recycler_item);
            lifecycle.addObserver(youTubePlayerView);

            return new YoutubeViewHolder(youtubeLayout);
        } else {
            FrameLayout spotifyTrackView = (FrameLayout) LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.item_spotify_track, parent, false);

            return new SpotifyViewHolder(spotifyTrackView, spotifyService, playerService);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MediaViewHolder viewHolder, int position) {
        viewHolder.cueTrack(trackIds.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        if (trackIds.get(position).contains(AppConstants.SPOTIFY)) {
            return SPOTIFY;
        } else {
            return YOUTUBE;
        }
    }

    @Override
    public int getItemCount() {
        return trackIds.size();
    }

    abstract class MediaViewHolder extends RecyclerView.ViewHolder {
        MediaViewHolder(FrameLayout view) {
            super(view);
        }

        abstract void cueTrack(String trackId);
    }

    class YoutubeViewHolder extends MediaViewHolder {
        private YouTubePlayerView youTubePlayerView;
        @Nullable
        private YouTubePlayer youTubePlayer;
        @Nullable
        private String currentVideoId;
        private ImageView deleteYoutubeVideoImageView;

        YoutubeViewHolder(FrameLayout playerLayout) {
            super(playerLayout);
            youTubePlayerView = playerLayout.findViewById(R.id.youtube_recycler_item);
            deleteYoutubeVideoImageView = playerLayout.findViewById(R.id.deleteYoutubeVideoImageView);
            if (editing) {
                deleteYoutubeVideoImageView.setVisibility(View.VISIBLE);
                deleteYoutubeVideoImageView.setOnClickListener(v -> {
                    Log.e("YoutubeViewHolder", "Delete clicked");
                    if (mOnTrackRemovedListener != null) {
                        mOnTrackRemovedListener.onTrackRemoved(v, getAdapterPosition());
                    }
                });
            }

            youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady(@NonNull YouTubePlayer initializedYouTubePlayer) {
                    youTubePlayer = initializedYouTubePlayer;
                    youTubePlayer.cueVideo(currentVideoId, 0);
                }
            });
        }

        void cueTrack(String videoId) {
            currentVideoId = videoId;

            if(youTubePlayer == null)
                return;

            youTubePlayer.cueVideo(videoId, 0);
        }
    }

    class SpotifyViewHolder extends MediaViewHolder {
        private final String TAG = SpotifyViewHolder.class.getSimpleName();

        private FrameLayout spotifyTrackView;
        private ImageView albumCover;
        private TextView trackTitle;
        private TextView trackArtist;
        @Nullable private SpotifyService mSpotifyService;
        @Nullable private Player mPlayerService;

        SpotifyViewHolder(FrameLayout view, @Nullable SpotifyService spotifyService, @Nullable Player playerService) {
            super(view);
            spotifyTrackView = view;
            albumCover = spotifyTrackView.findViewById(R.id.track_album_cover);
            trackTitle = spotifyTrackView.findViewById(R.id.track_title);
            trackArtist = spotifyTrackView.findViewById(R.id.track_artist);
            ImageView deleteSpotifyTrackImageView = spotifyTrackView.findViewById(R.id.deleteSpotifyTrackImageView);
            if (editing) {
                deleteSpotifyTrackImageView.setVisibility(View.VISIBLE);
                deleteSpotifyTrackImageView.setOnClickListener(v -> {
                    Log.e("SpotifyViewHolder", "Delete clicked");
                    if (mOnTrackRemovedListener != null) {
                        mOnTrackRemovedListener.onTrackRemoved(v, getAdapterPosition());
                    }
                });
            }

            mSpotifyService = spotifyService;
            mPlayerService = playerService;
        }

        void cueTrack(String track) {
            if (mSpotifyService != null) {
                String trackId = track.replace("spotify:track:", "");
                mSpotifyService.getTrack(trackId, new SpotifyCallback<Track>() {
                    @Override
                    public void success(Track track, Response response) {
                        Image image = track.album.images.get(0);
                        String trackName = track.name;
                        List<String> names = new ArrayList<>();
                        for (ArtistSimple i : track.artists) {
                            names.add(i.name);
                        }

                        Joiner joiner = Joiner.on(", ");
                        String artist = joiner.join(names);

                        Picasso.with(albumCover.getContext()).load(image.url).into(albumCover);
                        trackTitle.setText(trackName);
                        trackArtist.setText(artist);
                        spotifyTrackView.setOnClickListener(v -> {
                            if (mPlayerService != null) {
                                mPlayerService.playTrack(track);
                            } else {
                                Log.e(TAG, "Player not initialized");
                            }
                        });
                    }

                    @Override
                    public void failure(SpotifyError spotifyError) {
                        Log.e(TAG, spotifyError.getMessage());
                    }
                });
            } else {
                Log.e(TAG, "Spotify Service is null");
            }
        }
    }
}