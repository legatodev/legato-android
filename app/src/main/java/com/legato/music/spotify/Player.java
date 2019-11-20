package com.legato.music.spotify;

import androidx.annotation.Nullable;
import kaaes.spotify.webapi.android.models.Track;

public interface Player {

    void playTrack(Track track);

    void pause();

    void resume();

    void release();
}
