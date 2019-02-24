package com.example.spoluri.legato;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

public class NearbyUsersActivity extends AppCompatActivity {

    private SearchView searchSkills;
    private NearbyUsersAdapter mNearbyUsersAdapter;
    private RecyclerView mNearbyUserListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_users);

        searchSkills = findViewById(R.id.searchView);
        searchSkills.setActivated(true);
        searchSkills.setIconified(false);
        searchSkills.clearFocus();
        searchSkills.onActionViewExpanded();
        searchSkills.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                mNearbyUsersAdapter.getFilter().filter(newText);

                return false;
            }
        });

        // Initialize references to views
        mNearbyUserListView = findViewById(R.id.nearbyUserListView);

        // Initialize message ListView and its adapter

        Intent intent = getIntent();
        ArrayList<NearbyUser> nearbyUserList = (ArrayList<NearbyUser>) intent.getSerializableExtra("nearby_users");
        mNearbyUsersAdapter = new NearbyUsersAdapter(this, R.layout.item_nearbyuser, nearbyUserList);
        mNearbyUserListView.setAdapter(mNearbyUsersAdapter);
        DividerItemDecoration itemDecor = new DividerItemDecoration(mNearbyUserListView.getContext(), DividerItemDecoration.HORIZONTAL);
        mNearbyUserListView.addItemDecoration(itemDecor);

        // 4. Initialize ItemAnimator, LayoutManager and ItemDecorators
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        // 7. Set the LayoutManager
        mNearbyUserListView.setLayoutManager(layoutManager);

        Spinner spinner = findViewById(R.id.lookingForSpinner);
        // Creating ArrayAdapter using the string array and default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.looking_for, android.R.layout.simple_spinner_item);
        // Specify layout to be used when list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Applying the adapter to our spinner
        spinner.setAdapter(adapter);
    }
}
