package com.legato.music.viewmodels;

import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.legato.music.models.NearbyUser;
import com.legato.music.repositories.BaseRepository;

import java.util.List;

import co.chatsdk.core.dao.User;

public class SoloArtistViewModel extends ViewModel {
    private BaseRepository baseRepository;
    private NearbyUser nearbyUser;
    private String previousAvatarURL = "";
    private String avatarUrl = "";
    private FirebaseUser firebaseUser;

    public SoloArtistViewModel() {
        this.baseRepository = BaseRepository.getInstance();
        this.nearbyUser = baseRepository.getCurrentUser();
        this.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public User getUser() {
        return nearbyUser.getUser();
    }

    public String getPreviousAvatarURL() {
        return previousAvatarURL;
    }

    public void setPreviousAvatarURL(String previousAvatarURL) {
        this.previousAvatarURL = previousAvatarURL;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        getUser().setAvatarURL(avatarUrl);
    }

    public boolean addYoutubeVideoId(String newVideoId) {
        return nearbyUser.addYoutubeVideoId(newVideoId);
    }

    public List<String> getYoutubeVideoIds() {
        return nearbyUser.getYoutubeVideoIds();
    }

    public void resetYoutubeVideoIds() {
        nearbyUser.resetYoutubeVideoIds();
    }

    public String getYoutubeVideoIdsAsString() {
        return nearbyUser.getYoutubeVideoIdsAsString();
    }

    public String getLookingFor() {
        return nearbyUser.getLookingfor();
    }

    public String getInstagram() {
        return nearbyUser.getInstagram();
    }

    public String getFacebook() {
        return nearbyUser.getFacebook();
    }

    public String getYoutubeChannel() {
        return nearbyUser.getYoutubeChannel();
    }

    public String getPhotoUrl() {
        String photoUrl = "https://graph.facebook.com/" + getFacebookUserId() + "/picture?height=500";
        setAvatarUrl(photoUrl);

        return photoUrl;
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

    public String getSpotifyTrack() { return nearbyUser.getSpotifyTrack(); }
}
