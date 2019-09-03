package com.legato.music;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.legato.music.R;
import com.legato.music.AppConstants;
import com.legato.music.messaging.ActiveChatActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.chatsdk.core.dao.Keys;
import co.chatsdk.core.dao.User;
import co.chatsdk.core.hook.Hook;
import co.chatsdk.core.hook.HookEvent;
import co.chatsdk.core.session.ChatSDK;
import io.reactivex.Completable;

public class NearbyUsersActivity extends AppCompatActivity implements FilterDialogFragment.FilterListener, GeofireHelper.NearbyUserFoundListener  {

    @BindView(R.id.nearbyUserRecylerView)
    RecyclerView mNearbyUserRecyclerView;

    private GeofireHelper geofireHelper;
    private NearbyUsersAdapter mNearbyUsersAdapter;
    private FilterDialogFragment mFilterDialog;
    private User mUser;
    final int REQUEST_FINE_LOCATION=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_users);
        ButterKnife.bind(this);
        mNearbyUsersAdapter = new NearbyUsersAdapter(this, R.layout.item_nearbyuser);
        if (mNearbyUserRecyclerView != null) {
            mNearbyUserRecyclerView.setAdapter(mNearbyUsersAdapter);
        }
        mFilterDialog = new FilterDialogFragment(mNearbyUsersAdapter);

        DividerItemDecoration itemDecor = new DividerItemDecoration(mNearbyUserRecyclerView.getContext(), DividerItemDecoration.HORIZONTAL);
        mNearbyUserRecyclerView.addItemDecoration(itemDecor);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mNearbyUserRecyclerView.setLayoutManager(layoutManager);

        ChatSDK.hook().addHook(new Hook(data -> Completable.create(emitter -> {
            this.finish();
            emitter.onComplete();
        })), HookEvent.WillLogout);

        getLastLocation();
    }

    @Override
    public void onFilter(Filters filters) {
        mNearbyUsersAdapter.onFilter(filters);
    }

    @OnClick(R.id.buttonFilter)
    public void onFilterClicked() {
        // Show the dialog containing filter options
        mFilterDialog.show(getSupportFragmentManager(), FilterDialogFragment.TAG);
    }

    @OnClick(R.id.buttonActiveChats)
    public void onActiveChatsClicked() {
        Intent intent = new Intent(this, ActiveChatActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.buttonSettings)
    public void onSettingsClicked() {
        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.putExtra(Keys.USER_ENTITY_ID, ChatSDK.currentUserID());
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void getLastLocation() {
        mUser = ChatSDK.currentUser();
        geofireHelper = GeofireHelper.getInstance(this);
        // Get last known recent location using new Google Play Services SDK (v11+)
        boolean havePermission = checkPermissions();

        if (havePermission) {
            onLocationPermissionsGranted();
        }
    }

    private void onLocationPermissionsGranted() {
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
                            //showSnackbar(R.string.location_failure);
                        }
                    });
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
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    this.onLocationPermissionsGranted();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            default:
                return;
        }
    }

    private void onLocationChanged(Location location) {
        geofireHelper.setLocation(location);
        String searchRadius = AppConstants.DEFAULT_SEARCH_RADIUS;
        if (mUser != null) {
            searchRadius = mUser.metaStringForKey(com.legato.music.Keys.searchradius);
            if (!(searchRadius != null && !searchRadius.isEmpty()))
                searchRadius = AppConstants.DEFAULT_SEARCH_RADIUS;
        }

        geofireHelper.queryNeighbors(Integer.parseInt(searchRadius));
    }

    @Override
    public void nearbyUserFound(String userId, String distance) {
        mNearbyUsersAdapter.addNearbyUserToRecyclerView(userId, distance);
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mNearbyUsersAdapter.onDestroy();
        mNearbyUserRecyclerView.setAdapter(null);
    }
}
