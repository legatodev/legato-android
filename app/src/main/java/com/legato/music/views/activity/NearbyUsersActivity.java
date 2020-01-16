package com.legato.music.views.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.legato.music.models.NearbyUser;
import com.legato.music.views.adapters.NearbyUsersAdapter;
import com.legato.music.messaging.ActiveChatActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.legato.music.viewmodels.NearbyUsersViewModel;

import org.w3c.dom.Text;

import java.util.List;

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
    @BindView(R.id.noNearbyUsersTextView)
    TextView noNearbyUsersTextView;
    @BindView(R.id.nearbyUserProgressBar)
    ProgressBar nearbyUserProgressBar;

    private NearbyUsersAdapter mNearbyUsersAdapter;
    private DisposableList disposableList = new DisposableList();
    @Nullable NearbyMessages nearbyMessages;
    private NearbyUsersViewModel mNearbyUsersViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_users);
        ButterKnife.bind(this);

        mNearbyUsersViewModel = ViewModelProviders.of(this).get(NearbyUsersViewModel.class);

        initRecyclerView();
        if (!isEmailVerified()) {
            createVerificationDialog();
        } else {
            ChatSDK.hook().addHook(new Hook(data -> Completable.create(emitter -> {
                this.finish();
                emitter.onComplete();
            })), HookEvent.WillLogout);

            subscribeObservers();
            getLastLocation();
            initProximityAlert();
        }
    }

    private void initRecyclerView() {
        mNearbyUsersAdapter = new NearbyUsersAdapter(this, R.layout.item_nearbyuser);
        mNearbyUserRecyclerView.setAdapter(mNearbyUsersAdapter);

        DividerItemDecoration itemDecor = new DividerItemDecoration(mNearbyUserRecyclerView.getContext(), DividerItemDecoration.HORIZONTAL);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mNearbyUserRecyclerView.addItemDecoration(itemDecor);
        mNearbyUserRecyclerView.setLayoutManager(layoutManager);
    }

    private void initProximityAlert(){
        if (mNearbyUsersViewModel.isProximityAlertEnabled()) {
            nearbyMessages = new NearbyMessages(this);
            nearbyMessages.initialize();
        }else{
            Log.w(TAG,"Proximity alert has been disabled");
        }
    }

    private void subscribeObservers() {
        mNearbyUsersViewModel.getNearbyUsers().observe(this, new Observer<List<NearbyUser>>() {
            @Override
            public void onChanged(@Nullable List<NearbyUser> nearbyUsers) {
                if (nearbyUsers != null) {
                    noNearbyUsersTextView.setVisibility(View.GONE);
                    mNearbyUserRecyclerView.setVisibility(View.VISIBLE);
                    mNearbyUsersAdapter.updateNearbyUsers(nearbyUsers);
                }
            }
        });
    }

    private void logout() {
        showProgressBar(nearbyUserProgressBar);
        disposableList.add(ChatSDK.auth().logout()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> ChatSDK.ui().startSplashScreenActivity(this.getApplicationContext()), throwable -> {
                    ChatSDK.logError(throwable);
                })
        );
    }

    private void showProgressBar(@io.reactivex.annotations.Nullable ProgressBar progressBar) {
        if (progressBar != null && progressBar.getVisibility() == View.GONE) {
            progressBar.setVisibility(View.VISIBLE);
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    private void removeProgressBar(@io.reactivex.annotations.Nullable ProgressBar progressBar) {
        if (progressBar != null && progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.GONE);
            this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
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
        return mNearbyUsersViewModel.isEmailVerified();
    }

    private void sendEmailVerification() {
        mNearbyUsersViewModel.sendEmailVerification();
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
                    //TODO: allow user to choose location
                    Toast.makeText(this, "Location permissions are needed for Legato to work", Toast.LENGTH_SHORT).show();
                    Log.e(TAG,"Location tracking permission denied");

                }
                return;
            }
            default:
                return;
        }
    }

    private void onLocationChanged(Location location) {
        mNearbyUsersViewModel.setLocation(location);

        String searchRadius = mNearbyUsersViewModel.getSearchRadius();
        if (TextUtils.isEmpty(searchRadius))
            searchRadius = AppConstants.DEFAULT_SEARCH_RADIUS;

        mNearbyUsersViewModel.searchNearbyUserByRadius(Integer.parseInt(searchRadius));
    }

    @OnClick(R.id.buttonFilter)
    public void onFilterClicked() {
        // Show the dialog containing filter options
        FilterDialogFragment filterDialog = new FilterDialogFragment();
        filterDialog.show(getSupportFragmentManager(), FilterDialogFragment.TAG);
    }

    @Override
    public void onFilter(Filters filters) {
        mNearbyUsersAdapter.setFilters(filters);
        mNearbyUsersViewModel.searchNearbyUserByRadius(Integer.parseInt(filters.getSearchRadius()));
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
