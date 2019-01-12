package com.example.spoluri.legato;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.spoluri.legato.authentication.LoginActivity;
import com.example.spoluri.legato.messaging.ActiveChatActivity;
import com.example.spoluri.legato.youtube.YoutubeActivity;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;

import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class AccountActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
    private static final String TAG = "AccountActivity";

    private TextView id;
    private TextView infoLabel;
    private TextView info;

    private String mUserId = AppConstants.ANONYMOUS;

    private DatabaseReference mUserProfileDatabaseReference;
    private YouTubePlayerView youTubeView;
    private String mYoutubeVideo = "";
    private GeofireHelper geofireHelper;
    private View mRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        youTubeView = findViewById(R.id.youtube_view);

        id = findViewById(R.id.id);
        infoLabel = findViewById(R.id.info_label);
        info = findViewById(R.id.info);

        mRootView = findViewById(R.id.account_root);
        mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        mUserProfileDatabaseReference = firebaseDatabase.getReference().child("userprofiledata");
        geofireHelper = new GeofireHelper();
        getLastLocation();

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
    }

    private void InitializeYoutubeView() {
        youTubeView.initialize(AppConstants.YOUTUBE_API_KEY, this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void onLogout(View view) {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            launchLoginActivity();
                        }
                    }
                });
    }

    public void onYoutube(View view) {
        launchYoutubeActivity();
    }

    private void launchLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void launchYoutubeActivity() {
        Intent intent = new Intent(this, YoutubeActivity.class);
        startActivityForResult(intent, RequestCodes.YOUTUBE_VIDEO_FLOW);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RequestCodes.YOUTUBE_VIDEO_FLOW && resultCode == RESULT_OK && data != null) {
            Map<String, Object> youtube = new HashMap<>();
            // put() method
            mYoutubeVideo = data.getStringExtra("youtube_video");
            youtube.put("youtube_video", mYoutubeVideo);
            mUserProfileDatabaseReference.child(mUserId).updateChildren(youtube);
            InitializeYoutubeView();
        }
    }

    public void onMessengerLaunch(View view) {
        Intent intent = new Intent(this, ActiveChatActivity.class);
        startActivity(intent);
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

    public void getLastLocation() {
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

    public void onLocationChanged(Location location) {
        geofireHelper.setLocation(location);
        //TODO: get the search radius from the user
        geofireHelper.queryNeighbors(1600);
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
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
    }

    private void showSnackbar(@StringRes int errorMessageRes) {
        Snackbar.make(mRootView, errorMessageRes, Snackbar.LENGTH_LONG).show();
    }
}
