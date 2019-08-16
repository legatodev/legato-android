package com.legato.music;

import android.os.Bundle;

import com.legato.music.R;

import co.chatsdk.core.dao.Keys;
import co.chatsdk.core.dao.User;
import co.chatsdk.core.session.ChatSDK;
import co.chatsdk.core.utils.DisposableList;
import co.chatsdk.ui.main.BaseActivity;
import co.chatsdk.ui.utils.ToastHelper;

public class UserProfileActivity extends BaseActivity {

    protected User user;
    private DisposableList disposableList = new DisposableList();

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        String userEntityID = getIntent().getStringExtra(Keys.USER_ENTITY_ID);

        if (userEntityID != null && !userEntityID.isEmpty()) {
            user =  ChatSDK.db().fetchUserWithEntityID(userEntityID);
            if (user != null) {
                UserProfileFragment fragment = (UserProfileFragment) getSupportFragmentManager().findFragmentById(R.id.user_profile_fragment);
                fragment.setUser(user);
                fragment.updateInterface();
                fragment.initializeYoutubeFragment();
                return;
            }
        }

        //TODO: is this the right way to handle it?
        ToastHelper.show(this, R.string.user_entity_id_not_set);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposableList.dispose();
    }
}