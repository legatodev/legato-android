package com.legato.music.utils;

import android.location.Location;
import android.util.Log;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryDataEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.HashMap;

import co.chatsdk.core.session.ChatSDK;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;

public class GeofireHelper {
    public interface NearbyUserFoundListener {
        void nearbyUserFound(String userId, String distance);
    }

    private String mUserId = "";
    private final GeoFire mGeoFire;
    @Nullable private Location mCurrentLocation = null;
    private final HashMap<String, String> mNearHashMap; //TODO: maybe use sortedmap in order of distance.

    @Nullable private NearbyUserFoundListener nearbyUserFoundListener;

    private static @Nullable GeofireHelper sGeofireInstance;

    private GeofireHelper(@Nullable NearbyUserFoundListener nearbyUserFoundListener) {
        this.nearbyUserFoundListener = nearbyUserFoundListener;
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference userLocationDatabaseReference = mFirebaseDatabase.getReference().child("geofire");
        mGeoFire = new GeoFire(userLocationDatabaseReference);
        mNearHashMap = new HashMap<String, String>();
    }

     public static GeofireHelper getInstance(String userId, @Nullable NearbyUserFoundListener nearbyUserFoundListener){
        if(sGeofireInstance == null){
            sGeofireInstance = new GeofireHelper(nearbyUserFoundListener);
        }
        else {
            sGeofireInstance.nearbyUserFoundListener = nearbyUserFoundListener;
        }
         sGeofireInstance.mUserId = userId;

        return  sGeofireInstance;
     }

     public static void destroyGeofireHelper() {
        sGeofireInstance = null;
     }

    public void setLocation(@NonNull Location location) {
        mCurrentLocation = location;
        mGeoFire.setLocation(mUserId, new GeoLocation(mCurrentLocation.getLatitude(),
                mCurrentLocation.getLongitude()), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                if (error != null) {
                    System.err.println("There was an error saving the location to GeoFire: " + error);
                } else {
                    System.out.println("Location saved on server successfully!");
                }
            }
        });
    }

    //This function needs a unit test
    public void queryNeighbors(double searchRadius) {
        if (mCurrentLocation != null) {
            GeoQuery geoQuery = mGeoFire.queryAtLocation(new GeoLocation(mCurrentLocation.getLatitude(),
                    mCurrentLocation.getLongitude()), searchRadius);
            geoQuery.addGeoQueryDataEventListener(new GeoQueryDataEventListener() {
                @Override
                public void onDataEntered(DataSnapshot dataSnapshot, GeoLocation geoLocation) {
                    if (!dataSnapshot.getKey().equals(mUserId)) {
                        //Every point within the radius will call this function including the origin
                        Location location = new Location("");
                        location.setLatitude(geoLocation.latitude);
                        location.setLongitude(geoLocation.longitude);
                        final DecimalFormat df = new DecimalFormat("#.#");
                        if (mCurrentLocation != null) {
                            final String distanceTo = df.format(mCurrentLocation.distanceTo(location) / 1000.0);
                            if (nearbyUserFoundListener != null) {
                                nearbyUserFoundListener.nearbyUserFound(dataSnapshot.getKey(), distanceTo);
                            }
                            mNearHashMap.put(dataSnapshot.getKey(), distanceTo);
                        }
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
        }
        else {
            Log.e("GeofireHelper", "Current Location is not set. Nothing to query.");
        }
    }

    public HashMap<String, String> getNearbyUsers() {
        //Only return the list of user ids and in NearbyUser
        return mNearHashMap;
    }

    public String getDistanceToCurrentUser(String userEntityId) {
        return mNearHashMap.get(userEntityId) != null?mNearHashMap.get(userEntityId):"error";
    }
}
