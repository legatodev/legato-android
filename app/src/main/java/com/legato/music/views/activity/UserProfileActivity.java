package com.legato.music.views.activity;

import android.os.Bundle;

import com.legato.music.R;

import androidx.lifecycle.ViewModelProviders;
import com.legato.music.viewmodels.UserProfileViewModel;
import com.legato.music.views.fragments.UserProfileFragment;

import co.chatsdk.core.dao.Keys;
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
        if (userProfileViewModel != null) {
            if (userEntityID != null && !userEntityID.isEmpty()) {
                if (userProfileViewModel.getUserByEntityId(userEntityID) != null) {
                    return;
                }
            }

            //TODO: is this the right way to handle it?
            ToastHelper.show(this, R.string.user_entity_id_not_set);
            finish();
        }
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
