package com.example.spoluri.legato;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.chatsdk.core.dao.Keys;
import co.chatsdk.ui.utils.AvailabilityHelper;

public class UserProfileInfoHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.headlineTextView)
    TextView headlineTextView;

    @BindView(R.id.contentTextView)
    TextView contentTextView;

    private UserProfileInfo userProfileInfo;
    private final Context context;

    public UserProfileInfoHolder(Context context, View itemView) {
        super(itemView);
        // 1. Set the context
        this.context = context;

        // 2. Set up the UI widgets of the holder
        ButterKnife.bind(this, itemView);
    }

    public void bindUserProfileInfo(UserProfileInfo userProfileInfo) {

        // 4. Bind the data to the ViewHolder
        this.userProfileInfo = userProfileInfo;

        if (userProfileInfo != null) {
            this.headlineTextView.setText(userProfileInfo.getTitle());
            this.contentTextView.setText(userProfileInfo.getData());
        }
    }
}
