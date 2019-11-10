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

    public boolean getStartingSdkChat() {
        return startingChat;
    }
    public void setStartingSdkChat(boolean state) {
        startingChat = state;
    }

    protected User getChatSdkUser() {
        return chatSdkUser != null ? chatSdkUser : ChatSDK.currentUser();
    }
    public void fetchChatSdkUser(String userId) {
        chatSdkUser = ChatSDK.db().fetchUserWithEntityID(userId);
    }

    public User getUser() {
        return chatSdkUser;
    }
    public void setUser(User user) {
        this.chatSdkUser = user;
    }

    public String getDistance() {
        return chatSdkUser.isMe() ? "0" : this.distance;
    }
    public void setDistance(String distance) {
        this.distance = distance;
    }



    public String getYoutubeVideoIds() {
        return youtubeVideoIds;
    }
    public void setYoutubeVideoIds(String commaSeparatedVideoIds) {
        youtubeVideoIds = commaSeparatedVideoIds;
    }

}
