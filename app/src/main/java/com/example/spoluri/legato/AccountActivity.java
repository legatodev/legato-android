package com.example.spoluri.legato;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;
import android.support.v4.content.ContextCompat;

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

import java.util.HashMap;
import java.util.Map;

public class AccountActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener{
    TextView id;
    TextView infoLabel;
    TextView info;

    private String mUserId = AppConstants.ANONYMOUS;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserProfileDatabaseReference;
    ChildEventListener mChildEventListener;

    private YouTubePlayerView youTubeView;
    private String mYoutubeVideo = "";
    private GeofireHelper geofireHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        youTubeView = findViewById(R.id.youtube_view);

        id = findViewById(R.id.id);
        infoLabel = findViewById(R.id.info_label);
        info = findViewById(R.id.info);

        mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUserProfileDatabaseReference = mFirebaseDatabase.getReference().child("userprofiledata");
        geofireHelper = new GeofireHelper();
        getLastLocation();

        mChildEventListener = new ChildEventListener() {
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

            }
        };
        mUserProfileDatabaseReference.child(mUserId).addChildEventListener(mChildEventListener);
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
            Map<String, Object> youtube = new HashMap<String, Object>();
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

    @Override
    public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasRestored) {
        player.cueVideo(mYoutubeVideo);
    }

    @Override
    public void onInitializationFailure(Provider provider, YouTubeInitializationResult errorReason) {
    }

    public void getLastLocation() {
        //TODO: ask for location permission from user
        // Get last known recent location using new Google Play Services SDK (v11+)
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
                            System.out.println("Failure");
                        }
                    });
        }
    }

    public void onLocationChanged(Location location) {
        geofireHelper.setLocation(location);
        //TODO: get the search radius from the user
        geofireHelper.queryNeighbors(10);
    }
}
