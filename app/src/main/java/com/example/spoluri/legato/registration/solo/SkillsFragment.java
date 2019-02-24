package com.example.spoluri.legato.registration.solo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.example.spoluri.legato.R;

import java.util.ArrayList;

public class SkillsFragment extends Fragment implements View.OnClickListener {

    private SkillsAdapter mSkillsAdapter;
    private RecyclerView mSkillsListView;
    private ArrayList<Skills> mSkillsArrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_skills, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();

        // Initialize references to views
        mSkillsListView = (RecyclerView) view.findViewById(R.id.skillsRecyclerView);

        // Initialize message ListView and its adapter

        mSkillsArrayList = new ArrayList<Skills>();
        Skills skill = new Skills("Guitar", 5);
        mSkillsArrayList.add(skill);
        mSkillsAdapter = new SkillsAdapter(view.getContext(), R.layout.item_skills, mSkillsArrayList);
        mSkillsListView.setAdapter(mSkillsAdapter);
        DividerItemDecoration itemDecor = new DividerItemDecoration(mSkillsListView.getContext(), DividerItemDecoration.HORIZONTAL);
        mSkillsListView.addItemDecoration(itemDecor);

        // 4. Initialize ItemAnimator, LayoutManager and ItemDecorators
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());

        // 7. Set the LayoutManager
        mSkillsListView.setLayoutManager(layoutManager);

        FloatingActionButton addButton = (FloatingActionButton) view.findViewById(R.id.addSkillButton);
        addButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        mSkillsArrayList.add(new Skills("Piano", 10));
        mSkillsAdapter.notifyDataSetChanged();
    }
}
