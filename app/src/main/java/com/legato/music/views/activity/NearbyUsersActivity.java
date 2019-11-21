package com.legato.music.views.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.legato.music.AppConstants;
import com.legato.music.views.fragments.FilterDialogFragment;
import com.legato.music.models.Filters;
import com.legato.music.NearbyMessages;
import com.legato.music.R;
import com.legato.music.UserProfileActivity;
import com.legato.music.models.NearbyUser;
import com.legato.music.views.adapters.NearbyUsersAdapter;
import com.legato.music.messaging.ActiveChatActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.legato.music.viewmodels.NearbyUserViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.chatsdk.core.dao.Keys;
import co.chatsdk.core.hook.Hook;
import co.chatsdk.core.hook.HookEvent;
import co.chatsdk.core.session.ChatSDK;
import co.chatsdk.core.utils.DisposableList;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class NearbyUsersActivity extends AppCompatActivity implements
        FilterDialogFragment.FilterListener  {

    private static final String TAG = NearbyUsersActivity.class.getSimpleName();
    @BindView(R.id.nearbyUserRecylerView)
    RecyclerView mNearbyUserRecyclerView;

    private NearbyUsersAdapter mNearbyUsersAdapter;
    private FilterDialogFragment mFilterDialog;
    private DisposableList disposableList = new DisposableList();
    @Nullable NearbyMessages nearbyMessages;
    private NearbyUserViewModel mNearbyUserViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_users);
        ButterKnife.bind(this);

        mNearbyUserViewModel = ViewModelProviders.of(this).get(NearbyUserViewModel.class);
        if (!isEmailVerified()) {
            createVerificationDialog();
        }

        mNearbyUsersAdapter = new NearbyUsersAdapter(this, R.layout.item_nearbyuser);
        if (mNearbyUserRecyclerView != null) {
            mNearbyUserRecyclerView.setAdapter(mNearbyUsersAdapter);
        }
        DividerItemDecoration itemDecor = new DividerItemDecoration(mNearbyUserRecyclerView.getContext(), DividerItemDecoration.HORIZONTAL);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mFilterDialog = new FilterDialogFragment(mNearbyUsersAdapter);
        mNearbyUserRecyclerView.addItemDecoration(itemDecor);
        mNearbyUserRecyclerView.setLayoutManager(layoutManager);

        ChatSDK.hook().addHook(new Hook(data -> Completable.create(emitter -> {
            this.finish();
            emitter.onComplete();
        })), HookEvent.WillLogout);

        subscribeObservers();
        getLastLocation();
        initProximityAlert();
    }

    private void initProximityAlert(){
        if (mNearbyUserViewModel.isProximityAlertEnabled()) {
            nearbyMessages = new NearbyMessages(this);
            nearbyMessages.initialize();
        }else{
            Log.w(TAG,"Proximity alert has been disabled");
        }
    }

    private void subscribeObservers() {
        mNearbyUserViewModel.getNearbyUser().observe(this, new Observer<NearbyUser>() {
            @Override
            public void onChanged(@Nullable NearbyUser nearbyUser) {
                if (nearbyUser != null) {
                    mNearbyUsersAdapter.addNearbyUser(nearbyUser);
                }
            }
        });
    }

    private void logout() {
        disposableList.add(ChatSDK.auth().logout()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> ChatSDK.ui().startSplashScreenActivity(this.getApplicationContext()), throwable -> {
                    ChatSDK.logError(throwable);
                })
        );
    }

    public void createVerificationDialog() {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.alert_email_verification)
                .setPositiveButton(R.string.resend_verification, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        sendEmailVerification();
                        logout();
                    }
                })
                .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        logout();
                    }
                })
        .show();
    }

    private boolean isEmailVerified() {
        return mNearbyUserViewModel.isEmailVerified();
    }

    private void sendEmailVerification() {
        mNearbyUserViewModel.sendEmailVerification();
    }

    private void getLastLocation() {
        // Get last known recent location using new Google Play Services SDK (v11+)
        boolean havePermission = checkPermissions();

        if (havePermission) {
            onLocationPermissionsGranted();
        }
    }

    private void onLocationPermissionsGranted() {
        FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient(this);

        if (locationClient != null) {
            try {
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
                                Log.e(TAG, getResources().getString(R.string.location_failure));
                            }
                        });
            } catch (SecurityException e) {
                Log.e(TAG, "Location permissions failure");
            }
        }
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions();
            return false;
        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, AppConstants.REQUEST_FINE_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case AppConstants.REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    this.onLocationPermissionsGranted();

                } else {
                    /*  permission denied, boo!
                        TODO: Should try another time with additional explaination?
                     */
                    Log.e(TAG,"Location tracking permission denied");

                }
                return;
            }
            default:
                return;
        }
    }

    private void onLocationChanged(Location location) {
        mNearbyUserViewModel.setLocation(location);

        String searchRadius = mNearbyUserViewModel.getSearchRadius();
        if (!(searchRadius != null && !searchRadius.isEmpty()))
            searchRadius = AppConstants.DEFAULT_SEARCH_RADIUS;

        mNearbyUserViewModel.searchNearbyUserByRadius(Integer.parseInt(searchRadius));
    }

    @OnClick(R.id.buttonFilter)
    public void onFilterClicked() {
        // Show the dialog containing filter options
        mFilterDialog.show(getSupportFragmentManager(), FilterDialogFragment.TAG);
    }

    @Override
    public void onFilter(Filters filters) {
        mNearbyUsersAdapter.setFilters(filters);
        mNearbyUserViewModel.searchNearbyUserByRadius(Integer.parseInt(filters.getSearchRadius()));
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

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        disposableList.dispose();
    }
}
