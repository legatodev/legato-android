package com.example.spoluri.legato;

import android.os.Bundle;

import androidx.annotation.StringRes;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.chatsdk.core.dao.Keys;
import co.chatsdk.core.dao.User;
import co.chatsdk.core.session.ChatSDK;
import co.chatsdk.core.utils.DisposableList;
import co.chatsdk.ui.main.BaseFragment;
import co.chatsdk.ui.utils.AvailabilityHelper;
import co.chatsdk.ui.utils.ToastHelper;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class UserProfileFragment extends BaseFragment implements YouTubePlayer.OnInitializedListener {
    private static final String TAG = "UserProfileFragment";

    @BindView(R.id.featuredArtistImageView)
    protected ImageView featuredArtistImageView;
    @BindView(R.id.featuredBandImageView)
    protected ImageView featuredBandImageView;
    @BindView(R.id.profilePhotoImageView)
    protected SimpleDraweeView profilePhotoImageView;
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
    @BindView(R.id.profileUserNameTextView)
    protected TextView profileUserNameTextView;
    @BindView(R.id.emailTextView)
    protected TextView emailTextView;
    //@BindView(R.id.youtubeView1)
    //protected YouTubePlayerView youtubeView;

    private String mYoutubeVideo = "";
    private DisposableList disposableList = new DisposableList();

    protected User user;

    public static UserProfileFragment newInstance() {
        return UserProfileFragment.newInstance(null);
    }

    public static UserProfileFragment newInstance(User user) {
        UserProfileFragment f = new UserProfileFragment();

        Bundle b = new Bundle();

        if (user != null) {
            b.putString(Keys.UserId, user.getEntityID());
        }

        f.setArguments(b);
        f.setRetainInstance(true);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.getString(Keys.UserId) != null) {
            user = ChatSDK.db().fetchUserWithEntityID(savedInstanceState.getString(Keys.UserId));
        }

//        disposableList.add(ChatSDK.events().sourceOnMain().filter(NetworkEvent.filterType(EventType.UserMetaUpdated, EventType.UserPresenceUpdated))
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(networkEvent -> {
//                    if (networkEvent.user.equals(getUser())) {
//                        reloadData();
//                    }
//                }));

        mainView = inflater.inflate(R.layout.fragment_user_profile, container, false);
        ButterKnife.bind(this, mainView);
        //InitializeYoutubeView();

        return mainView;
    }

    protected User getUser () {
        return user != null ? user : ChatSDK.currentUser();
    }

    protected void block() {
        if (getUser().isMe()) return;

        disposableList.add(ChatSDK.blocking().blockUser(getUser().getEntityID())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    updateBlockedButton(true);
                    updateInterface();
                    ToastHelper.show(getContext(), getString(R.string.user_blocked));
                }, throwable1 -> {
                    ChatSDK.logError(throwable1);
                    Toast.makeText(UserProfileFragment.this.getContext(), throwable1.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }));
    }

    protected void unblock() {
        if (getUser().isMe()) return;

        disposableList.add(ChatSDK.blocking().unblockUser(getUser().getEntityID())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    updateBlockedButton(false);
                    updateInterface();
                    ToastHelper.show(getContext(), R.string.user_unblocked);
                }, throwable12 -> {
                    ChatSDK.logError(throwable12);
                    Toast.makeText(UserProfileFragment.this.getContext(), throwable12.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }));
    }

    protected void toggleBlocked() {
        if (getUser().isMe()) return;

        boolean blocked = ChatSDK.blocking().isBlocked(getUser().getEntityID());
        if (blocked) unblock();
        else block();
    }

    protected void setViewVisibility(View view, int visibility) {
        if (view != null) view.setVisibility(visibility);
    }

    protected void setViewVisibility(View view, boolean visible) {
        setViewVisibility(view, visible ? View.VISIBLE : View.INVISIBLE);
    }

    protected void setViewText(TextView textView, String text) {
        if (textView != null) textView.setText(text);
    }

    protected void updateBlockedButton(boolean blocked) {
        if (blocked) {
            setViewText(blockOrUnblockButton, getString(R.string.unblock));
        } else {
            setViewText(blockOrUnblockButton, getString(R.string.block));
        }
    }

    public void updateInterface() {
        User user = getUser();

        if (user == null) return;
        //this.user = user;

        boolean isCurrentUser = user.isMe();
        setHasOptionsMenu(isCurrentUser);

        boolean visible = !isCurrentUser;

        if (!isCurrentUser) {
            // Find out if the user is blocked already?
            if (ChatSDK.blocking() != null && ChatSDK.blocking().blockingSupported()) {
                updateBlockedButton(ChatSDK.blocking().isBlocked(getUser().getEntityID()));
                if (blockOrUnblockButton != null) blockOrUnblockButton.setOnClickListener(v -> toggleBlocked());
            }
            else {
                // TODO: Set height to zero
                setViewVisibility(blockOrUnblockButton, false);
            }
        }

        // Name
        setViewText(profileUserNameTextView, getUser().getName());

        // Email
        setViewText(emailTextView, getUser().getEmail());

        //profile pic
        if (profilePhotoImageView != null) {
            profilePhotoImageView.setImageURI(getUser().getAvatarURL());
        }

        String availability = getUser().getAvailability();

        // Availability
        if (availability != null && !isCurrentUser && profileUserAvailabilityImageView != null) {
            profileUserAvailabilityImageView.setImageResource(AvailabilityHelper.imageResourceIdForAvailability(availability));
            setViewVisibility(profileUserAvailabilityImageView, true);
        } else {
            setViewVisibility(profileUserAvailabilityImageView, false);
        }

        mYoutubeVideo = getUser().metaStringForKey(com.example.spoluri.legato.Keys.youtube);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
        Log.e(TAG, "Youtube Initialization Failed: " + errorReason.toString());
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
        player.cueVideo(mYoutubeVideo);
    }

    private void InitializeYoutubeView() {
        //youtubeView.initialize(AppConstants.YOUTUBE_API_KEY, this);
    }

    @Override
    public void clearData() {

    }

    @Override
    public void reloadData() {
        updateInterface();
    }

    public void setUser (User user) {
        this.user = user;
    }

}

