package com.example.spoluri.legato.registration.solo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.spoluri.legato.R;

import java.util.ArrayList;

public class SkillsFragment extends Fragment implements View.OnClickListener {

    private SkillsAdapter mSkillsAdapter;
    private RecyclerView mSkillsRecyclerView;
    private ArrayList<Skills> mSkillsArrayList;
    private static final int MAX_SKILLS = 6;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_skills, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();

        if (view != null) {
            // Initialize references to views
            mSkillsRecyclerView = view.findViewById(R.id.skillsRecyclerView);

            // Initialize message ListView and its adapter
            mSkillsArrayList = new ArrayList<>();
            Skills skill = new Skills("Choose Skill", 0);
            mSkillsArrayList.add(skill);
            mSkillsAdapter = new SkillsAdapter(view.getContext(), R.layout.item_skills, mSkillsArrayList);
            mSkillsRecyclerView.setAdapter(mSkillsAdapter);
            DividerItemDecoration itemDecor = new DividerItemDecoration(mSkillsRecyclerView.getContext(), DividerItemDecoration.HORIZONTAL);
            mSkillsRecyclerView.addItemDecoration(itemDecor);

            // 4. Initialize ItemAnimator, LayoutManager and ItemDecorators
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());

            // 7. Set the LayoutManager
            mSkillsRecyclerView.setLayoutManager(layoutManager);

            FloatingActionButton addButton = view.findViewById(R.id.addSkillButton);
            addButton.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        if (mSkillsArrayList.size() < MAX_SKILLS) {
            mSkillsArrayList.add(new Skills("Choose Skill", 0));
            mSkillsAdapter.notifyDataSetChanged();
        }
    }
}
