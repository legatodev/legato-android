package com.legato.music.viewmodels;

import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.legato.music.AppConstants;
import com.legato.music.models.NearbyUser;
import com.legato.music.repositories.BaseRepository;

import java.util.ArrayList;
import java.util.List;

import co.chatsdk.core.dao.User;

public class SoloArtistViewModel extends ViewModel {
    private BaseRepository baseRepository;
    private NearbyUser nearbyUser;
    private String previousAvatarURL = "";
    private FirebaseUser firebaseUser;

    public SoloArtistViewModel() {
        this.baseRepository = BaseRepository.getInstance();
        this.nearbyUser = baseRepository.getCurrentUser();
        this.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
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

    public String getAvatarUrl() {
        String avatarUrl = nearbyUser.getAvatarUrl();
        if (avatarUrl.isEmpty()) {
            String facebookUserId = getFacebookUserId();
            if (!facebookUserId.isEmpty())
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
        return !nearbyUser.getGenres().isEmpty();
    }

    public String getLookingFor() {
        return nearbyUser.getLookingfor();
    }

    public String getDescription() { return nearbyUser.getDescription(); }

    public void setDescription(String newDescription) { nearbyUser.setDescription(newDescription); }

    public String getInstagram() {
        return nearbyUser.getInstagram();
    }

    public String getFacebook() { return nearbyUser.getFacebook(); }

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
