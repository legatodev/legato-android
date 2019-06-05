package com.example.spoluri.legato;

import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import co.chatsdk.core.dao.Keys;
import co.chatsdk.core.dao.User;
import co.chatsdk.core.utils.DisposableList;
import co.chatsdk.ui.main.BaseFragment;

public class ProfileFragment extends BaseFragment {

    @BindView(R.id.featuredArtistImageView)
    protected ImageView featuredArtistImageView;
    @BindView(R.id.featuredBandImageView)
    protected ImageView featuredBandImageView;
    @BindView(R.id.profilePhotoImageView)
    protected ImageView profilePhotoImageView;
    @BindView(R.id.profileUserAvailabilityImageView)
    protected ImageView profileUserAvailabilityImageView;
    @BindView(R.id.featuredArtistNameTextView)
    protected TextView featuredArtistNameTextView;
    @BindView(R.id.featuredBandNameTextView)
    protected TextView featuredBandNameTextView;
    @BindView(R.id.blockOrUnblockButton)
    protected Button blockOrUnblockButton;
    @BindView(R.id.connectOrRemoveButton)
    protected Button connectOrRemoveButton;
    @BindView(R.id.profileInfoRecyclerView)
    protected RecyclerView userInfoRecyclerView;
    @BindView(R.id.galleryHorizontalScrollViewLayout)
    protected LinearLayout galleryHorizontalScrollViewLayout;

    private DisposableList disposableList = new DisposableList();

    protected User user;

    public static ProfileFragment newInstance() {
        return ProfileFragment.newInstance(null);
    }

    public static ProfileFragment newInstance(User user) {
        ProfileFragment f = new ProfileFragment();

        Bundle b = new Bundle();

        if (user != null) {
            b.putString(Keys.UserId, user.getEntityID());
        }

        f.setArguments(b);
        f.setRetainInstance(true);
        return f;
    }

    @Override
    public void clearData() {

    }

    @Override
    public void reloadData() {

    }
}

