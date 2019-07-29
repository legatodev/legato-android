package com.example.spoluri.legato;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserProfileInfoHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.headlineTextView)
    TextView headlineTextView;

    @BindView(R.id.contentTextView)
    TextView contentTextView;

    public UserProfileInfoHolder(Context context, View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bindUserProfileInfo(UserProfileInfo userProfileInfo) {
        if (userProfileInfo != null) {
            this.headlineTextView.setText(userProfileInfo.getTitle());
            this.contentTextView.setText(userProfileInfo.getData());
        }
    }
}
