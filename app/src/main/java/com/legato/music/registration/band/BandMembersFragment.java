package com.legato.music.registration.band;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.legato.music.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BandMembersFragment extends Fragment implements View.OnClickListener {
    @BindView(R.id.bandMembersRecyclerView)
    @Nullable RecyclerView mBandMembersRecyclerView;
    @BindView(R.id.finishBandRegistrationButton)
    @Nullable Button finishButton;
    @BindView(R.id.addBandMemberButton)
    @Nullable Button addBandMember;
    @Nullable private BandMembersAdapter mBandMembersAdapter;
    private ArrayList<BandMember> mBandMemberArrayList = new ArrayList<>();

    private static final int MAX_SKILLS = 6;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_band_members, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();

        if (view == null) { return; }
        // Initialize message ListView and its adapter
        BandMember bandMember = new BandMember("Choose Skill", "");
        mBandMemberArrayList.add(bandMember);
        mBandMembersAdapter = new BandMembersAdapter(view.getContext(), R.layout.item_band_member, mBandMemberArrayList);
        if (mBandMembersRecyclerView != null) {
            mBandMembersRecyclerView.setAdapter(mBandMembersAdapter);
            DividerItemDecoration itemDecor = new DividerItemDecoration(mBandMembersRecyclerView.getContext(), DividerItemDecoration.HORIZONTAL);
            mBandMembersRecyclerView.addItemDecoration(itemDecor);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
            mBandMembersRecyclerView.setLayoutManager(layoutManager);
        }

        if (addBandMember != null)
            addBandMember.setOnClickListener(this);

        if (finishButton != null) {
            finishButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    submitForm();
                }
            });
        }
    }

    private void submitForm() {
    }

    @Override
    public void onClick(View view) {
        if (mBandMembersAdapter != null && mBandMemberArrayList.size() < MAX_SKILLS) {
            mBandMemberArrayList.add(new BandMember("Choose Skill", ""));
            mBandMembersAdapter.notifyItemInserted(mBandMemberArrayList.size() - 1); //TODO: We do't need to refresh the whole adapter and have it refreshed. Only add.
            //TODO: how to remove a skill?
        }
    }
}
