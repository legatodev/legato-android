package com.example.spoluri.legato;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class NearbyUsersActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private NearbyUsersAdapter mNearbyUsersAdapter;
    private ListView mNearbyUserListView;
    private ArrayList<NearbyUsersCreator> mNearbyUserList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_users);

        // Initialize references to views
        mNearbyUserListView = findViewById(R.id.nearbyUserListView);
        mNearbyUserListView.setOnItemClickListener(this);

        // Initialize message ListView and its adapter
        mNearbyUserList = (ArrayList<NearbyUsersCreator>) getIntent().getSerializableExtra("nearby_users");
        mNearbyUsersAdapter = new NearbyUsersAdapter(this, R.layout.item_nearbyuser, mNearbyUserList);
        mNearbyUserListView.setAdapter(mNearbyUsersAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //Open profile activity
        NearbyUsersCreator nearbyUser = mNearbyUsersAdapter.getItem(i);
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("nearby_user", nearbyUser);
        startActivity(intent);
    }
}
