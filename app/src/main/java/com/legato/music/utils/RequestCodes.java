package com.legato.music.utils;

public final class RequestCodes {
    /** Request code for starting when starting the flow to get youtube video */
    public static final int RC_YOUTUBE_SEARCH = 100;
    public static final int RC_SIGN_IN = 101;
    public static final int RC_SPOTIFY_TRACK = 102;

    private RequestCodes() {
        throw new AssertionError("No instance for you!");
    }
}

