package com.legato.music.repositories;

import android.content.Context;

import androidx.annotation.Nullable;

import com.legato.music.LegatoInterfaceAdapter;

import co.chatsdk.core.dao.DaoCore;
import co.chatsdk.core.dao.DaoMaster;
import co.chatsdk.core.dao.DaoSession;
import co.chatsdk.core.dao.User;
import co.chatsdk.core.session.ChatSDK;
import co.chatsdk.core.types.ConnectionType;
import co.chatsdk.firebase.FirebaseNetworkAdapter;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

class ChatSDKClient {
    private static final String TAG = "ChatSDKClient";

    @Nullable private static ChatSDKClient instance;
    private static boolean mLoggedIn = false;

    @Nullable private User mCurrentUser;
    @Nullable private String mCurrentUserId;

    private ChatSDKClient(){
        mCurrentUser = ChatSDK.currentUser();
        mCurrentUserId = ChatSDK.currentUserID();
    }

    public static ChatSDKClient getInstance() {
        if (instance == null) {
            instance = new ChatSDKClient();
        }
        return instance;
    }

    public void login() {
        if (!mLoggedIn) {
            Disposable d = ChatSDK.auth().authenticate()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> {
                                // Success
                                mCurrentUser = ChatSDK.currentUser();
                                mCurrentUserId = ChatSDK.currentUserID();
                                mLoggedIn = true;
                            },
                            ChatSDK::logError
                    );
        }
    }

    public void logout() {
        if (mLoggedIn) {
            Disposable d = ChatSDK.auth().logout()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> {
                                // success
                                // TODO: Clearing the ChatSDK session somehow
                                mCurrentUser = null;
                                mCurrentUserId = "";
                                mLoggedIn = false;
                            },
                            ChatSDK::logError
                    );
        }
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

    public void navToLogin(Context context) {
        if (ChatSDK.auth().isAuthenticated()) {
            logout();
        }
        ChatSDK.ui().startSplashScreenActivity(context);
    }
}
