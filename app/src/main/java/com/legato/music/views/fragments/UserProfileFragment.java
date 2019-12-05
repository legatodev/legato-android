package com.legato.music.views.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProviders;
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
import com.legato.music.R;
import com.legato.music.models.NearbyUser;
import com.legato.music.utils.LegatoAuthenticationHandler;
import com.legato.music.viewmodels.UserProfileViewModel;
import com.legato.music.views.activity.SoloRegistrationActivity;
import com.legato.music.views.adapters.UserProfileInfoAdapter;
import com.legato.music.views.adapters.YoutubePlayerAdapter;

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
    private static final String TAG = UserProfileFragment.class.getSimpleName();

    @BindView(R.id.profilePhotoImageView)
    protected SimpleDraweeView profilePhotoImageView;
    @BindView(R.id.profileUserAvailabilityImageView)
    protected ImageView profileUserAvailabilityImageView;
    @BindView(R.id.connectOrRemoveButton)
    protected Button connectOrRemoveButton;
    @BindView(R.id.editProfileButton)
    protected Button editProfileButton;
    @BindView(R.id.userDescription)
    protected TextView userDescriptionTextView;
    @BindView(R.id.profileInfoRecyclerView)
    protected RecyclerView userInfoRecyclerView;
    @BindView(R.id.profileUserNameTextView)
    protected TextView profileUserNameTextView;
    @BindView(R.id.emailTextView)
    protected TextView emailTextView;
    @BindView(R.id.logoutButton)
    protected Button logoutButton;
    @BindView(R.id.deleteAccountButton)
    protected Button deleteAccountButton;
    @BindView(R.id.youtubeRecyclerView)
    protected RecyclerView youtubeRecyclerView;
    @BindView(R.id.youtubeGalleryLayout)
    protected View youtubeGalleryView;

    private DisposableList disposableList = new DisposableList();

    private UserProfileInfoAdapter userProfileInfoAdapter;
    @Nullable private YoutubePlayerAdapter youtubePlayerAdapter;

    @NonNull private UserProfileViewModel userProfileViewModel;

    @BindView(R.id.profileProgressBar)
    ProgressBar profileProgressBar = null;

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mainView = inflater.inflate(R.layout.fragment_user_profile, container, false);
        ButterKnife.bind(this, mainView);

        userProfileViewModel = ViewModelProviders.of(requireActivity()).get(UserProfileViewModel.class);

        return mainView;
    }

    private void updateYoutubePlayerView() {
        if (youtubeGalleryView != null) {
            youtubeGalleryView.setVisibility(View.GONE);

            if (youtubeRecyclerView != null) {
                List<String> youtubeVideoIds = userProfileViewModel.getYoutubeVideoIds();

                if (!youtubeVideoIds.isEmpty()) {
                    youtubePlayerAdapter = new YoutubePlayerAdapter(
                            youtubeVideoIds,
                            this.getLifecycle());
                    youtubeRecyclerView.setAdapter(youtubePlayerAdapter);

                    youtubeGalleryView.setVisibility(View.VISIBLE);
                }
            }
        }
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
        new AlertDialog.Builder(getContext())
                .setMessage(getContext().getResources().getString(R.string.alert_delete_account))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
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
                .setNegativeButton(android.R.string.cancel, null)
                .show();
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

        if (!nearbyUser.getUsername().isEmpty()) {
            setViewText(profileUserNameTextView, nearbyUser.getUsername());
            setViewText(emailTextView, nearbyUser.getEmail());
            if (profilePhotoImageView != null && nearbyUser.getPhotoUrl() != null) {
                profilePhotoImageView.setImageURI(nearbyUser.getPhotoUrl());
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

            userDescriptionTextView.setText(nearbyUser.getDescription());

            userProfileViewModel.setYoutubeVideoIds(nearbyUser.getYoutube());
            updateYoutubePlayerView();

            userProfileInfoAdapter.notifyData(userProfileViewModel.getProfileInfo());
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
        disposableList.add(ChatSDK.auth().logout()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(() -> ChatSDK.ui().startSplashScreenActivity(
                getActivity().getApplicationContext()),
                throwable -> ChatSDK.logError(throwable))
        );
    }

    @Override
    public void clearData() {

    }

    @Override
    public void reloadData() {
        updateInterface();
        updateYoutubePlayerView();
    }

    private void startChat() {
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

        if (savedInstanceState != null && savedInstanceState.getString(Keys.UserId) != null) {
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

        if (youtubeRecyclerView != null) {
            youtubeRecyclerView.setHasFixedSize(true);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(
                    getActivity(),
                    LinearLayoutManager.HORIZONTAL,
                    false);
            youtubeRecyclerView.setLayoutManager(layoutManager);
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
}
