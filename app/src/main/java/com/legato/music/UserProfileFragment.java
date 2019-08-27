package com.legato.music;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.legato.music.registration.solo.SoloRegistrationActivity;
import com.legato.music.R;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.squareup.picasso.Picasso;

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

public class UserProfileFragment extends BaseFragment {
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
    @BindView(R.id.connectOrRemoveButton)
    protected Button connectOrRemoveButton;
    @BindView(R.id.editProfileButton)
    protected Button editProfileButton;
    @BindView(R.id.profileInfoRecyclerView)
    protected RecyclerView userInfoRecyclerView;
    @BindView(R.id.galleryHorizontalScrollViewLayout)
    protected LinearLayout galleryHorizontalScrollViewLayout;
    @BindView(R.id.profileUserNameTextView)
    protected TextView profileUserNameTextView;
    @BindView(R.id.emailTextView)
    protected TextView emailTextView;
    @BindView(R.id.logoutButton)
    protected Button logoutButton;

    private UserProfileInfoAdapter userProfileInfoAdapter;
    private String mYoutubeVideo = "";
    private DisposableList disposableList = new DisposableList();
    private boolean startingChat = false;
    private ArrayList<UserProfileInfo> profileInfo;
    private User user;
    private String distance;

    public UserProfileFragment() {
        profileInfo = new ArrayList<>();
    }

    public static UserProfileFragment newInstance() {
        return UserProfileFragment.newInstance(null);
    }

    public static UserProfileFragment newInstance(User user) {
        UserProfileFragment userProfileFragment = new UserProfileFragment();
        Bundle bundle = new Bundle();
        if (user != null) {
            bundle.putString(Keys.UserId, user.getEntityID());
        }

        userProfileFragment.setArguments(bundle);
        return userProfileFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mainView = inflater.inflate(R.layout.fragment_user_profile, container, false);
        ButterKnife.bind(this, mainView);

        if (savedInstanceState != null && savedInstanceState.getString(Keys.UserId) != null) {
            user = ChatSDK.db().fetchUserWithEntityID(savedInstanceState.getString(Keys.UserId));

            disposableList.add(ChatSDK.events().sourceOnMain().filter(NetworkEvent.filterType(EventType.UserMetaUpdated, EventType.UserPresenceUpdated))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(networkEvent -> {
                        if (networkEvent.user.equals(getUser())) {
                            reloadData();
                        }
                    }));
        }

        userProfileInfoAdapter = new UserProfileInfoAdapter(getContext(), R.layout.item_userprofileinfo);
        userInfoRecyclerView.setAdapter(userProfileInfoAdapter);

        DividerItemDecoration itemDecor = new DividerItemDecoration(userInfoRecyclerView.getContext(), DividerItemDecoration.HORIZONTAL);
        userInfoRecyclerView.addItemDecoration(itemDecor);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        userInfoRecyclerView.setLayoutManager(layoutManager);

        return mainView;
    }

    public void initializeYoutubeFragment() {
        if (mYoutubeVideo != null && !mYoutubeVideo.isEmpty()) {
            YouTubePlayerSupportFragment youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            transaction.replace(R.id.youtubeView, youTubePlayerFragment);
            transaction.addToBackStack(null);
            transaction.commit();

            youTubePlayerFragment.initialize(AppConstants.YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {

                @Override
                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
                    if (!wasRestored) {
                        player.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                        if (mYoutubeVideo != null)
                            player.cueVideo(mYoutubeVideo);
                        else {
                            Log.e(TAG, "Youtube video url not found:");
                        }
                    }
                }

                @Override
                public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult error) {
                    // YouTube error
                    String errorMessage = error.toString();
                    Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
                    Log.d("errorMessage:", errorMessage);
                }
            });
        }
        else {
            mainView.findViewById(R.id.youtubeView).setVisibility(View.INVISIBLE);
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

    protected void setViewText(TextView textView, String text) {
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
        if(!isCurrentUser)
            editProfileButton.setVisibility(View.GONE);

        setHasOptionsMenu(isCurrentUser);
        String distance = isCurrentUser ? "0" : this.distance;
        logoutButton.setVisibility(isCurrentUser?View.VISIBLE:View.INVISIBLE);
        connectOrRemoveButton.setVisibility(isCurrentUser?View.INVISIBLE:View.VISIBLE);

        NearbyUser nearbyUser = new NearbyUser(user, distance);

        setViewText(profileUserNameTextView, nearbyUser.getUsername());
        setViewText(emailTextView, nearbyUser.getEmail());
        if (profilePhotoImageView != null && nearbyUser.getPhotourl() != null) {
            profilePhotoImageView.setImageURI(nearbyUser.getPhotourl());
        }

        String availability = nearbyUser.getAvailability();
        if (availability != null && !isCurrentUser && profileUserAvailabilityImageView != null) {
            profileUserAvailabilityImageView.setImageResource(AvailabilityHelper.imageResourceIdForAvailability(availability));
            setViewVisibility(profileUserAvailabilityImageView, true);
        } else {
            setViewVisibility(profileUserAvailabilityImageView, false);
        }

        mYoutubeVideo = nearbyUser.getYoutube();
        connectOrRemoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startChat();
            }
        });

        profileInfo.clear();
        profileInfo.add(new UserProfileInfo("Skills", nearbyUser.getSkills()));
        profileInfo.add(new UserProfileInfo("Genres", nearbyUser.getGenres()));
        userProfileInfoAdapter.notifyData(profileInfo);
    }

    @OnClick(R.id.logoutButton)
    public void onLogout(View view) {
        disposableList.add(ChatSDK.auth().logout()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> ChatSDK.ui().startSplashScreenActivity(getActivity().getApplicationContext()), throwable -> {
                    ChatSDK.logError(throwable);
                })
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
                }, throwable -> {
                    ToastHelper.show(getContext(), throwable.getLocalizedMessage());
                }));
    }

    @Override
    public void onStop() {
        super.onStop();
        disposableList.dispose();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}

