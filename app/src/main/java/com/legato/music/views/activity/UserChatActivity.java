package com.legato.music.views.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.legato.music.viewmodels.NearbyUsersViewModel;
import com.legato.music.viewmodels.UserProfileViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
        mUserProfileViewModel = ViewModelProviders.of(this).get(UserProfileViewModel.class);

        if (thread.typeIs(ThreadType.Private1to1)) {
            User currentUser = ChatSDK.currentUser();
            for (User user : thread.getUsers()){
            if (!user.getId().equals(currentUser.getId())) {
                actionBarView.setOnClickListener(v -> {
                    Context context = v.getContext();
                    mUserProfileViewModel.getUserByEntityId(user.getEntityID());
                    Intent intent = new Intent(context, UserProfileActivity.class);
                    intent.putExtra(Keys.USER_ENTITY_ID, user.getEntityID());
                    intent.putExtra(com.legato.music.utils.Keys.distance, mUserProfileViewModel.getDistance());
                    context.startActivity(intent);
                });
            }
        }
        }
    }
}
