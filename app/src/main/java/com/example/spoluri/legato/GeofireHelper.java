package com.example.spoluri.legato;

import android.location.Location;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryDataEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GeofireHelper {
    private String mUserId = AppConstants.ANONYMOUS;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserLocationDatabaseReference;
    private GeoFire mGeoFire;
    private Location mCurrentLocation;

    public GeofireHelper() {
        mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUserLocationDatabaseReference = mFirebaseDatabase.getReference().child("geofire");
        mGeoFire = new GeoFire(mUserLocationDatabaseReference);
    }

    public void setLocation(Location location){
        mCurrentLocation = location;
        mGeoFire.setLocation(mUserId, new GeoLocation(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), new GeoFire.CompletionListener() {
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

    public void queryNeighbors(double searchRadius){
        GeoQuery geoQuery = mGeoFire.queryAtLocation(new GeoLocation(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), searchRadius);
        geoQuery.addGeoQueryDataEventListener(new GeoQueryDataEventListener() {
            @Override
            public void onDataEntered(DataSnapshot dataSnapshot, GeoLocation location) {
                //Every point within the radius will call this function including the origin
                System.out.println(String.format("Key %s entered the search area at [%f,%f]", dataSnapshot.getKey(), location.latitude, location.longitude));
            }

            @Override
            public void onDataExited(DataSnapshot dataSnapshot) {
                System.out.println(String.format("Key %s is no longer in the search area", dataSnapshot.getKey()));
            }

            @Override
            public void onDataMoved(DataSnapshot dataSnapshot, GeoLocation location) {
                System.out.println(String.format("Key %s moved within the search area to [%f,%f]", dataSnapshot.getKey(), location.latitude, location.longitude));
            }

            @Override
            public void onDataChanged(DataSnapshot dataSnapshot, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                System.out.println("All initial data has been loaded and events have been fired!");
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                System.err.println("There was an error with this query: " + error);
            }
        });
    }
}
