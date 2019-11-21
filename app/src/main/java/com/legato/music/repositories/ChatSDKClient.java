package com.legato.music.repositories;

import android.util.Log;

import androidx.annotation.Nullable;

import org.apache.commons.lang3.ObjectUtils;

import co.chatsdk.core.dao.User;
import co.chatsdk.core.session.ChatSDK;

public class ChatSDKClient {
    private static final String TAG = "ChatSDKClient";

    @Nullable private static ChatSDKClient instance;

    @Nullable private User mCurrentUser;
    @Nullable private String mCurrentUserId;

    private ChatSDKClient(){
        mCurrentUser = ChatSDK.currentUser();
        mCurrentUserId = ChatSDK.currentUserID();
    }

    public static ChatSDKClient getInstance(){
        if(instance == null){
            instance = new ChatSDKClient();
        }
        return instance;
    }

    public String getCurrentUserId(){
        if(mCurrentUserId != null) {
            return mCurrentUserId;
        }else{
            throw new NullPointerException("Current User Dao not initialized. Check ChatSDKClient instance");
        }
    }

    public void setMetaString(String key, String value){
        if(mCurrentUser != null) {
            mCurrentUser.setMetaString(key, value);
        }else{
            throw new NullPointerException("Current User Dao not initialized. Check ChatSDKClient instance");
        }
    }

    public String getMetaString(String key){
        if(mCurrentUser != null) {
            return mCurrentUser.metaStringForKey(key);
        }else{
            throw new NullPointerException("Current User Dao not initialized. Check ChatSDKClient instance");
        }
    }

    public Boolean isProximityAlertEnabled() {
        if (mCurrentUser != null) {
            String proximityAlert = mCurrentUser.metaStringForKey(com.legato.music.utils.Keys.proximityalert);
            if (proximityAlert != null)
                return proximityAlert.equals("true");
            return false;
        } else {
            throw new NullPointerException("Current User Dao not initialized. Check ChatSDKClient instance");
        }
    }
}
