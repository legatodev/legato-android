package com.legato.music.repositories;

import android.location.Location;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.legato.music.models.NearbyUser;

import co.chatsdk.core.dao.User;

public class BaseRepository {

    @Nullable private static BaseRepository instance;
    private GeofireClient mGeofireClient;
    private FirebaseClient mFirebaseClient;
    private ChatSDKClient mChatSDKClient;


    public static BaseRepository getInstance(){
        if(instance == null){
            instance = new BaseRepository();
        }
        return instance;
    }
    private BaseRepository(){
       mGeofireClient = GeofireClient.getInstance();
       mFirebaseClient = FirebaseClient.getInstance();
       mChatSDKClient = ChatSDKClient.getInstance();


       mGeofireClient.setUserId(mChatSDKClient.getCurrentUserId());
    }

    public LiveData<NearbyUser> getNearbyUser(){
        return mGeofireClient.getNearbyUser();
    }

    public void searchNearbyUserByRadius(double searchRadius){
        mGeofireClient.searchNearbyUserByRadius(searchRadius);
    }

    public void setLocation(Location location){
        mGeofireClient.setLocation(location);
    }

    public Boolean isEmailVerified(){
        return mFirebaseClient.isEmailVerified();
    }

    public void sendEmailVerification(){
        mFirebaseClient.sendEmailVerification();
    }

    public Boolean isProximityAlertEnabled(){
        return mChatSDKClient.isProximityAlertEnabled();
    }

    public String getSearchRadius(){
        return mChatSDKClient.getMetaString(com.legato.music.utils.Keys.searchradius);
    }
}
