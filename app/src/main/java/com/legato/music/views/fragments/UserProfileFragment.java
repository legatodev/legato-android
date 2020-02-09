package com.legato.music.views.fragments;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.facebook.AccessToken;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.youtube.player.YouTubeIntents;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.legato.music.AppConstants;
import com.legato.music.BuildConfig;
import com.legato.music.R;
import com.legato.music.models.NearbyUser;
import com.legato.music.repositories.BaseRepository;
import com.legato.music.spotify.Player;
import com.legato.music.spotify.PlayerService;
import com.legato.music.utils.LegatoAuthenticationHandler;
import com.legato.music.viewmodels.UserProfileViewModel;
import com.legato.music.viewmodels.UserProfileViewModelFactory;
import com.legato.music.views.activity.SoloRegistrationActivity;
import com.legato.music.views.adapters.MediaPlayerAdapter;
import com.legato.music.views.adapters.UserProfileInfoAdapter;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

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
import co.chatsdk.core.types.ConnectionType;
import co.chatsdk.core.utils.DisposableList;
import co.chatsdk.ui.main.BaseFragment;
import co.chatsdk.ui.utils.AvailabilityHelper;
import co.chatsdk.ui.utils.ToastHelper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;

public class UserProfileFragment extends BaseFragment {
    private static final String TAG = UserProfileFragment.class.getSimpleName();
    private static final int REQUEST_CODE = 1337;

    @BindView(R.id.profilePhotoImageView)
    protected SimpleDraweeView profilePhotoImageView;
    @BindView(R.id.profileUserAvailabilityImageView)
    protected ImageView profileUserAvailabilityImageView;
    @BindView(R.id.connectButton)
    protected Button connectButton;
    @BindView(R.id.editProfileButton)
    protected Button editProfileButton;
    @BindView(R.id.userDescription)
    protected TextView userDescriptionTextView;
    @BindView(R.id.profileInfoRecyclerView)
    protected RecyclerView userInfoRecyclerView;
    @BindView(R.id.profileUserNameTextView)
    protected TextView profileUserNameTextView;
    @BindView(R.id.logoutButton)
    protected Button logoutButton;
    @BindView(R.id.deleteAccountButton)
    protected Button deleteAccountButton;
    @BindView(R.id.mediaRecyclerView)
    protected RecyclerView mediaRecyclerView;
    @BindView(R.id.mediaGalleryLayout)
    protected View mediaGalleryView;
    @BindView(R.id.userInfoLayout)
    protected LinearLayout userInfoLayout;

    @BindView(R.id.addOrRemoveContactImageButton)
    protected Button addOrRemoveContactImageButton;

    @BindView(R.id.appVersionTextView)
    protected TextView appVersionTextView;

    @BindView(R.id.privacyPolicyButton)
    protected Button privacyPolicyButton;

    @BindView(R.id.userProfileInstagramView)
    ImageView userProfileInstagramView;

    @BindView(R.id.userProfileFacebookView)
    ImageView userProfileFacebookView;

    @BindView(R.id.userProfileYoutubeView)
    ImageView userProfileYoutubeView;

    @BindView(R.id.userProfileDistanceTextView)
    TextView userProfileDistanceTextView;

    @BindView(R.id.privacyPolicyCardView)
    CardView privacyPolicyCardView;

    private DisposableList disposableList = new DisposableList();

    private UserProfileInfoAdapter userProfileInfoAdapter;
    @Nullable private MediaPlayerAdapter mediaPlayerAdapter;

    @NonNull private UserProfileViewModel userProfileViewModel;

    @BindView(R.id.profileProgressBar)
    ProgressBar profileProgressBar = null;

    private List<String> trackIds = new ArrayList<>();

