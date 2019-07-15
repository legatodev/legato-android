package com.example.spoluri.legato.registration.solo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

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

public class SkillsFragment extends Fragment implements View.OnClickListener, SkillsAdapter.SkillSelectedListener {
    public interface FinishClickedListener{
        void onFinish();
    }

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

    private FinishClickedListener finishClickedListener;

    public SkillsFragment(FinishClickedListener finishClickedListener) {
        this.finishClickedListener = finishClickedListener;
    }

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
            mSkillArrayList = new ArrayList<>();
            Skill skill = new Skill("Choose Skill", 0);
            mSkillArrayList.add(skill);
            mSkillsAdapter = new SkillsAdapter(getContext(), this, R.layout.item_skill, mSkillArrayList);
            mSkillsRecyclerView.setAdapter(mSkillsAdapter);
            DividerItemDecoration itemDecor = new DividerItemDecoration(mSkillsRecyclerView.getContext(), DividerItemDecoration.HORIZONTAL);
            mSkillsRecyclerView.addItemDecoration(itemDecor);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());

            mSkillsRecyclerView.setLayoutManager(layoutManager);

            addButton = view.findViewById(R.id.addSkillButton);
            addButton.setOnClickListener(this);
            addButton.setEnabled(false);

            finishButton = view.findViewById(R.id.finishSoloRegistrationButton);
            finishButton.setOnClickListener(this);
        }
    }

    private void validate() {
        boolean valid = skillSelected || rapperCheckBox.isChecked() || vocalsCheckBox.isChecked() || writingCheckBox.isChecked();

        finishButton.setEnabled(valid);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addSkillButton:
                if (mSkillArrayList.size() < MAX_SKILLS) {
                    mSkillArrayList.add(new Skill("Choose Skill", 0));
                    mSkillsAdapter.notifyItemInserted(mSkillArrayList.size() - 1);
                    addButton.setEnabled(false);
                    //TODO: how to remove a skill?
                }

                break;
            case R.id.finishSoloRegistrationButton:
                finishClickedListener.onFinish();
                break;
        }
    }

    @Override
    public void onSkillSelected(View v, int position) {
        //TODO: call this when a skill is chosen.
        skillSelected = true;
        addButton.setEnabled(true);
        validate();
    }

    public String extractData() {
        String skills = "";
        skills += (vocalsCheckBox.isChecked()?"vocals |":"");
        skills += (writingCheckBox.isChecked()?"writing |":"");
        skills += (rapperCheckBox.isChecked()?"rapper |":"");

        for (int i = 0; i < mSkillsAdapter.getItemCount(); i++) {
            SkillsAdapter.SkillsHolder holder = (SkillsAdapter.SkillsHolder)mSkillsRecyclerView.findViewHolderForAdapterPosition(i);
            skills += holder.getSkill();
        }

        return skills;
    }
}
