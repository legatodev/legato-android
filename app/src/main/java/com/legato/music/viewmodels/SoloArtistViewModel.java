package com.legato.music.viewmodels;

import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.legato.music.models.NearbyUser;
import com.legato.music.repositories.BaseRepository;

import java.util.HashMap;
import java.util.List;

public class SoloArtistViewModel extends ViewModel {
    private BaseRepository mBaseRepository;
    private NearbyUser mUser;
    private FirebaseUser firebaseUser;

    public SoloArtistViewModel() {
        this.mBaseRepository = BaseRepository.getInstance();
        this.mUser = mBaseRepository.getCurrentUser();
        /*
        TODO: No direct call to data source. Remove Firebase Call to mBaseRepository
         */
        this.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public String getAvatarUrl() {
        return mUser.getAvatarUrl();
    }

    public void setAvatarUrl(String avatarUrl) {
        mUser.setAvatarUrl(avatarUrl);
    }

    public boolean addYoutubeVideoId(String newVideoId) {
        return mUser.addYoutubeVideoId(newVideoId);
    }

    public List<String> getYoutubeVideoIds() {
        return mUser.getYoutubeVideoIds();
    }

    public void resetYoutubeVideoIds() {
        mUser.resetYoutubeVideoIds();
    }

    public String getUserName() {
        return mUser.getUsername();
    }

    public String getYoutubeVideoIdsAsString() {
        return mUser.getYoutubeVideoIdsAsString();
    }

    public String getLookingFor() {
        return mUser.getLookingfor();
    }

    public String getInstagram() {
        return mUser.getInstagram();
    }

    public String getFacebook() {
        return mUser.getFacebook();
    }

    public String getYoutubeChannel() {
        return mUser.getYoutubeChannel();
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

    public String getSpotifyTrack() {
        return mUser.getSpotifyTrack();
    }

    public void setMetaMap(HashMap<String, String> map){
        mBaseRepository.setMetaMap(map);
    }
}