    @androidx.annotation.Nullable
    private Player mPlayerBoundService;
    private boolean mSpotifyPlayerBound = false;
    private boolean initializingSpotify = false;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mPlayerBoundService = ((PlayerService.PlayerBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mPlayerBoundService = null;
        }
    };

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mainView = inflater.inflate(R.layout.fragment_user_profile, container, false);
        ButterKnife.bind(this, mainView);

        userProfileViewModel = getViewModel();

        return mainView;
    }

    private void setViewVisibility(View view, int visibility) {
        if (view != null) view.setVisibility(visibility);
    }

    private void setViewVisibility(View view, boolean visible) {
        setViewVisibility(view, visible ? View.VISIBLE : View.INVISIBLE);
    }

    private void setViewText(@Nullable TextView textView, String text) {
        if (textView != null) textView.setText(text);
    }

    @OnClick(R.id.deleteAccountButton)
    public void onDeleteAccountClicked(View view) {
        AlertDialog deleteDialog = new AlertDialog.Builder(getContext())
                .setMessage(getContext().getResources().getString(R.string.alert_delete_account))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showProgressBar(profileProgressBar);
                        List<Thread> allThreads = ChatSDK.thread()
                                .getThreads(ThreadType.Private1to1);
                        for (Thread thread : allThreads) {
                            ChatSDK.thread().deleteThread(thread);
                        }

                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        String currentUserId = ChatSDK.currentUserID();
                        deleteUser(firebaseUser, currentUserId);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null).create();
        deleteDialog.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                deleteDialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.red));
                deleteDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
            }
        });
        deleteDialog.show();
    }

    @OnClick(R.id.editProfileButton)
    public void onEditProfileClicked() {
        Intent intent = new Intent(getContext(), SoloRegistrationActivity.class);
        startActivity(intent);
    }

    private void updateInterface() {
        User user = userProfileViewModel.getUser();

        if (user == null) {
            return;
        }

        String distance = userProfileViewModel.getDistance();

        boolean isCurrentUser = user.isMe();

        editProfileButton.setVisibility(isCurrentUser ? View.VISIBLE : View.GONE);
        logoutButton.setVisibility(isCurrentUser ? View.VISIBLE : View.GONE);
        deleteAccountButton.setVisibility(isCurrentUser ? View.VISIBLE : View.GONE);
        privacyPolicyCardView.setVisibility(isCurrentUser ? View.VISIBLE : View.GONE);

        setHasOptionsMenu(isCurrentUser);
        connectButton.setVisibility(isCurrentUser ? View.INVISIBLE : View.VISIBLE);

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startChat();
            }
        });

        NearbyUser nearbyUser = new NearbyUser(user, distance);

        if (!TextUtils.isEmpty(nearbyUser.getUsername())) {
            setViewText(profileUserNameTextView, nearbyUser.getUsername());
            if (!TextUtils.isEmpty(nearbyUser.getAvatarUrl())) {
                profilePhotoImageView.setImageURI(nearbyUser.getAvatarUrl());
            } else {
                ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
                int color = generator.getColor(nearbyUser.getEmail());

                TextDrawable drawable = TextDrawable.builder()
                        .buildRoundRect(nearbyUser.getUsername().substring(0,1), color, 30);

                profilePhotoImageView.setImageDrawable(drawable);
            }

            String availability = nearbyUser.getAvailability();
            if (profileUserAvailabilityImageView != null) {
                if (availability != null && !isCurrentUser) {
                    profileUserAvailabilityImageView.setImageResource(
                            AvailabilityHelper.imageResourceIdForAvailability(availability));
                    setViewVisibility(profileUserAvailabilityImageView, true);
                } else {
                    setViewVisibility(profileUserAvailabilityImageView, false);
                }
            }

            if (!TextUtils.isEmpty(nearbyUser.getDescription())) {
                userInfoLayout.setVisibility(View.VISIBLE);
                userDescriptionTextView.setText(nearbyUser.getDescription());
            }

            userProfileViewModel.setYoutubeVideoIds(nearbyUser.getYoutube());
            userProfileViewModel.setSpotifyTrackIds(nearbyUser.getSpotify());
            updateMediaPlayerView();

            setInstagramOnClick(nearbyUser);
            setFacebookOnClick(nearbyUser);
            setYoutubeOnClick(nearbyUser);
            if (!isCurrentUser) {
                userProfileDistanceTextView.setVisibility(View.VISIBLE);
                userProfileDistanceTextView.setText(nearbyUser.getDistance() + " mi");
            } else {
                userProfileDistanceTextView.setVisibility(View.GONE);
            }

            userProfileInfoAdapter.notifyData(userProfileViewModel.getProfileInfo());

            if (!isCurrentUser) {
                addOrRemoveContactImageButton.setVisibility(View.VISIBLE);
                updateContactButton(userProfileViewModel.isFriend());
                addOrRemoveContactImageButton.setOnClickListener(view -> toggleFriends());
            } else {
                addOrRemoveContactImageButton.setVisibility(View.GONE);
            }

            appVersionTextView.setText(
                    getResources().getString(R.string.app_version) + ": " + BuildConfig.VERSION_NAME);
        }
    }

    private void updateMediaPlayerView() {
        if (mediaGalleryView != null) {
            mediaGalleryView.setVisibility(View.GONE);

            if (mediaRecyclerView != null) {
                trackIds.clear();
                trackIds.addAll(userProfileViewModel.getYoutubeVideoIds());
                if (!userProfileViewModel.getSpotifyTrackIds().isEmpty() && !initializingSpotify) {
                    spotifyInitialize();
                } else {
                    if (!trackIds.isEmpty()) {
                        mediaPlayerAdapter = new MediaPlayerAdapter(
                                trackIds,
                                this.getLifecycle(),
                                false,
                                null);
                        mediaRecyclerView.setAdapter(mediaPlayerAdapter);

                        mediaGalleryView.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    private void spotifyInitialize() {
            if (userProfileViewModel.getSpotifyAccessToken().isEmpty()) {
                // Request code will be used to verify if result comes from the login activity. Can be set to any integer.
                AuthenticationRequest.Builder builder =
                        new AuthenticationRequest.Builder(getResources().getString(R.string.spotify_client_id), AuthenticationResponse.Type.TOKEN, AppConstants.SPOTIFY_REDIRECT_URI);

                builder.setShowDialog(true);
                builder.setScopes(new String[]{"streaming"});
                AuthenticationRequest request = builder.build();
                AuthenticationClient.openLoginActivity(getActivity(), REQUEST_CODE, request);
                initializingSpotify = true;
            } else {
                addSpotifyTracks();
            }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            if (requestCode == REQUEST_CODE) {
                AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);

                switch (response.getType()) {
                    // Response was successful and contains auth token
                    case TOKEN:
                        // Handle successful response
                        userProfileViewModel.setSpotifyAccessToken(response.getAccessToken());
                        addSpotifyTracks();
                        initializingSpotify = false;
                        Log.d(TAG, "Access Token: " + userProfileViewModel.getSpotifyAccessToken());
                        break;

                    // Auth flow returned an error
                    case ERROR:
                        // Handle error response
                        Log.e(TAG, "Spotify authorization failed."+response.getError());

                        // Most likely auth flow was cancelled
                    default:
                        // Handle other cases
                        mediaPlayerAdapter = new MediaPlayerAdapter(
                                trackIds,
                                this.getLifecycle(),
                                false,
                                null);
                        mediaRecyclerView.setAdapter(mediaPlayerAdapter);
                        mediaGalleryView.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void addSpotifyTracks() {
        String spotifyAccessToken = userProfileViewModel.getSpotifyAccessToken();
        if (!TextUtils.isEmpty(spotifyAccessToken)) {
            doBindService();
            trackIds.addAll(userProfileViewModel.getSpotifyTrackIds());
            SpotifyApi spotifyApi = new SpotifyApi();
            spotifyApi.setAccessToken(spotifyAccessToken);
            SpotifyService spotifyService = spotifyApi.getService();
            mediaPlayerAdapter = new MediaPlayerAdapter(
                    trackIds,
                    this.getLifecycle(),
                    spotifyService,
                    mPlayerBoundService,
                    false,
                    null);
        } else{
            mediaPlayerAdapter = new MediaPlayerAdapter(
                    trackIds,
                    this.getLifecycle(),
                    false,
                    null);
        }

        mediaRecyclerView.setAdapter(mediaPlayerAdapter);
        mediaGalleryView.setVisibility(View.VISIBLE);
    }

    private void doBindService() {
        if (getContext().bindService(
                PlayerService.getIntent(getContext()),
                mServiceConnection,
                Activity.BIND_AUTO_CREATE)) {
            mSpotifyPlayerBound = true;
        }
    }

    private void doUnbindService() {
        if (mSpotifyPlayerBound) {
            getContext().unbindService(mServiceConnection);
            mSpotifyPlayerBound = false;
        }
    }

    @OnClick(R.id.privacyPolicyButton)
    public void privacyPolicyButtonClicked() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(AppConstants.PRIVACY_POLICY_URL));
        startActivity(intent);
    }

    private void setInstagramOnClick(NearbyUser nearbyUser) {
        if (this.userProfileInstagramView != null && nearbyUser != null) {
            String instagram = nearbyUser.getInstagram();
            if (!TextUtils.isEmpty(instagram)) {
                this.userProfileInstagramView.setVisibility(View.VISIBLE);
                this.userProfileInstagramView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.parse("http://instagram.com/_u/" + instagram);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        intent.setPackage("com.instagram.android");
                        try {
                            getActivity().startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            getActivity().startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://instagram.com/" + instagram)));
                        }
                    }
                });
            } else {
                this.userProfileInstagramView.setVisibility(View.GONE);
            }
        }
    }

    private void setFacebookOnClick(NearbyUser nearbyUser) {
        if (this.userProfileFacebookView != null && nearbyUser != null) {
            String facebookPageId = nearbyUser.getFacebookPageId();
            if (!TextUtils.isEmpty(facebookPageId)) {
                this.userProfileFacebookView.setVisibility(View.VISIBLE);
                this.userProfileFacebookView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        try {
                            getActivity().getPackageManager()
                                    .getPackageInfo("com.facebook.katana", 0); //Checks if FB is even installed.
                            intent.setData(Uri.parse("fb://page/" + facebookPageId));
                        } catch (Exception e) {
                            intent.setData(Uri.parse("https://www.facebook.com/" + facebookPageId)); //catches and opens a url to the desired page
                        }
                        getActivity().startActivity(intent);
                    }
                });
            }
            else {
                this.userProfileFacebookView.setVisibility(View.GONE);
            }
        }
    }

    private void setYoutubeOnClick(NearbyUser nearbyUser) {
        if (this.userProfileYoutubeView != null && nearbyUser != null) {
            String youtube_channel = nearbyUser.getYoutubeChannel();
            if (!TextUtils.isEmpty(youtube_channel)) {
                this.userProfileYoutubeView.setVisibility(View.VISIBLE);
                this.userProfileYoutubeView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = YouTubeIntents.createUserIntent(v.getContext(), youtube_channel);
                        v.getContext().startActivity(intent);
                    }
                });
            }
            else {
                this.userProfileYoutubeView.setVisibility(View.GONE);
            }
        }
    }

    private void deleteUserAuthentication(FirebaseUser firebaseUser) {
        reauthenticateUser(firebaseUser);
        firebaseUser.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            removeProgressBar(profileProgressBar);
                            ChatSDK.ui().startSplashScreenActivity(
                                    getActivity().getApplicationContext());
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

    private AuthCredential getFbCredentials() {
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
        showProgressBar(profileProgressBar);
        disposableList.add(ChatSDK.auth().logout()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action() {
                            @Override
                            public void run() throws Exception {
                                userProfileViewModel.logout();
                                ChatSDK.ui().startSplashScreenActivity(
                                        getActivity().getApplicationContext());
                                Log.d(TAG,"ChatSDK logged out successfully!!");
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Log.d(TAG,throwable.toString());
                            }
                        })
        );
    }

    @Override
    public void clearData() {

    }

    @Override
    public void reloadData() {
        updateInterface();
    }

    private void startChat () {
        boolean startingChat = userProfileViewModel.getStartingSdkChat();
        if (startingChat) {
            return;
        }

        userProfileViewModel.setStartingSdkChat(true);
        User user = userProfileViewModel.getUser();
        showProgressBar(profileProgressBar);
        disposableList.add(
                ChatSDK.thread().createThread("", user, ChatSDK.currentUser())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doFinally(() -> {
                            removeProgressBar(profileProgressBar);
                            userProfileViewModel.setStartingSdkChat(false);
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

        updateInterface();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.getString(Keys.USER_ENTITY_ID) != null) {
            disposableList.add(
                    ChatSDK.events().sourceOnMain().filter(
                            NetworkEvent.filterType(
                                    EventType.UserMetaUpdated,
                                    EventType.UserPresenceUpdated))
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(networkEvent -> {
                                if (networkEvent.user.equals(userProfileViewModel.getUser())) {
                                    reloadData();
                                }
                            })
            );
        }

        userProfileInfoAdapter = new UserProfileInfoAdapter(
                getContext(),
                R.layout.item_userprofileinfo);
        if (userInfoRecyclerView != null) {
            userInfoRecyclerView.setAdapter(userProfileInfoAdapter);

            DividerItemDecoration itemDecor = new DividerItemDecoration(
                    userInfoRecyclerView.getContext(),
                    DividerItemDecoration.HORIZONTAL);
            userInfoRecyclerView.addItemDecoration(itemDecor);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            userInfoRecyclerView.setLayoutManager(layoutManager);
        }

        if (mediaRecyclerView != null) {
            mediaRecyclerView.setHasFixedSize(true);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(
                    getActivity(),
                    LinearLayoutManager.HORIZONTAL,
                    false);
            mediaRecyclerView.setLayoutManager(layoutManager);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        disposableList.dispose();
    }

    private void showProgressBar(@Nullable ProgressBar progressBar) {
        if (progressBar != null && progressBar.getVisibility() == View.GONE) {
            progressBar.setVisibility(View.VISIBLE);
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    private void removeProgressBar(@Nullable ProgressBar progressBar) {
        if (progressBar != null && progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.GONE);
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    protected void toggleFriends() {
        if (userProfileViewModel.isFriend()) deleteContact();
        else addContact();
    }

    protected void addContact() {
        disposableList.add(userProfileViewModel.addContact()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    updateContactButton(true);
                }, throwable -> {
                    ChatSDK.logError(throwable);
                }));
    }

    protected void deleteContact() {
        disposableList.add(ChatSDK.contact().deleteContact(userProfileViewModel.getUser(), ConnectionType.Contact)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    updateContactButton(false);
                }, throwable -> {
                    ChatSDK.logError(throwable);
                }));
    }

    protected void updateContactButton(boolean contact) {
        if (contact) {
            addOrRemoveContactImageButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorited, 0, 0, 0);
        } else {
            addOrRemoveContactImageButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_not_favorited, 0, 0, 0);
        }
    }

    @NonNull
    private UserProfileViewModel getViewModel() {
        BaseRepository baseRepository = BaseRepository.getInstance();
        ViewModelProvider.Factory factory = new UserProfileViewModelFactory(baseRepository);
        return ViewModelProviders.of(requireActivity(), factory).get(UserProfileViewModel.class);
    }
}