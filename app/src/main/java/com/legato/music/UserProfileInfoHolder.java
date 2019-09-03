package com.legato.music;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.legato.music.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserProfileInfoHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.headlineTextView)
    @Nullable TextView headlineTextView;

    @BindView(R.id.contentTextView)
    @Nullable TextView contentTextView;

    public UserProfileInfoHolder(Context context, View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bindUserProfileInfo(UserProfileInfo userProfileInfo) {
        if (userProfileInfo != null) {
            if (headlineTextView != null)
                this.headlineTextView.setText(userProfileInfo.getTitle());
            if (contentTextView != null)
                this.contentTextView.setText(userProfileInfo.getData());
        }
    }
}
