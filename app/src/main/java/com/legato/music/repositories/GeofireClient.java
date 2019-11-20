package com.legato.music.repositories;

import android.location.Location;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryDataEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.legato.music.models.NearbyUser;

import java.text.DecimalFormat;
import java.util.HashMap;

import co.chatsdk.core.dao.User;
import co.chatsdk.core.session.ChatSDK;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;

public class GeofireClient {
    private static final String TAG = GeofireClient.class.getSimpleName();

    @Nullable private static GeofireClient mGeofireInstance;
    private String mUserId = "";
    private final GeoFire mGeoFire;
    @Nullable private Location mCurrentLocation = null;
    private final HashMap<String, NearbyUser> mNearHashMap;
    private MutableLiveData<NearbyUser> mNearbyUser;


    private GeofireClient() {
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference userLocationDatabaseReference = mFirebaseDatabase.getReference().child("geofire");
        mGeoFire = new GeoFire(userLocationDatabaseReference);
        mNearHashMap = new HashMap<String, NearbyUser>();
        mNearbyUser = new MutableLiveData<>();
    }

    public static GeofireClient getInstance() {
        if(mGeofireInstance == null){
            mGeofireInstance = new GeofireClient();
        }
        return mGeofireInstance;
    }

    public static void destroyGeofireHelper() {
        mGeofireInstance = null;
    }

    public LiveData<NearbyUser> getNearbyUser(){
        return mNearbyUser;
    }

    public void setUserId(String userId){
        mUserId = userId;
    }
    public void setLocation(@NonNull Location location) {
        mCurrentLocation = location;
        mGeoFire.setLocation(mUserId, new GeoLocation(mCurrentLocation.getLatitude(),
                mCurrentLocation.getLongitude()), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                if (error != null) {
                    Log.e(TAG,"There was an error saving the location to GeoFire: " + error);
                }
            }
        });
    }

    public void searchNearbyUserByRadius(double searchRadius){
        if (mCurrentLocation != null) {
            GeoQuery geoQuery = mGeoFire.queryAtLocation(new GeoLocation(mCurrentLocation.getLatitude(),
                                                         mCurrentLocation.getLongitude()), searchRadius);
            geoQuery.addGeoQueryDataEventListener(new GeoQueryDataEventListener() {
                @Override
                public void onDataEntered(DataSnapshot dataSnapshot, GeoLocation geoLocation) {
                    Location location = new Location("");
                    location.setLatitude(geoLocation.latitude);
                    location.setLongitude(geoLocation.longitude);
                    final DecimalFormat df = new DecimalFormat("#.#");
                    if (mCurrentLocation != null) {
                        final String distanceTo = df.format(mCurrentLocation.distanceTo(location) / 1000.0);
                        User user = ChatSDK.db().fetchOrCreateEntityWithEntityID(User.class, dataSnapshot.getKey());
                        NearbyUser nearbyUser = new NearbyUser(user,distanceTo);
                        mNearbyUser.setValue(nearbyUser);
                        mNearHashMap.put(dataSnapshot.getKey(), nearbyUser);
                    }
                }
                @Override
                public void onDataExited(DataSnapshot dataSnapshot) {
                }

                @Override
                public void onDataMoved(DataSnapshot dataSnapshot, GeoLocation location) {
                }

                @Override
                public void onDataChanged(DataSnapshot dataSnapshot, GeoLocation location) {
                    //Key %s changed within the search area to [%f,%f]", dataSnapshot.getKey(), location.latitude, location.longitude
                }

                @Override
                public void onGeoQueryReady() {
                    //All initial data has been loaded and events have been fired!
                }

                @Override
                public void onGeoQueryError(DatabaseError error) {
                    //There was an error with this query
                }
            });
            } else {
            Log.e(TAG, "Current Location is not set. Nothing to query.");
        }
    }

    public String getDistanceToCurrentUser(String userEntityId) {
        if(mNearHashMap.get(userEntityId)!= null){
            return mNearHashMap.get(userEntityId).getDistance();
        }else{
            Log.e(TAG,"User not available in nearbyusers list");
            return "NA";
        }
    }
}
