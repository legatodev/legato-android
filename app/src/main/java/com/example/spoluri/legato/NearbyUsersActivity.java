package com.example.spoluri.legato;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spoluri.legato.messaging.ActiveChatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NearbyUsersActivity extends AppCompatActivity implements FilterDialogFragment.FilterListener  {

    @BindView(R.id.nearbyUserRecylerView)
    RecyclerView mNearbyUserRecyclerView;

    private NearbyUsersAdapter mNearbyUsersAdapter;
    private FilterDialogFragment mFilterDialog;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_users);
        ButterKnife.bind(this);
        context = this;
        mNearbyUsersAdapter = new NearbyUsersAdapter(this, R.layout.item_nearbyuser);
        mNearbyUserRecyclerView.setAdapter(mNearbyUsersAdapter);
        mFilterDialog = new FilterDialogFragment(mNearbyUsersAdapter);

        DividerItemDecoration itemDecor = new DividerItemDecoration(mNearbyUserRecyclerView.getContext(), DividerItemDecoration.HORIZONTAL);
        mNearbyUserRecyclerView.addItemDecoration(itemDecor);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mNearbyUserRecyclerView.setLayoutManager(layoutManager);
    }

    private void setupActionBar() {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_actionbar);
        View view = getSupportActionBar().getCustomView();

        ImageButton imageButton= view.findViewById(R.id.settings);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AccountActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onFilter(Filters filters) {
        mNearbyUsersAdapter.onFilter(filters);
    }

    @OnClick(R.id.filterBar)
    public void onFilterClicked() {
        // Show the dialog containing filter options
        mFilterDialog.show(getSupportFragmentManager(), FilterDialogFragment.TAG);
    }

    @OnClick(R.id.buttonActiveChats)
    public void onActiveChatsClicked() {
        Intent intent = new Intent(this, ActiveChatActivity.class);
        startActivity(intent);
    }
}
