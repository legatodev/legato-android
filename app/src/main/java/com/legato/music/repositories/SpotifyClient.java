package com.legato.music.repositories;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/*
Any interaction with FireBase frame should be done by this class
*/

class SpotifyClient {

    @Nullable private  static SpotifyClient instance;
    private String mAccessToken = "";

    public static SpotifyClient getInstance(){
        if(instance == null){
            instance = new SpotifyClient();
        }

        return instance;
    }

    public String getSpotifyAccessToken() {
        return mAccessToken;
    }

    public void setSpotifyAccessToken(String accessToken) {
        mAccessToken = accessToken;
    }
}
