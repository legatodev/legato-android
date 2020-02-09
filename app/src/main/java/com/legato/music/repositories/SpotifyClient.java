package com.legato.music.repositories;

import androidx.annotation.Nullable;

/*
Any interaction with FireBase frame should be done by this class
*/

class SpotifyClient {

    @Nullable private  static SpotifyClient mSpotifyinstance;
    private String mAccessToken = "";

    public static SpotifyClient getInstance(){
        if(mSpotifyinstance == null){
            mSpotifyinstance = new SpotifyClient();
        }

        return mSpotifyinstance;
    }

    public void destroyInstance() {
        mSpotifyinstance = null;
    }

    public String getSpotifyAccessToken() {
        return mAccessToken;
    }

    public void setSpotifyAccessToken(String accessToken) {
        mAccessToken = accessToken;
    }
}
