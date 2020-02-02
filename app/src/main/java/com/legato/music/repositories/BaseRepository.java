package com.legato.music.repositories;

import android.content.Context;
import android.location.Location;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseUser;
import com.legato.music.models.NearbyUser;

import java.util.List;

import co.chatsdk.core.dao.User;
import io.reactivex.Completable;

public class BaseRepository {

    @Nullable private static BaseRepository instance;
    private static boolean loggedIn = false;

    private GeofireClient mGeofireClient;
    private FirebaseClient mFirebaseClient;
    private ChatSDKClient mChatSDKClient;
    private SpotifyClient mSpotifyClient;

    public static BaseRepository getInstance(){
        if(instance == null){
            instance = new BaseRepository();
        }

        return instance;
    }

    private BaseRepository() {
       mGeofireClient = GeofireClient.getInstance();
       mFirebaseClient = FirebaseClient.getInstance();
       mChatSDKClient = ChatSDKClient.getInstance();
       mSpotifyClient = SpotifyClient.getInstance();

        mGeofireClient.setUserId(mChatSDKClient.getCurrentUserId());
        loggedIn = true;
    }

    public void login() {
        if (!loggedIn) {
            mChatSDKClient.login();
            mFirebaseClient.login();

            mGeofireClient.setUserId(mChatSDKClient.getCurrentUserId());

            loggedIn = true;
        }
    }

    public void logout() {
        if (loggedIn) {
            mChatSDKClient.logout();
            mFirebaseClient.logout();
            mGeofireClient.setUserId("");

            loggedIn = false;
        }
    }

    public LiveData<List<NearbyUser>> getNearbyUsers(){
        return mGeofireClient.getNearbyUsers();
    }

    public NearbyUser getCurrentUser() {
        User user = mChatSDKClient.getCurrentUser();

        NearbyUser currentUser = new NearbyUser(
                user,
                mGeofireClient.getDistanceToCurrentUser(user.getEntityID())
        );

        return currentUser;
    }

    public String getDistanceFromCurrentUser() {
        User user = mChatSDKClient.getCurrentUser();
        return mGeofireClient.getDistanceToCurrentUser(user.getEntityID());
    }

    public User getUserByEntityId(String entityId) {
        return mChatSDKClient.getEntityById(entityId); }

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

    public Completable addContact(User user) {
        return mChatSDKClient.addContact(user);
    }

    public Completable deleteContact(User user) {
        return mChatSDKClient.deleteContact(user);
    }

    public boolean isFriend(User user) { return mChatSDKClient.isFriend(user); }

    public String getSpotifyAccessToken() {
        return mSpotifyClient.getSpotifyAccessToken();
    }

    public void setSpotifyAccessToken(String accessToken) {
        mSpotifyClient.setSpotifyAccessToken(accessToken);
    }

    public void navToLogin(Context context) {
        mChatSDKClient.navToLogin(context);
    }

    public FirebaseUser getFirebaseAuthUser() {
        return mFirebaseClient.getFirebaseUser();
    }
}
