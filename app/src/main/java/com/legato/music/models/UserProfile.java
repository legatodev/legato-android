package com.legato.music.models;

import androidx.annotation.NonNull;

import com.legato.music.UserProfileInfo;

import java.util.ArrayList;

import co.chatsdk.core.dao.User;
import co.chatsdk.core.session.ChatSDK;

public class UserProfile {
    private boolean startingChat = false;
    @NonNull
    ArrayList<UserProfileInfo> profileInfo;
    @NonNull private User chatSdkUser;
    private String distance;
    @NonNull private String youtubeVideoIds;

    public UserProfile() {
        profileInfo = new ArrayList<UserProfileInfo>();
        chatSdkUser = new User();
        distance = "";
        youtubeVideoIds = "";
    }

    public void resetUserProfileInfo() {
        profileInfo = new ArrayList<UserProfileInfo>();
    }

    public void setUserProfileInfo(ArrayList<UserProfileInfo> newInfo) {
        profileInfo.clear();
        profileInfo = newInfo;
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
        return chatSdkUser;
    }

    public void fetchChatSdkUser(String userId) {
        chatSdkUser = ChatSDK.db().fetchUserWithEntityID(userId);
    }

    protected User getChatSdkUser() {
        return chatSdkUser != null ? chatSdkUser : ChatSDK.currentUser();
    }

    public String getDistance() {
        return distance;
    }

    public void setUser(User user) {
        this.chatSdkUser = user;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void setYoutubeVideoIds(String commaSeparatedVideoIds) {
        youtubeVideoIds = commaSeparatedVideoIds;
    }
    public String getYoutubeVideoIds() {
        return youtubeVideoIds;
    }
}
