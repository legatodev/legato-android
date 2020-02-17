package com.legato.music.viewmodels;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.legato.music.AppConstants;
import com.legato.music.models.NearbyUser;
import com.legato.music.repositories.BaseRepository;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import co.chatsdk.core.dao.User;

public class SoloArtistViewModel extends ViewModel {
    private static final String TAG = SoloArtistViewModel.class.getSimpleName();

    private BaseRepository baseRepository;
    private NearbyUser nearbyUser;
    private String previousAvatarURL = "";
    private FirebaseUser firebaseUser;

    MutableLiveData<Boolean> isQueryingApi = new MutableLiveData<>();

    public SoloArtistViewModel(BaseRepository baseRepository) {
        this.baseRepository = baseRepository;

        this.nearbyUser = baseRepository.getCurrentUser();
        this.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (TextUtils.isEmpty(nearbyUser.getFacebookPageId()) && !TextUtils.isEmpty(nearbyUser.getFacebook())) {
            queryFacebookPageId(nearbyUser.getFacebook());
        }
    }

    public NearbyUser getNearbyUser() { return nearbyUser; }

    public User getUser() {
        return nearbyUser.getUser();
    }

    public void setUser(User user) {
        nearbyUser.setUser(user);
    }

    public String getPreviousAvatarURL() {
        return previousAvatarURL;
    }

    public void setPreviousAvatarURL(String previousAvatarURL) {
        this.previousAvatarURL = previousAvatarURL;
    }

    public @Nullable String getAvatarUrl() {
        String avatarUrl = nearbyUser.getAvatarUrl();
        if (TextUtils.isEmpty(avatarUrl)) {
            String facebookUserId = getFacebookUserId();
            if (!TextUtils.isEmpty(facebookUserId))
                avatarUrl = "https://graph.facebook.com/" + facebookUserId + "/picture?height=500";
        }

        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        getUser().setAvatarURL(avatarUrl);
    }

    public List<String> getYoutubeVideoIds() {
        return new ArrayList<>(nearbyUser.getYoutubeSamples().getTrackIds());
    }

    public void resetYoutubeVideoIds() {
        nearbyUser.getYoutubeSamples().resetTrackIds();
    }

    public String getYoutubeVideoIdsAsString() {
        return nearbyUser.getYoutubeSamples().getTrackIdsAsString();
    }

    public boolean hasSelectedGenres() {
        return !TextUtils.isEmpty(nearbyUser.getGenres());
    }

    public String getLookingFor() {
        return nearbyUser.getLookingfor();
    }

    public String getDescription() { return nearbyUser.getDescription(); }

    public void setDescription(String newDescription) { nearbyUser.setDescription(newDescription); }

    public void setInstagram(String handle) {
        nearbyUser.setInstagram(handle);
    }

    public String getInstagram() {
        return nearbyUser.getInstagram();
    }

    public String getFacebook() { return nearbyUser.getFacebook(); }

    public String getFacebookPageId() {
        return nearbyUser.getFacebookPageId();
    }

    public LiveData<Boolean> getIsQueryingApi() {
        return isQueryingApi;
    }

    public void queryInstagramHandle(String instagramHandle) {
        //isQueryingApi.setValue(true);
        // nearbyUser.setInstagram("");

        // TODO: Need instagram API query logic here.
    }

    public void queryFacebookPageId(String facebookPageTitle) {
        isQueryingApi.setValue(true);
        nearbyUser.setFacebookPageId("");

        GraphRequest request = GraphRequest.newGraphPathRequest(
            AccessToken.getCurrentAccessToken(),
            "/me/accounts",
            response -> {
                // Try to get the async response and parse the data for the page title
                // in order to get the page id.
                try {
                    JSONArray pages = response
                            .getJSONObject()
                            .getJSONArray("data");

                    boolean pageFound = false;

                    for (int i = 0; i < pages.length(); i++) {
                        JSONObject pageDetails = pages.getJSONObject(i);

                        if (pageDetails.getString("name")
                                .equals(facebookPageTitle)) {

                            nearbyUser.setFacebookPageId(
                                    pageDetails.getString("id")
                            );
                            pageFound = true;
                        }
                    }

                    if (!pageFound) {
                        nearbyUser.setFacebookPageId("");
                    }
                } catch (Exception e) {
                    Log.d(TAG, "Unable to find facebook page.\n".concat(e.getMessage()));
                }

                isQueryingApi.setValue(false);
            });

        // Set up the parameters for the API query.
        Bundle parameters = new Bundle();
        parameters.putString("fields", "name,id");
        request.setParameters(parameters);
        request.executeAsync();
    }

    public void queryYoutubeChannel(String youtubeChannel) {
        //isQueryingApi.setValue(true);
        // nearbyUser.setYoutubeChannel("");

        // TODO: Need youtube API query logic here.
    }

    public void setYoutubeChannel(String youtubeChannelName) {
        nearbyUser.setYoutubeChannel(youtubeChannelName);
    }

    public String getYoutubeChannel() {
        return nearbyUser.getYoutubeChannel();
    }

    public String getFacebookUserId() {
        String facebookUserId = "";

        // find the Facebook profile and get the user's id
        for(UserInfo profile : firebaseUser.getProviderData()) {
            // check if the provider id matches "facebook.com"
            if(FacebookAuthProvider.PROVIDER_ID.equals(profile.getProviderId())) {
                facebookUserId = profile.getUid();
            }
        }

        return facebookUserId;
    }

    public boolean addTrackId(String trackId) {
        if (trackId.contains(AppConstants.SPOTIFY)) {
            return nearbyUser.getSpotifySamples().addTrackId(trackId);
        } else {
            return nearbyUser.getYoutubeSamples().addTrackId(trackId);
        }
    }

    public boolean removeTrackId(String trackId) {
        if (trackId.contains(AppConstants.SPOTIFY)) {
            return nearbyUser.getSpotifySamples().removeTrackId(trackId);
        } else {
            return nearbyUser.getYoutubeSamples().removeTrackId(trackId);
        }
    }

    public List<String> getSpotifyTrackIds() {
        return new ArrayList<>(nearbyUser.getSpotifySamples().getTrackIds());
    }

    public void resetSpotifyTrackIds() {
        nearbyUser.getSpotifySamples().resetTrackIds();
    }

    public String getSpotifyTrackIdsAsString() {
        return nearbyUser.getSpotifySamples().getTrackIdsAsString();
    }

    public String getSpotifyAccessToken() {
        return baseRepository.getSpotifyAccessToken();
    }

    public void setSpotifyAccessToken(String spotifyAccessToken) {
        baseRepository.setSpotifyAccessToken(spotifyAccessToken);
    }
}
