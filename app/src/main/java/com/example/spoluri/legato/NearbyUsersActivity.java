package com.example.spoluri.legato;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NearbyUsersActivity extends AppCompatActivity {

    @BindView(R.id.searchView)
    SearchView mSearchSkills;

    @BindView(R.id.nearbyUserRecylerView)
    RecyclerView mNearbyUserRecyclerView;

    @BindView(R.id.lookingForSpinner)
    Spinner spinner;

    private NearbyUsersAdapter mNearbyUsersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_users);
        ButterKnife.bind(this);

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

        Intent intent = getIntent();
        mNearbyUsersAdapter = new NearbyUsersAdapter(this, R.layout.item_nearbyuser);
        mNearbyUserRecyclerView.setAdapter(mNearbyUsersAdapter);
        DividerItemDecoration itemDecor = new DividerItemDecoration(mNearbyUserRecyclerView.getContext(), DividerItemDecoration.HORIZONTAL);
        mNearbyUserRecyclerView.addItemDecoration(itemDecor);

        // 4. Initialize ItemAnimator, LayoutManager and ItemDecorators
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        // 7. Set the LayoutManager
        mNearbyUserRecyclerView.setLayoutManager(layoutManager);

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
