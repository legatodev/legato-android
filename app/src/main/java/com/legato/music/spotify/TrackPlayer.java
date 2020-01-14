package com.legato.music.spotify;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import com.legato.music.AppConstants;
import com.legato.music.R;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import java.io.IOException;

import androidx.annotation.Nullable;
import kaaes.spotify.webapi.android.models.Track;

public class TrackPlayer implements Player, MediaPlayer.OnCompletionListener {

    private static final String TAG = TrackPlayer.class.getSimpleName();

    @Nullable private MediaPlayer mMediaPlayer;
    @Nullable private String mCurrentTrack;
    @Nullable private SpotifyAppRemote mSpotifyAppRemote;
    private Context mContext;

    private class OnPreparedListener implements MediaPlayer.OnPreparedListener {

        private final String mUrl;

        public OnPreparedListener(String url) {
            mUrl = url;
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            mp.start();
            mCurrentTrack = mUrl;
        }
    }

    TrackPlayer(Context context) {
        mContext = context;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //TODO: inform search results adapter
        release();
    }

    private void playPreview(String url) {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }

        if (url == null || url.isEmpty()) {
            Log.e(TAG, "Track doesn't have a preview");
            return;
        }

        try {
            createMediaPlayer(url);
            mCurrentTrack = url;
        } catch (IOException e) {
            Log.e(TAG, "Could not play: " + url, e);
        }
    }

    @Override
    public void pause() {
        Log.d(TAG, "Pause");
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        } else if (mSpotifyAppRemote != null){
            mSpotifyAppRemote.getPlayerApi().pause();
        }
    }

    @Override
    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
            mCurrentTrack = null;
        } else if (mSpotifyAppRemote != null) {
            mSpotifyAppRemote.getPlayerApi().pause();
            SpotifyAppRemote.disconnect(mSpotifyAppRemote);
        }
    }

    @Override
    public void resume() {
        Log.d(TAG, "Resume");
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
        } else if (mSpotifyAppRemote != null){
            mSpotifyAppRemote.getPlayerApi().resume();
        }
    }

    private void createMediaPlayer(String url) throws IOException {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setDataSource(url);
        mMediaPlayer.setOnPreparedListener(new OnPreparedListener(url));
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.prepareAsync();
    }

    @Override
    public void playTrack(Track track) {
        if (mSpotifyAppRemote == null) {
            // Set the connection parameters
            ConnectionParams connectionParams =
                    new ConnectionParams.Builder(mContext.getResources().getString(R.string.spotify_client_id))
                            .setRedirectUri(AppConstants.SPOTIFY_REDIRECT_URI)
                            .showAuthView(true)
                            .build();

            SpotifyAppRemote.connect(mContext, connectionParams,
                    new Connector.ConnectionListener() {
                        @Override
                        public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                            mSpotifyAppRemote = spotifyAppRemote;
                            mSpotifyAppRemote.getPlayerApi().play(track.uri);
                        }

                        @Override
                        public void onFailure(Throwable error) {
                            Log.e(TAG, String.format("Connection failed: %s", error));
                            //Could not connect to spotify app to play track. Try playing a preview.
                            playPreview(track.preview_url);
                        }
                    });
        } else {
            mSpotifyAppRemote.getPlayerApi().play(track.uri);
        }
    }
}
