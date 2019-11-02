package com.legato.music;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.AccessToken;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.legato.music.registration.solo.SoloRegistrationActivity;
import com.legato.music.utils.LegatoAuthenticationHandler;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.chatsdk.core.dao.Keys;
import co.chatsdk.core.dao.Thread;
import co.chatsdk.core.dao.User;
import co.chatsdk.core.events.EventType;
import co.chatsdk.core.events.NetworkEvent;
import co.chatsdk.core.interfaces.ThreadType;
import co.chatsdk.core.session.ChatSDK;
import co.chatsdk.core.types.AccountDetails;
import co.chatsdk.core.utils.DisposableList;
import co.chatsdk.ui.main.BaseFragment;
import co.chatsdk.ui.utils.AvailabilityHelper;
import co.chatsdk.ui.utils.ToastHelper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
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
    @BindView(R.id.deleteAccountButton)
    @Nullable protected Button deleteAccountButton;

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

    @OnClick(R.id.deleteAccountButton)
    public void onDeleteAccountClicked(View view) {
        new AlertDialog.Builder(getContext())
                .setMessage(getContext().getResources().getString(R.string.alert_delete_account))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        List<Thread> allThreads = ChatSDK.thread().getThreads(ThreadType.Private1to1);
                        for (Thread thread : allThreads) {
                            ChatSDK.thread().deleteThread(thread);
                        }

                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        String currentUserId = ChatSDK.currentUserID();
                        deleteUser(firebaseUser, currentUserId);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
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
        if (logoutButton != null) {
            logoutButton.setVisibility(isCurrentUser ? View.VISIBLE : View.INVISIBLE);
        }

        if (deleteAccountButton != null) {
            deleteAccountButton.setVisibility(isCurrentUser ? View.VISIBLE : View.INVISIBLE);
        }

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

    private void deleteUserAuthentication(FirebaseUser firebaseUser) {
        reauthenticateUser(firebaseUser);
        firebaseUser.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            ChatSDK.ui().startSplashScreenActivity(getActivity().getApplicationContext());
                        } else {
                            Log.e(TAG, "User authentication deletion failed :");
                        }
                    }
                });
    }

    private void reauthenticateUser(FirebaseUser firebaseUser) {
        AuthCredential credential = null;

        if (FacebookAuthProvider.PROVIDER_ID.equals(firebaseUser.getProviderId())) {
            credential = getFbCredentials();
        } else if (GoogleAuthProvider.PROVIDER_ID.equals(firebaseUser.getProviderId()))  {
            credential = getGoogleCredentials();
        } else if (EmailAuthProvider.PROVIDER_ID.equals(firebaseUser.getProviderId())) {
            credential = getEmailCredentials();
        }

        if (credential != null) {
            firebaseUser.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d(TAG, "User re-authenticated.");
                        }
                    });
        }
    }

    @Nullable private AuthCredential getEmailCredentials(){
        LegatoAuthenticationHandler authHandler = (LegatoAuthenticationHandler) ChatSDK.auth();
        AccountDetails accountDetails = authHandler.getAccountDetails();
        if (accountDetails != null) {
            return EmailAuthProvider
                    .getCredential(accountDetails.username, accountDetails.password);
        }

        return null;
    }

    @Nullable private AuthCredential getGoogleCredentials() {
        LegatoAuthenticationHandler authHandler = (LegatoAuthenticationHandler) ChatSDK.auth();
        AccountDetails accountDetails = authHandler.getAccountDetails();
        if (accountDetails != null) {
            String token = accountDetails.token;
            return GoogleAuthProvider.getCredential(token, null);
        }

        return null;
    }

    public AuthCredential getFbCredentials() {
        AccessToken token = AccessToken.getCurrentAccessToken();
        return FacebookAuthProvider.getCredential(token.getToken());
    }

    private void deleteUser(FirebaseUser firebaseUser, String currentUserId) {
        LegatoAuthenticationHandler authHandler = (LegatoAuthenticationHandler) ChatSDK.auth();
        disposableList.add(authHandler.deleteUser()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                            deleteUserAuthentication(firebaseUser);
                            ChatSDK.ui().startSplashScreenActivity(getContext());
                        },
                        throwable -> {
                            ChatSDK.logError(throwable);
                        })
        );
    }

    @OnClick(R.id.logoutButton)
    public void onLogout(View view) {
        logout();
    }

    private void logout() {
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

