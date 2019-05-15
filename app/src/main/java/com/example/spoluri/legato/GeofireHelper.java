package com.example.spoluri.legato;

import android.location.Location;
import android.util.Log;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryDataEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

class GeofireHelper {
    private final String mUserId;
    private final GeoFire mGeoFire;
    private Location mCurrentLocation;
    private ArrayList<NearbyUser> mNearbyUsersList;
    private final FirebaseDatabase mFirebaseDatabase;
    private final HashMap<String,NearbyUser> mNearHashMap;

    private static GeofireHelper sGeofireInstance;

    private GeofireHelper() {
        mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference userLocationDatabaseReference = mFirebaseDatabase.getReference().child("geofire");
        mGeoFire = new GeoFire(userLocationDatabaseReference);
        mNearbyUsersList = new ArrayList<>();
        mNearHashMap = new HashMap<String,NearbyUser>();
    }
     public static GeofireHelper getInstance(){
        if(sGeofireInstance == null){
            sGeofireInstance = new GeofireHelper();
        }

        return  sGeofireInstance;
     }

    public void setLocation(Location location) {
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
                    //Every point within the radius will call this function including the origin
                    Location location = new Location("");
                    location.setLatitude(geoLocation.latitude);
                    location.setLongitude(geoLocation.longitude);
                    final DecimalFormat df = new DecimalFormat("#.#");
                    final String distanceTo = df.format(mCurrentLocation.distanceTo(location) / 1000.0);
                    if (!dataSnapshot.getKey().equals(mUserId)) {
                        DatabaseReference userProfileDataDatabaseReference = mFirebaseDatabase.getReference().child("userprofiledata").child(dataSnapshot.getKey());
                        userProfileDataDatabaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                UserProfileData userProfileData = dataSnapshot.getValue(UserProfileData.class);
                                NearbyUser user = new NearbyUser(userProfileData,distanceTo);
                                String id = dataSnapshot.getKey();
                                mNearHashMap.put(id,user);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }

                @Override
                public void onDataExited(DataSnapshot dataSnapshot) {
                    System.out.println(String.format("Key %s is no longer in the search area", dataSnapshot.getKey()));
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

    public ArrayList<NearbyUser> getNearbyUsersList() {
        mNearbyUsersList = null;
        mNearbyUsersList = new ArrayList<NearbyUser>(mNearHashMap.values());
        return mNearbyUsersList;
    }
}
