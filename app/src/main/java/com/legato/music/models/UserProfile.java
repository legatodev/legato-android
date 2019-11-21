package com.legato.music.models;

import androidx.annotation.NonNull;

import com.legato.music.utils.Keys;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import co.chatsdk.core.dao.User;
import co.chatsdk.core.session.ChatSDK;

public class UserProfile {
    private boolean startingChat = false;
    @NonNull
    ArrayList<UserProfileInfo> profileInfo;
    @NonNull private User chatSdkUser;
    private String distance;
    @NonNull private ArrayList<String> youtubeVideoIds;

    public UserProfile() {
        profileInfo = new ArrayList<UserProfileInfo>();
        chatSdkUser = new User();
        distance = "";
        youtubeVideoIds = new ArrayList<>();
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

    public boolean addYoutubeVideoId(String newVideoId) {
        boolean bSuccess = false;

        if (youtubeVideoIds.size() < 6) {
            if (!youtubeVideoIds.contains(newVideoId)) {
                youtubeVideoIds.add(newVideoId);
            }
            bSuccess = true;
        }

        return bSuccess;
    }

    public boolean removeYoutubeVideoId(int position) {
        boolean bSuccess = false;

        if (!youtubeVideoIds.isEmpty()) {
            youtubeVideoIds.remove(position);

            bSuccess = true;
        }

        return bSuccess;
    }

    public List<String> getYoutubeVideoIds() {
        if (youtubeVideoIds.isEmpty()) {
            setYoutubeVideoIds(chatSdkUser.metaStringForKey(Keys.youtube));
        }

        if (!youtubeVideoIds.isEmpty() && !youtubeVideoIds.get(0).isEmpty()) {
            return youtubeVideoIds;
        }

        youtubeVideoIds = new ArrayList<>();

        return youtubeVideoIds;
    }

    public String getYoutubeVideoIdsAsString() {
        return sanitizeVideoIdsString(getYoutubeVideoIds().toString());
    }

    public void setYoutubeVideoIds(String videoIds) {
        videoIds = sanitizeVideoIdsString(videoIds);

        if (videoIds.contains(",")) {
            youtubeVideoIds = new ArrayList<>(Arrays.asList(videoIds.split(",")));
        }
        else {
            youtubeVideoIds = new ArrayList<>(Arrays.asList(videoIds));
        }
    }

    public void resetYoutubeVideoIds() {
        chatSdkUser.setMetaString(Keys.youtube, "");
        youtubeVideoIds = new ArrayList<>();
    }

    private String sanitizeVideoIdsString(String videoIds) {
        /*
            This function is used to clean up strings within the ChatSdk that could have added
            space characters between elements, such as a List<>.toString() call,
            and brackets from json arrays that could be returned with comma separated values
            e.g., "[videoId1,videoId2,videoId3]"
         */
        return videoIds
                .replace(" ", "")
                .replace("[","")
                .replace("]","");
    }

}
