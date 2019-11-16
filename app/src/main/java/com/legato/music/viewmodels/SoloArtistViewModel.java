package com.legato.music.viewmodels;

import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.legato.music.models.UserProfile;
import com.legato.music.utils.Keys;

import java.util.List;

import co.chatsdk.core.dao.User;

public class SoloArtistViewModel extends ViewModel {
    private UserProfile userProfile;
    private String previousAvatarURL = "";
    private String avatarUrl = "";
    private FirebaseUser firebaseUser;

    public SoloArtistViewModel() {
        this.userProfile = new UserProfile();
        this.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public User getUser() {
        return userProfile.getUser();
    }

    public void setUser(User user) {
        userProfile.setUser(user);

        userProfile.setYoutubeVideoIds(
                user.metaStringForKey(Keys.youtube));
    }

    public String getPreviousAvatarURL() {
        return previousAvatarURL;
    }

    public void setPreviousAvatarURL(String previousAvatarURL) {
        this.previousAvatarURL = userProfile.getUser().getAvatarURL();
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public boolean addYoutubeVideoId(String newVideoId) {
        return userProfile.addYoutubeVideoId(newVideoId);
    }

    public List<String> getYoutubeVideoIds() {
        return userProfile.getYoutubeVideoIds();
    }

    public void resetYoutubeVideoIds() {
        userProfile.resetYoutubeVideoIds();
    }

    public String getYoutubeVideoIdsAsString() {
        return userProfile.getYoutubeVideoIdsAsString();
    }

    public String getLookingFor() {
        return userProfile.getUser().metaStringForKey(Keys.lookingfor);
    }

    public String getInstagram() {
        return userProfile.getUser().metaStringForKey(Keys.instagram);
    }

    public String getFacebook() {
        return userProfile.getUser().metaStringForKey(Keys.facebook);
    }

    public String getYoutubeChannel() {
        return userProfile.getUser().metaStringForKey(Keys.youtube_channel);
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
}
