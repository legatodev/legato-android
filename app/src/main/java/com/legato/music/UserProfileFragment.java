package com.legato.music;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.legato.music.registration.solo.SoloRegistrationActivity;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.chatsdk.core.dao.Keys;
import co.chatsdk.core.dao.User;
import co.chatsdk.core.events.EventType;
import co.chatsdk.core.events.NetworkEvent;
import co.chatsdk.core.session.ChatSDK;
import co.chatsdk.core.utils.DisposableList;
import co.chatsdk.ui.main.BaseFragment;
import co.chatsdk.ui.utils.AvailabilityHelper;
import co.chatsdk.ui.utils.ToastHelper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.Nullable;

public class UserProfileFragment extends BaseFragment {
    private static final String TAG = "UserProfileFragment";

    @BindView(R.id.featuredArtistImageView)
    @Nullable protected ImageView featuredArtistImageView;
    @BindView(R.id.featuredBandImageView)
    @Nullable protected ImageView featuredBandImageView;
    @BindView(R.id.profilePhotoImageView)
    @Nullable protected SimpleDraweeView profilePhotoImageView;
    @BindView(R.id.profileUserAvailabilityImageView)
    @Nullable protected ImageView profileUserAvailabilityImageView;
    @BindView(R.id.featuredArtistNameTextView)
    @Nullable protected TextView featuredArtistNameTextView;
    @BindView(R.id.featuredBandNameTextView)
    @Nullable protected TextView featuredBandNameTextView;
    @BindView(R.id.connectOrRemoveButton)
    @Nullable protected Button connectOrRemoveButton;
    @BindView(R.id.editProfileButton)
    @Nullable protected Button editProfileButton;
    @BindView(R.id.profileInfoRecyclerView)
    @Nullable protected RecyclerView userInfoRecyclerView;
    @BindView(R.id.profileUserNameTextView)
    @Nullable protected TextView profileUserNameTextView;
    @BindView(R.id.emailTextView)
    @Nullable protected TextView emailTextView;
    @BindView(R.id.logoutButton)
    @Nullable protected Button logoutButton;

    private UserProfileInfoAdapter userProfileInfoAdapter;
    private String mYoutubeVideo = "";
    private DisposableList disposableList = new DisposableList();
    private boolean startingChat = false;
    private ArrayList<UserProfileInfo> profileInfo;
    @Nullable private User user;
    @Nullable private String distance;

    private YouTubePlayerView youTubePlayerView;

