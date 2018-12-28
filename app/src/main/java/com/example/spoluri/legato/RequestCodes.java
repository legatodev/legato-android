package com.example.spoluri.legato;

import android.support.annotation.RestrictTo;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public final class RequestCodes {
    /** Request code for starting when starting the flow to get youtube video */
    public static final int YOUTUBE_VIDEO_FLOW = 100;

    private RequestCodes() {
        throw new AssertionError("No instance for you!");
    }
}

