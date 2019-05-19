package com.example.spoluri.legato;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import com.google.android.material.snackbar.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spoluri.legato.messaging.ActiveChatActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;

import androidx.core.app.ActivityCompat;

import java.util.HashMap;
import java.util.Map;

import co.chatsdk.core.dao.Keys;
import co.chatsdk.core.dao.User;
import co.chatsdk.core.session.ChatSDK;
import co.chatsdk.core.types.AuthKeys;
import co.chatsdk.core.types.ConnectionType;
import co.chatsdk.core.utils.DisposableList;
import co.chatsdk.ui.utils.ToastHelper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class AccountActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
    private static final String TAG = "AccountActivity";

    private TextView id;
    private TextView infoLabel;
    private TextView info;

    private String mUserId = AppConstants.ANONYMOUS;
    private View mRootView;
    private YouTubePlayerView mYouTubeView;
    private String mYoutubeVideo = "";
    private GeofireHelper geofireHelper;
    private DatabaseReference mUserProfileDatabaseReference;

    protected User user;
    protected boolean startingChat = false;

    private DisposableList disposableList = new DisposableList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        id = findViewById(R.id.id);
        infoLabel = findViewById(R.id.info_label);
        info = findViewById(R.id.info);

        mRootView = findViewById(R.id.account_root);
        mYouTubeView = findViewById(R.id.youtube_view);

        mUserId = (String)ChatSDK.auth().getLoginInfo().get(AuthKeys.CurrentUserID);
        geofireHelper = new GeofireHelper(mUserId);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        mUserProfileDatabaseReference = firebaseDatabase.getReference().child("userprofiledata");

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getKey().equals("youtube_video")) {
                    mYoutubeVideo = dataSnapshot.getValue(String.class);
                    InitializeYoutubeView();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, databaseError.toException());
            }
        };

        mUserProfileDatabaseReference.child(mUserId).addChildEventListener(childEventListener);

        getLastLocation();
    }

    private void InitializeYoutubeView() {
        mYouTubeView.initialize(AppConstants.YOUTUBE_API_KEY, this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void onLogout(View view) {
        ChatSDK.auth().logout()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> ChatSDK.ui().startSplashScreenActivity(getApplicationContext()), throwable -> {
                    ChatSDK.logError(throwable);
                    Toast.makeText(AccountActivity.this, throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    public void onYoutube(View view) {
        Intent intent = new Intent(this, AvatarActivity.class);
        startActivity(intent);
        //Intent intent = new Intent(this, YoutubeActivity.class);
        //startActivityForResult(intent, RequestCodes.RC_YOUTUBE_SEARCH);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RequestCodes.RC_YOUTUBE_SEARCH && resultCode == RESULT_OK && data != null) {
            Map<String, Object> youtube = new HashMap<>();
            // put() method
            mYoutubeVideo = data.getStringExtra("youtube_video");
            youtube.put("youtube_video", mYoutubeVideo);
            mUserProfileDatabaseReference.child(mUserId).updateChildren(youtube);
                InitializeYoutubeView();
        }
    }

    public void onMessengerLaunch(View view) {
        //Intent intent = new Intent(this, ActiveChatActivity.class);
        //startActivity(intent);
        String userEntityID = "MZW9VySBWJcjKDqdj8O7W6EDur12";

        if (userEntityID != null && !userEntityID.isEmpty()) {
            user = ChatSDK.db().fetchUserWithEntityID(userEntityID);
            Disposable d = ChatSDK.core().userOn(user).subscribe(() -> {
                disposableList.add(ChatSDK.contact().addContact(user, ConnectionType.Contact)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                        }, throwable -> {
                            ChatSDK.logError(throwable);
                        }));

                // User object has now been populated and is ready to use
                startChat();

            }, throwable -> {

            });
        }
    }

    public void startChat () {

        if (startingChat) {
            return;
        }

        startingChat = true;

        disposableList.add(ChatSDK.thread().createThread("", user, ChatSDK.currentUser())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {
                    startingChat = false;
                })
                .subscribe(thread -> {
                    ChatSDK.ui().startChatActivityForID(getApplicationContext(), thread.getEntityID());
                }, throwable -> {
                    ToastHelper.show(getApplicationContext(), throwable.getLocalizedMessage());
                }));
    }


    public void onNearbyUsers(View view) {
        Intent intent = new Intent(this, NearbyUsersActivity.class);
        intent.putExtra("nearby_users", geofireHelper.getNearbyUsersList());
        startActivity(intent);
    }

    @Override
    public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasRestored) {
        player.cueVideo(mYoutubeVideo);
    }

    @Override
    public void onInitializationFailure(Provider provider, YouTubeInitializationResult errorReason) {
        showSnackbar(R.string.youtube_initialization_failure);
        Log.e(TAG, "Youtube Initialization Failed: " + errorReason.toString());
    }

    private void getLastLocation() {
        // Get last known recent location using new Google Play Services SDK (v11+)
        boolean havePermission = checkPermissions();

        if (havePermission) {
            FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient(this);

            if (locationClient != null) {
                locationClient.getLastLocation()
                        .addOnSuccessListener(new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // GPS location can be null if GPS is switched off
                                if (location != null) {
                                    onLocationChanged(location);
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                showSnackbar(R.string.location_failure);
                            }
                        });
            }
        }
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions(); //TODO: need to implement a callback to know what the result is
            return false;
        }
    }

    private void requestPermissions() {
        final int REQUEST_FINE_LOCATION=0;
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
    }

    private void onLocationChanged(Location location) {
        geofireHelper.setLocation(location);
        //TODO: get the search radius from the user
        geofireHelper.queryNeighbors(1600);
    }

    private void showSnackbar(@StringRes int errorMessageRes) {
        Snackbar.make(mRootView, errorMessageRes, Snackbar.LENGTH_LONG).show();
    }
}
