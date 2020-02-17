package com.legato.music.views.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.legato.music.repositories.BaseRepository;
import com.legato.music.viewmodels.NearbyUsersViewModel;
import com.legato.music.viewmodels.UserProfileViewModel;
import com.legato.music.viewmodels.UserProfileViewModelFactory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import co.chatsdk.core.dao.Keys;
import co.chatsdk.core.dao.User;
import co.chatsdk.core.interfaces.ThreadType;
import co.chatsdk.core.session.ChatSDK;
import co.chatsdk.ui.chat.ChatActivity;

public class UserChatActivity extends ChatActivity {

    private UserProfileViewModel mUserProfileViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserProfileViewModel = getViewModel();

        if (thread.typeIs(ThreadType.Private1to1)) {
            User currentUser = ChatSDK.currentUser();
            for (User user : thread.getUsers()){
            if (!user.getId().equals(currentUser.getId())) {
                actionBarView.setOnClickListener(v -> {
                    Context context = v.getContext();
                    mUserProfileViewModel.getUserByEntityId(user.getEntityID());
                    Intent intent = new Intent(context, UserProfileActivity.class);
                    intent.putExtra(Keys.PushKeyUserEntityID, user.getEntityID());
                    intent.putExtra(com.legato.music.utils.Keys.distance, mUserProfileViewModel.getDistance());
                    context.startActivity(intent);
                });
            }
        }
        }
    }

    @NonNull
    private UserProfileViewModel getViewModel() {
        BaseRepository baseRepository = BaseRepository.getInstance();
        ViewModelProvider.Factory factory = new UserProfileViewModelFactory(baseRepository);
        return ViewModelProviders.of(this, factory).get(UserProfileViewModel.class);
    }
}
