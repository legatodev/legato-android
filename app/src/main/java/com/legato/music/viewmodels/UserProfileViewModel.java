package com.legato.music.viewmodels;

import androidx.lifecycle.ViewModel;

import com.legato.music.UserProfileInfo;
import com.legato.music.models.UserProfile;

import java.util.ArrayList;

import co.chatsdk.core.dao.User;

public class UserProfileViewModel extends ViewModel {

    private UserProfile userProfile;

    public UserProfileViewModel() {
        userProfile = new UserProfile();
    }

    public void resetUserProfileInfo() {
        userProfile.resetUserProfileInfo();
    }

    public void updateUserProfileInfo(ArrayList<UserProfileInfo> newInfo) {
        userProfile.updateUserProfileInfo(newInfo);
    }

    public void updateYoutubeVideoIds(String commaSeparatedVideoIds) {
        userProfile.updateYoutubeVideoIds(commaSeparatedVideoIds);
    }

    public ArrayList<UserProfileInfo> getProfileInfo() {
        return userProfile.getProfileInfo();
    }

    public void setSdkChat(boolean state) {
        userProfile.setSdkChat(state);
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

    public String getYoutubeVideoIds() {
        return userProfile.getYoutubeVideoIds();
    }
}
