package com.example.spoluri.legato;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.ActionBar;

import com.example.spoluri.legato.messaging.ActiveChatActivity;
import com.example.spoluri.legato.registration.RegistrationActivity;
import com.example.spoluri.legato.youtube.YoutubeActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.chatsdk.core.dao.User;
import co.chatsdk.core.session.ChatSDK;
import co.chatsdk.core.types.AuthKeys;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class AccountActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
    private static final String TAG = "AccountActivity";

    @BindView(R.id.id)
    TextView id;

    @BindView(R.id.info_label)
    TextView infoLabel;

    @BindView(R.id.info)
    TextView info;

    @BindView(R.id.account_root)
    View mRootView;

    @BindView(R.id.youtube_view)
    YouTubePlayerView mYouTubeView;


    private String mUserId = AppConstants.ANONYMOUS;
    private String mYoutubeVideo = "";
    private GeofireHelper geofireHelper;

    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        ButterKnife.bind(this);

        geofireHelper = GeofireHelper.getInstance();
        mUserId = (String)ChatSDK.auth().getLoginInfo().get(AuthKeys.CurrentUserID);
        mUser = ChatSDK.db().fetchOrCreateEntityWithEntityID(User.class, mUserId);

        ChatSDK.core().userOn(mUser);
        mYoutubeVideo = mUser.metaStringForKey(Keys.youtube);
        InitializeYoutubeView();
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
        //Intent intent = new Intent(this, YoutubeActivity.class);
        //startActivityForResult(intent, RequestCodes.RC_YOUTUBE_SEARCH);
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RequestCodes.RC_YOUTUBE_SEARCH && resultCode == RESULT_OK && data != null) {
            // put() method
            mYoutubeVideo = data.getStringExtra("youtube_video");
            mUser.setMetaString(Keys.youtube, mYoutubeVideo);
            InitializeYoutubeView();
        }
    }

    public void onMessengerLaunch(View view) {
        Intent intent = new Intent(this, ActiveChatActivity.class);
        startActivity(intent);
    }

    public void onNearbyUsers(View view) {
        Intent intent = new Intent(this, NearbyUsersActivity.class);
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
