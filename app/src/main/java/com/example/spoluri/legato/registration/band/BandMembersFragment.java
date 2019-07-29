package com.example.spoluri.legato.registration.band;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spoluri.legato.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BandMembersFragment extends Fragment implements View.OnClickListener {
    @BindView(R.id.bandMembersRecyclerView)
    RecyclerView mBandMembersRecyclerView;
    private BandMembersAdapter mBandMembersAdapter;
    private ArrayList<BandMember> mBandMemberArrayList;

    private Button finishButton;
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

        if (view != null) {
            // Initialize message ListView and its adapter
            mBandMemberArrayList = new ArrayList<>();
            BandMember bandMember = new BandMember("Choose Skill", "");
            mBandMemberArrayList.add(bandMember);
            mBandMembersAdapter = new BandMembersAdapter(view.getContext(), R.layout.item_band_member, mBandMemberArrayList);
            mBandMembersRecyclerView.setAdapter(mBandMembersAdapter);
            DividerItemDecoration itemDecor = new DividerItemDecoration(mBandMembersRecyclerView.getContext(), DividerItemDecoration.HORIZONTAL);
            mBandMembersRecyclerView.addItemDecoration(itemDecor);

            // 4. Initialize ItemAnimator, LayoutManager and ItemDecorators
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());

            // 7. Set the LayoutManager
            mBandMembersRecyclerView.setLayoutManager(layoutManager);

            FloatingActionButton addButton = view.findViewById(R.id.addBandMemberButton);
            addButton.setOnClickListener(this);

            finishButton = view.findViewById(R.id.finishBandRegistrationButton);
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
        if (mBandMemberArrayList.size() < MAX_SKILLS) {
            mBandMemberArrayList.add(new BandMember("Choose Skill", ""));
            mBandMembersAdapter.notifyItemInserted(mBandMemberArrayList.size() - 1); //TODO: We do't need to refresh the whole adapter and have it refreshed. Only add.
            //TODO: how to remove a skill?
        }
    }
}
