package com.legato.music.repositories;

import androidx.annotation.Nullable;

import co.chatsdk.core.dao.User;
import co.chatsdk.core.session.ChatSDK;

public class ChatSDKClient {

    @Nullable private static ChatSDKClient instance;

    @Nullable private User mCurrentUser;
    @Nullable private String mCurrentUserId;

    public static ChatSDKClient getInstance(){
        if(instance == null){
            instance = new ChatSDKClient();
        }
        return instance;
    }

    private void ChatSDK(){
        mCurrentUser = ChatSDK.currentUser();
        mCurrentUserId = ChatSDK.currentUserID();
    }

    public String getCurrentUserId(){
        if(mCurrentUserId != null) {
            return mCurrentUserId;
        }else{
            return ""; //This dirty implementation is because of NullAway restriction
        }
    }

    public void setAttribute(String key, String value){
        if(mCurrentUser != null) {
            mCurrentUser.setMetaString(key, value);
        }else{
            ChatSDK.currentUser().setMetaString(key, value);
        }
    }

}
