package com.legato.music.repositories;

import com.legato.music.R;
import com.legato.music.spotify.SearchPager;

import androidx.annotation.Nullable;

import co.chatsdk.core.dao.User;
import co.chatsdk.core.session.ChatSDK;
import co.chatsdk.core.types.ConnectionType;
import co.chatsdk.ui.utils.ToastHelper;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;

class ChatSDKClient {
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

    public User getCurrentUser() {
        if (mCurrentUser != null)
            return mCurrentUser;
        else
            return new User();
    }

    public String getCurrentUserId(){
        if(mCurrentUser != null) {
            return mCurrentUser.getEntityID();
        }else{
            throw new NullPointerException("Current User Dao not initialized. Check ChatSDKClient instance");
        }
    }

    public User getEntityById(String entityId) {
        User user = ChatSDK.db().fetchEntityWithEntityID(entityId, User.class);
        if (user != null)
            mCurrentUser = user;
        else
            mCurrentUser = new User();

        return mCurrentUser;
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

    public Completable addContact(User user) {
        return ChatSDK.contact().addContact(user, ConnectionType.Contact);
    }

    public Completable deleteContact(User user) {
        return ChatSDK.contact().deleteContact(user, ConnectionType.Contact);
    }

    public boolean isFriend(User user) {
        return ChatSDK.contact().exists(user);
    }
}
