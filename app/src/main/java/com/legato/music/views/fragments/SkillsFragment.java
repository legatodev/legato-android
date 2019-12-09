package com.legato.music.views.fragments;

import android.content.res.Resources;
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

import com.legato.music.models.Skill;
import com.legato.music.utils.Keys;
import com.legato.music.R;
import com.legato.music.views.adapters.SkillsAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.chatsdk.core.dao.User;
import co.chatsdk.core.session.ChatSDK;

public class SkillsFragment extends Fragment implements View.OnClickListener, SkillsAdapter.SkillSelectedListener {
    public interface FinishClickedListener{
        void onFinish();
    }

    @BindView(R.id.skillsRecyclerView) RecyclerView mSkillsRecyclerView;
    @BindView(R.id.finishSoloRegistrationButton) Button finishButton;
    @BindView(R.id.addSkillButton) Button addSkillButton;

    @Nullable private SkillsAdapter mSkillsAdapter;
    private ArrayList<Skill> mSkillArrayList = new ArrayList<>();
    private boolean skillSelected;
    private static final int MAX_SKILLS = 6;
    private boolean valid;

    private FinishClickedListener finishClickedListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_skills, container, false);
        ButterKnife.bind(this, view);

        this.finishClickedListener = (FinishClickedListener) getActivity();

        skillSelected = false;
        User user = ChatSDK.currentUser();
        Resources res = getResources();
        populateCurrentSkills(user, res);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (view == null) { return; }

        if(mSkillArrayList.size()==0) {
            Skill skill = new Skill("Choose Skill", 0);
            mSkillArrayList.add(skill);
        }
        mSkillsAdapter = new SkillsAdapter(getContext(), this, R.layout.item_skill, mSkillArrayList);
        mSkillsRecyclerView.setAdapter(mSkillsAdapter);
        DividerItemDecoration itemDecor = new DividerItemDecoration(mSkillsRecyclerView.getContext(), DividerItemDecoration.HORIZONTAL);
        mSkillsRecyclerView.addItemDecoration(itemDecor);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mSkillsRecyclerView.setLayoutManager(layoutManager);

        addSkillButton.setOnClickListener(this);
        addSkillButton.setEnabled(false);

        finishButton.setOnClickListener(this);

        validate();
    }

    private void populateCurrentSkills(User user, Resources res) {
        String[] resSkills = res.getStringArray(R.array.skills_array);
        String currentSkills = user.metaStringForKey(Keys.skills);
        if (currentSkills != null && !currentSkills.isEmpty()) {
            String[] currentSkillsArray = currentSkills.split("\\|", -1);
            for (int i = 0; i < resSkills.length; i++) {
                for (int j = 0; j < currentSkillsArray.length; j++) {
                    if (currentSkillsArray[j].contains(resSkills[i])) {
                        String skillLevel = getSkillLevel(currentSkillsArray[j]);
                        if (!skillLevel.isEmpty()) {
                            String skillString = getSkill(currentSkillsArray[j]);
                            boolean ownsInstrument = getOwnsInstrument(currentSkillsArray[j]);
                            if (!skillString.isEmpty()) {
                                Skill s = new Skill(skillString, Integer.parseInt(skillLevel), ownsInstrument);
                                mSkillArrayList.add(s);
                            }
                        }
                    }
                }
            }
        }
    }

    private String getSkill(String input) {
        Pattern pattern = Pattern.compile("[\\p{L}]+");
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.group(0);
        }

        return "";
    }

    private boolean getOwnsInstrument(String input) {
        Matcher matcher = Pattern.compile("\\((.*?)\\)").matcher(input);
        String ownsInstrumentString = "";
        if (matcher.find()) {
            ownsInstrumentString = matcher.group(1);
            if (!ownsInstrumentString.isEmpty() && ownsInstrumentString.equals("Yes")) {
                return true;
            } else {
                return false;
            }
        }

        return false;
    }

    private String getSkillLevel(String s) {

        return s.replaceAll("[^\\d]", "");
    }

    private void validate() {
        valid = skillSelected;
        finishButton.setEnabled(valid);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addSkillButton:
                if (mSkillsAdapter != null && mSkillArrayList.size() < MAX_SKILLS) {
                    mSkillArrayList.add(new Skill("Choose Skill", 0));
                    mSkillsAdapter.notifyItemInserted(mSkillArrayList.size() - 1);
                } else {
                    addSkillButton.setEnabled(false);
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
        addSkillButton.setEnabled(true);
        validate();
    }

   public String extractData() {
        String skills = "";

        ArrayList<Skill> extractedSkillList = new ArrayList<>();

        if (mSkillsAdapter != null) {
            for (int i = 0; i < mSkillsAdapter.getItemCount(); i++) {
                SkillsAdapter.SkillsHolder holder = (SkillsAdapter.SkillsHolder) mSkillsRecyclerView.findViewHolderForAdapterPosition(i);
                if (holder != null) {
                    Skill s = holder.getSkill();
                    if(s != null)
                        extractedSkillList.add(s);
                }
            }

            if(extractedSkillList.size() > 0){
                Collections.sort(extractedSkillList, Skill.sortBySkillLevel);
                skills = formulateSkillString(extractedSkillList);
            }
        }
        return skills;
   }

    private String formulateSkillString(List<Skill> extractedSkillList){
        String skillString = "";
        for(Skill skill: extractedSkillList){
            String ownsInstrument = "No";
            if (skill.getOwnsInstrument()) {
                ownsInstrument = "Yes";
            }
            skillString += (skill.getSkill() + "(" + ownsInstrument + ")" + " - " + skill.getSkillLevel() + "|");
        }
        return skillString;
    }

    public boolean isInputValid() {
        return valid;
    }
}
