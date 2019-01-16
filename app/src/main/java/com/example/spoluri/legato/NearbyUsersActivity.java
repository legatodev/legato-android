package com.example.spoluri.legato;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;

import java.util.ArrayList;

public class NearbyUsersActivity extends AppCompatActivity {

    private NearbyUsersAdapter mNearbyUsersAdapter;
    private RecyclerView mNearbyUserListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_users);

        // Initialize references to views
        mNearbyUserListView = (RecyclerView)findViewById(R.id.nearbyUserListView);

        // Initialize message ListView and its adapter
        Intent intent = getIntent();
        ArrayList<NearbyUser> nearbyUserList = (ArrayList<NearbyUser>) intent.getSerializableExtra("nearby_users");
        mNearbyUsersAdapter = new NearbyUsersAdapter(this, R.layout.item_nearbyuser, nearbyUserList);
        mNearbyUserListView.setAdapter(mNearbyUsersAdapter);

        // 4. Initialize ItemAnimator, LayoutManager and ItemDecorators
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        // 7. Set the LayoutManager
        mNearbyUserListView.setLayoutManager(layoutManager);
    }
}
