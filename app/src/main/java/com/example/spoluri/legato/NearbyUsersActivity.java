package com.example.spoluri.legato;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

public class NearbyUsersActivity extends AppCompatActivity {

    private SearchView mSearchSkills;
    private NearbyUsersAdapter mNearbyUsersAdapter;
    private RecyclerView mNearbyUserRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_users);

        mSearchSkills = findViewById(R.id.searchView);
        mSearchSkills.setActivated(true);
        mSearchSkills.setIconified(false);
        mSearchSkills.clearFocus();
        mSearchSkills.onActionViewExpanded();
        mSearchSkills.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

        mNearbyUserRecyclerView = findViewById(R.id.nearbyUserRecylerView);

        Intent intent = getIntent();
        ArrayList<NearbyUser> nearbyUserList = (ArrayList<NearbyUser>) intent.getSerializableExtra("nearby_users");
        mNearbyUsersAdapter = new NearbyUsersAdapter(this, R.layout.item_nearbyuser, nearbyUserList);
        mNearbyUserRecyclerView.setAdapter(mNearbyUsersAdapter);
        DividerItemDecoration itemDecor = new DividerItemDecoration(mNearbyUserRecyclerView.getContext(), DividerItemDecoration.HORIZONTAL);
        mNearbyUserRecyclerView.addItemDecoration(itemDecor);

        // 4. Initialize ItemAnimator, LayoutManager and ItemDecorators
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        // 7. Set the LayoutManager
        mNearbyUserRecyclerView.setLayoutManager(layoutManager);

        Spinner spinner = findViewById(R.id.lookingForSpinner);
        // Creating ArrayAdapter using the string array and default spinner layout
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.looking_for, android.R.layout.simple_spinner_item);
        // Specify layout to be used when list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Applying the adapter to our spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mNearbyUsersAdapter.getFilter().filter(adapter.getItem(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
