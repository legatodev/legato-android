package com.legato.music.views.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.legato.music.R;
import com.legato.music.repositories.BaseRepository;
import com.legato.music.viewmodels.UserProfileViewModel;
import com.legato.music.viewmodels.UserProfileViewModelFactory;
import com.legato.music.views.fragments.UserProfileFragment;

import co.chatsdk.core.dao.Keys;
import co.chatsdk.ui.main.BaseActivity;
import co.chatsdk.ui.utils.ToastHelper;
import io.reactivex.annotations.Nullable;

public class UserProfileActivity extends BaseActivity {
    private static final String TAG = UserProfileActivity.class.getSimpleName();

    UserProfileViewModel userProfileViewModel;
    UserProfileFragment userProfileFragment;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        userProfileViewModel = getViewModel();

        userProfileFragment = (UserProfileFragment) getSupportFragmentManager()
                .findFragmentById(R.id.user_profile_fragment);
    }

    @Override
    protected void onResume() {
        super.onResume();

        @Nullable String userEntityID = getIntent().getStringExtra(Keys.PushKeyUserEntityID);
        if (userProfileViewModel != null) {
            if (!TextUtils.isEmpty(userEntityID)) {
                if (userProfileViewModel.getUserByEntityId(userEntityID) != null) {
                    @Nullable String distance = getIntent().getStringExtra(com.legato.music.utils.Keys.distance);
                    if (!TextUtils.isEmpty(distance)) {
                        userProfileViewModel.setDistance(distance);
                    } else {
                        Log.d(TAG, "Distance not available");
                    }
                    return;
                } else {
                    Log.e(TAG, "User entity does not exist in View Model");
                }
            } else {
                Log.e(TAG, "User entity id not sent to the activity.");
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        userProfileFragment.onActivityResult(requestCode, resultCode, data);
    }

    @NonNull
    private UserProfileViewModel getViewModel() {
        BaseRepository baseRepository = BaseRepository.getInstance();
        ViewModelProvider.Factory factory = new UserProfileViewModelFactory(baseRepository);
        return ViewModelProviders.of(this, factory).get(UserProfileViewModel.class);
    }
}
