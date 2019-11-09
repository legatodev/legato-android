package com.legato.music.models;

import androidx.annotation.NonNull;

import com.legato.music.NearbyUser;
import com.legato.music.UserProfileInfo;

import java.util.ArrayList;

import co.chatsdk.core.dao.User;
import co.chatsdk.core.session.ChatSDK;

public class UserProfile {
    private boolean startingChat = false;
    @NonNull
    ArrayList<UserProfileInfo> profileInfo;
    @NonNull private User user;
    private String distance;
    @NonNull private String youtubeVideoIds;

    public UserProfile() {
        profileInfo = new ArrayList<UserProfileInfo>();
        user = new User();
        distance = "";
        youtubeVideoIds = "";
    }

    public void resetUserProfileInfo() {
        profileInfo = new ArrayList<UserProfileInfo>();
    }

    public void updateUserProfileInfo(ArrayList<UserProfileInfo> newInfo) {
        profileInfo.clear();
        profileInfo = newInfo;
    }

    public void updateYoutubeVideoIds(String commaSeparatedVideoIds) {
        youtubeVideoIds = commaSeparatedVideoIds;
    }


    public ArrayList<UserProfileInfo> getProfileInfo() {
        return profileInfo;
    }


    public void setSdkChat(boolean state) {
        startingChat = state;
    }
    public boolean getStartingSdkChat() {
        return startingChat;
    }

    public User getUser() {
        return user;
    }
    public void fetchChatSdkUser(String userId) {
        user = ChatSDK.db().fetchUserWithEntityID(userId);
    }
    protected User getChatSdkUser() {
        return user != null ? user : ChatSDK.currentUser();
    }

    public String getDistance() {
        return distance;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getYoutubeVideoIds() {
        return youtubeVideoIds;
    }
}