    public UserProfileFragment() {
        profileInfo = new ArrayList<>();
        user = null;
        distance = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mainView = inflater.inflate(R.layout.fragment_user_profile, container, false);
        ButterKnife.bind(this, mainView);

        if (savedInstanceState != null && savedInstanceState.getString(Keys.UserId) != null) {
            user = ChatSDK.db().fetchUserWithEntityID(savedInstanceState.getString(Keys.UserId));

            disposableList
                .add(ChatSDK.events().sourceOnMain().filter(
                    NetworkEvent.filterType(
                        EventType.UserMetaUpdated,
                        EventType.UserPresenceUpdated))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(networkEvent -> {
                        if (networkEvent.user.equals(getUser())) {
                            reloadData();
                        }
                    }));
        }

        userProfileInfoAdapter = new UserProfileInfoAdapter(getContext(), R.layout.item_userprofileinfo);
        if (userInfoRecyclerView != null) {
            userInfoRecyclerView.setAdapter(userProfileInfoAdapter);

            DividerItemDecoration itemDecor = new DividerItemDecoration(userInfoRecyclerView.getContext(), DividerItemDecoration.HORIZONTAL);
            userInfoRecyclerView.addItemDecoration(itemDecor);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            userInfoRecyclerView.setLayoutManager(layoutManager);
        }

        youTubePlayerView = mainView.findViewById(R.id.youtube_player_view);
        getLifecycle().addObserver(youTubePlayerView);
        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NotNull YouTubePlayer youTubePlayer) {
                youTubePlayer.cueVideo(mYoutubeVideo, 0);
            }
        });

        return mainView;
    }

    public void initializeYoutubeFragment() {
        youTubePlayerView.setVisibility(View.GONE);

        if (mYoutubeVideo != null && !mYoutubeVideo.isEmpty()) {
            youTubePlayerView.setVisibility(View.VISIBLE);
        }
    }

    protected User getUser () {
        return user != null ? user : ChatSDK.currentUser();
    }

    protected void setViewVisibility(View view, int visibility) {
        if (view != null) view.setVisibility(visibility);
    }

    protected void setViewVisibility(View view, boolean visible) {
        setViewVisibility(view, visible ? View.VISIBLE : View.INVISIBLE);
    }

    protected void setViewText(@Nullable TextView textView, String text) {
        if (textView != null) textView.setText(text);
    }

    @OnClick(R.id.editProfileButton)
    public void onEditProfileClicked() {
        Intent intent = new Intent(getContext(), SoloRegistrationActivity.class);
        startActivity(intent);
    }

    public void updateInterface() {
        User user = getUser();

        if (user == null) {
            return;
        }

        boolean isCurrentUser = user.isMe();
        String distance = isCurrentUser ? "0" : this.distance;

        if (distance == null)
            return;

        if(!isCurrentUser && editProfileButton != null)
            editProfileButton.setVisibility(View.GONE);

        setHasOptionsMenu(isCurrentUser);
        if (logoutButton != null)
            logoutButton.setVisibility(isCurrentUser?View.VISIBLE:View.INVISIBLE);
        if (connectOrRemoveButton != null) {
            connectOrRemoveButton.setVisibility(isCurrentUser ? View.INVISIBLE : View.VISIBLE);

            connectOrRemoveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startChat();
                }
            });
        }

        NearbyUser nearbyUser = new NearbyUser(user, distance);

        setViewText(profileUserNameTextView, nearbyUser.getUsername());
        setViewText(emailTextView, nearbyUser.getEmail());
        if (profilePhotoImageView != null && nearbyUser.getPhotoUrl() != null) {
            profilePhotoImageView.setImageURI(nearbyUser.getPhotoUrl());
        }

        String availability = nearbyUser.getAvailability();
        if (profileUserAvailabilityImageView != null) {
            if (availability != null && !isCurrentUser && profileUserAvailabilityImageView != null) {
                profileUserAvailabilityImageView.setImageResource(AvailabilityHelper.imageResourceIdForAvailability(availability));
                setViewVisibility(profileUserAvailabilityImageView, true);
            } else {
                setViewVisibility(profileUserAvailabilityImageView, false);
            }
        }

        mYoutubeVideo = nearbyUser.getYoutube();

        profileInfo.clear();
        profileInfo.add(new UserProfileInfo("Skills", nearbyUser.getSkills()));
        profileInfo.add(new UserProfileInfo("Genres", nearbyUser.getGenres()));
        userProfileInfoAdapter.notifyData(profileInfo);
    }

    @OnClick(R.id.logoutButton)
    public void onLogout(View view) {
        disposableList.add(ChatSDK.auth().logout()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> ChatSDK.ui().startSplashScreenActivity(getActivity().getApplicationContext()), throwable -> ChatSDK.logError(throwable))
        );
    }

    @Override
    public void clearData() {

    }

    @Override
    public void reloadData() {
        updateInterface();
        initializeYoutubeFragment();
    }

    public void setUser (User user) {
        this.user = user;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void startChat () {
        if (startingChat) {
            return;
        }

        startingChat = true;
        disposableList.add(ChatSDK.thread().createThread("", user, ChatSDK.currentUser())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {
                    dismissProgressDialog();
                    startingChat = false;
                })
                .subscribe(thread -> {
                    ChatSDK.ui().startChatActivityForID(getContext(), thread.getEntityID());
                }, throwable -> ToastHelper.show(getContext(), throwable.getLocalizedMessage())));
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposableList.dispose();
        youTubePlayerView.release();
    }
}

