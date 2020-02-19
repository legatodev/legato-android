package com.legato.music.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.legato.music.R;
import com.legato.music.models.Skill;
import com.legato.music.repositories.BaseRepository;
import com.legato.music.viewmodels.SkillsViewModel;
import com.legato.music.viewmodels.SkillsViewModelFactory;
import com.legato.music.views.adapters.SkillsAdapter;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SkillsFragment extends Fragment implements View.OnClickListener, SkillsAdapter.SkillSelectedListener {
    public interface FinishClickedListener{
        void onFinish();
    }

    @BindView(R.id.skillsRecyclerView) RecyclerView mSkillsRecyclerView;
    @BindView(R.id.finishSoloRegistrationButton) Button finishButton;
    @BindView(R.id.addSkillButton) Button addSkillButton;

    @Nullable private SkillsAdapter mSkillsAdapter;

    @NonNull private SkillsViewModel skillsViewModel;

    private FinishClickedListener finishClickedListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_skills, container, false);
        ButterKnife.bind(this, view);

        this.finishClickedListener = (FinishClickedListener) getActivity();

        skillsViewModel = getViewModel();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        populateCurrentSkills();

        mSkillsAdapter = new SkillsAdapter(
                getContext(),
                this,
                R.layout.item_skill,
                skillsViewModel.getSkillsList());
        mSkillsRecyclerView.setAdapter(mSkillsAdapter);

        DividerItemDecoration itemDecor = new DividerItemDecoration(
                mSkillsRecyclerView.getContext(),
                DividerItemDecoration.HORIZONTAL);
        mSkillsRecyclerView.addItemDecoration(itemDecor);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mSkillsRecyclerView.setLayoutManager(layoutManager);

        addSkillButton.setOnClickListener(this);
        addSkillButton.setEnabled(false);

        finishButton.setOnClickListener(this);

        validate();
    }

    private void populateCurrentSkills() {
        String[] resSkills = getResources().getStringArray(R.array.skills_array);

        skillsViewModel.populateCurrentSkills(resSkills);
    }

    private void validate() {
        finishButton.setEnabled(skillsViewModel.isSkillSelected());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addSkillButton:
                if (mSkillsAdapter != null && skillsViewModel.isValidSkillsArray()) {
                    skillsViewModel.addSkill(new Skill(getContext().getResources().getString(R.string.default_skill), 0));
                    mSkillsAdapter.notifyItemInserted(skillsViewModel.getSkillsCount() - 1);
                } else {
                    addSkillButton.setEnabled(false);
                }

                break;
            case R.id.finishSoloRegistrationButton:
                /*
                  Below alert will pop only when All skills are deleted except default skill
                  i.e. 'Choose Skill'
                */
                if(skillsViewModel.getSkillsCount() == 1 &&
                   (skillsViewModel.getSkillsList().get(0).getSkill() ==
                    getContext().getResources().getString(R.string.default_skill)
                   )){
                        new AlertDialog.Builder(getContext())
                            .setMessage(getContext().getResources().getString(R.string.alert_select_one_skill))
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.ok, null)
                            .show();
                }else {
                    finishClickedListener.onFinish();
                }
                break;
        }
    }

    @Override
    public void onSkillSelected(View v, int position) {
        skillsViewModel.setSkillSelected(true);

        addSkillButton.setEnabled(skillsViewModel.isSkillSelected());
        validate();
    }

    @Override
    public void onSkillDeleteValidate(View v, int position) {
        skillsViewModel.setSkillSelected(false);
        validate();
    }

    public String extractData() {
        return skillsViewModel.extractData(mSkillsRecyclerView, mSkillsAdapter);
    }

    @NonNull
    private SkillsViewModel getViewModel() {
        BaseRepository baseRepository = BaseRepository.getInstance();
        ViewModelProvider.Factory factory = new SkillsViewModelFactory(
                baseRepository,
                Arrays.asList(getResources().getStringArray(R.array.no_instrument)));
        return ViewModelProviders.of(requireActivity(), factory).get(SkillsViewModel.class);
    }
}
