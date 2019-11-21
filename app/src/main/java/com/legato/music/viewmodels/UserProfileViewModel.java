package com.legato.music.viewmodels;

import androidx.lifecycle.ViewModel;

import com.legato.music.models.UserProfileInfo;
import com.legato.music.models.UserProfile;

import java.util.ArrayList;
import java.util.List;

import co.chatsdk.core.dao.User;

public class UserProfileViewModel extends ViewModel {

    private UserProfile userProfile;

    public UserProfileViewModel() {
        userProfile = new UserProfile();
    }

    public ArrayList<UserProfileInfo> getProfileInfo() {
        return userProfile.getProfileInfo();
    }
    public void setUserProfileInfo(ArrayList<UserProfileInfo> newInfo) {
        userProfile.setUserProfileInfo(newInfo);
    }

    public void setSdkChat(boolean state) {
        userProfile.setStartingSdkChat(state);
    }
    public boolean getStartingSdkChat() {
        return userProfile.getStartingSdkChat();
    }

    public User getUser() {
        return userProfile.getUser();
    }
    public void setUser(User user) {
        userProfile.setUser(user);
    }

    public void fetchChatSdkUser(String userId) {
        userProfile.fetchChatSdkUser(userId);
    }

    public String getDistance() {
        return userProfile.getDistance();
    }
    public void setDistance(String distance) {
        userProfile.setDistance(distance);
    }

    public List<String> getYoutubeVideoIds() {
        return userProfile.getYoutubeVideoIds();
    }
    public void setYoutubeVideoIds(String videoIds) {
        userProfile.setYoutubeVideoIds(videoIds);
    }
}
