package com.example.spoluri.legato;

import android.os.Bundle;

import android.content.Intent;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;

import com.example.spoluri.legato.connections.*;

public class YoutubeActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener, ServerResponseListener {

    private static final int RECOVERY_REQUEST = 1;
    private YouTubePlayerView youTubeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube);

        youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        youTubeView.initialize(AppConstants.YOUTUBE_API_KEY, this);

        // Service to search video
        final String keyWord = "Jordan";
        ServiceTask mYtServiceTask = new ServiceTask(AppConstants.SEARCH_VIDEO);
        mYtServiceTask.setmServerResponseListener(this);
        mYtServiceTask.execute(new String[]{keyWord});
    }

    @Override
    public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasRestored) {
        if (!wasRestored) {
            player.cueVideo("PuWHYJF8Il8");
        }
    }

    @Override
    public void onInitializationFailure(Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_REQUEST).show();
        } else {
            String error = String.format(getString(R.string.player_error), errorReason.toString());
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(AppConstants.YOUTUBE_API_KEY, this);
        }
    }

    protected Provider getYouTubePlayerProvider() {
        return youTubeView;
    }

    @Override
    public void prepareRequest(Object... objects) {

    }

    @Override
    public void goBackground(Object... objects) {

    }

    @Override
    public void completedRequest(Object... objects) {

    }
}
