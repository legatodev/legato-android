package com.legato.music;

import android.os.Bundle;

import com.legato.music.repositories.GeofireClient;
import androidx.lifecycle.ViewModelProviders;
import com.legato.music.viewmodels.UserProfileViewModel;

import co.chatsdk.core.dao.Keys;
import co.chatsdk.core.dao.User;
import co.chatsdk.core.session.ChatSDK;
import co.chatsdk.ui.main.BaseActivity;
import co.chatsdk.ui.utils.ToastHelper;
import io.reactivex.annotations.Nullable;

public class UserProfileActivity extends BaseActivity {

    @Nullable
    UserProfileViewModel userProfileViewModel;

    @Nullable
    UserProfileFragment userProfileFragment;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        userProfileViewModel = ViewModelProviders.of(this).get(UserProfileViewModel.class);

        userProfileFragment = (UserProfileFragment) getSupportFragmentManager()
                .findFragmentById(R.id.user_profile_fragment);
    }

    @Override
    protected void onResume() {
        super.onResume();

        String userEntityID = getIntent().getStringExtra(Keys.USER_ENTITY_ID);
        if (userEntityID != null && !userEntityID.isEmpty()) {
            /*user =  ChatSDK.db().fetchUserWithEntityID(userEntityID);
            distance = GeofireClient.getInstance().getDistanceToCurrentUser(userEntityID);
            if (user != null) {
                UserProfileFragment fragment = (UserProfileFragment) getSupportFragmentManager().findFragmentById(R.id.user_profile_fragment);
                fragment.setUser(user);
                fragment.setDistance(distance);
                fragment.updateInterface();*/
            User user =  ChatSDK.db().fetchUserWithEntityID(userEntityID);
            String distance = GeofireClient.getInstance().getDistanceToCurrentUser(userEntityID);
            if (user != null) {
                if (userProfileViewModel != null) {
                    userProfileViewModel.setUser(user);
                    userProfileViewModel.setDistance(distance);
                }
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

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
