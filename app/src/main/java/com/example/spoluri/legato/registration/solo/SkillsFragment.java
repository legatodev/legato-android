package com.example.spoluri.legato.registration.solo;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.example.spoluri.legato.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;

public class SkillsFragment extends Fragment implements View.OnClickListener, SkillsAdapter.SkillSelectedListener {
    @BindView(R.id.skillsRecyclerView)
    RecyclerView mSkillsRecyclerView;
    @BindView(R.id.rapperCheckBox)
    CheckBox rapperCheckBox;
    @BindView(R.id.vocalsCheckBox)
    CheckBox vocalsCheckBox;
    @BindView(R.id.writingCheckBox)
    CheckBox writingCheckBox;
    private SkillsAdapter mSkillsAdapter;
    private ArrayList<Skill> mSkillArrayList;
    private boolean skillSelected;
    private Button finishButton;
    private static final int MAX_SKILLS = 6;
    private FloatingActionButton addButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_skills, container, false);
        ButterKnife.bind(this, view);
        skillSelected = false;

        rapperCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                validate();
            }
        });

        vocalsCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                validate();
            }
        });

        writingCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                validate();
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();

        if (view != null) {
            // Initialize message ListView and its adapter
            mSkillArrayList = new ArrayList<>();
            Skill skill = new Skill("Choose Skill", 0);
            mSkillArrayList.add(skill);
            mSkillsAdapter = new SkillsAdapter(view.getContext(), this, R.layout.item_skill, mSkillArrayList);
            mSkillsRecyclerView.setAdapter(mSkillsAdapter);
            DividerItemDecoration itemDecor = new DividerItemDecoration(mSkillsRecyclerView.getContext(), DividerItemDecoration.HORIZONTAL);
            mSkillsRecyclerView.addItemDecoration(itemDecor);

            // 4. Initialize ItemAnimator, LayoutManager and ItemDecorators
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());

            // 7. Set the LayoutManager
            mSkillsRecyclerView.setLayoutManager(layoutManager);

            addButton = view.findViewById(R.id.addSkillButton);
            addButton.setOnClickListener(this);
            addButton.setEnabled(false);

            finishButton = view.findViewById(R.id.finishSoloRegistrationButton);
            finishButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    submitForm();
                }
            });
        }
    }

    public void validate() {
        //TODO: atleast one valid skill entry is provided
        boolean valid = skillSelected || rapperCheckBox.isChecked() || vocalsCheckBox.isChecked() || writingCheckBox.isChecked();

        finishButton.setEnabled(valid);
    }

    private void submitForm() {
    }

    @Override
    public void onClick(View view) {
        if (mSkillArrayList.size() < MAX_SKILLS) {
            mSkillArrayList.add(new Skill("Choose Skill", 0));
            mSkillsAdapter.notifyItemInserted(mSkillArrayList.size() - 1);
            addButton.setEnabled(false);
            //TODO: how to remove a skill?
        }
    }

    @Override
    public void onSkillSelected(View v, int position) {
        //TODO: call this when a skill is chosen.
        skillSelected = true;
        addButton.setEnabled(true);
        validate();
    }
}
