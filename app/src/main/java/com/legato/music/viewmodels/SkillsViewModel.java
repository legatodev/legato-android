package com.legato.music.viewmodels;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.legato.music.models.NearbyUser;
import com.legato.music.models.Skill;
import com.legato.music.repositories.BaseRepository;
import com.legato.music.views.adapters.SkillsAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SkillsViewModel extends ViewModel {
    private static final String TAG = SkillsViewModel.class.getSimpleName();

    private BaseRepository mBaseRepository;
    private NearbyUser nearbyUser;

    private ArrayList<Skill> mSkillArrayList = new ArrayList<>();
    private List<String> mNoInstrumentList;
    private boolean mSkillSelected = false;
    private static final int MAX_SKILLS = 6;

    public SkillsViewModel(BaseRepository baseRepository, List<String> noInstrumentList) {
        mBaseRepository = baseRepository;
        mNoInstrumentList = noInstrumentList;

        nearbyUser = mBaseRepository.getCurrentUser();
    }

    public List<Skill> getSkillsList() {
        if (mSkillArrayList.isEmpty()) {
            mSkillArrayList.add(new Skill("Choose Skill", 0));
        }

        return mSkillArrayList;
    }

    public int getSkillsCount() {
        return mSkillArrayList.size();
    }

    public void populateCurrentSkills(String[] skillsArr) {

        String currentSkills = nearbyUser.getSkills();

        if (!TextUtils.isEmpty(currentSkills)) {
            String[] currentSkillsArray = currentSkills.split("\\|", -1);
            for (int i = 0; i < skillsArr.length; i++) {
                for (int j = 0; j < currentSkillsArray.length; j++) {
                    if (currentSkillsArray[j].contains(skillsArr[i])) {
                        String skillLevel = getSkillLevel(currentSkillsArray[j]);
                        if(Integer.parseInt(skillLevel) < 1) {
                            skillLevel = "1";
                        }
                        if (Integer.parseInt(skillLevel) > 10) {
                            skillLevel = "10";
                        }

                        if (!skillLevel.isEmpty()) {
                            String skillString = getSkill(currentSkillsArray[j]);
                            boolean ownsInstrument = getOwnsInstrument(currentSkillsArray[j]);
                            if (!skillString.isEmpty()) {
                                Skill s = new Skill(skillString, Integer.parseInt(skillLevel), ownsInstrument);
                                addSkill(s);
                            }
                        }
                    }
                }
            }
        } else {
            Log.e(TAG, "Nearby user does not have any skills");
        }
    }

    private String getSkill(String input) {
        Pattern pattern = Pattern.compile("[\\p{L} ]+");
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

    public void addSkill(Skill skill) {
        boolean hasSkill = false;

        for (int i = 0; i < mSkillArrayList.size(); i++) {
            if (mSkillArrayList.get(i).getSkill().equals(skill.getSkill())) {
                hasSkill = true;
            }
        }

        if (!hasSkill) {
            mSkillArrayList.add(skill);
        }
    }

    public void setSkillSelected(boolean state) { mSkillSelected = state; }

    public boolean isSkillSelected() {
        return mSkillSelected;
    }

    public boolean isValidSkillsArray() {
        return mSkillArrayList.size() < MAX_SKILLS;
    }

    public String extractData(
            @Nullable RecyclerView recyclerView,
            @Nullable SkillsAdapter adapter) {

        if (recyclerView == null || adapter == null)
            return "";

        String skills = "";
        ArrayList<Skill> extractedSkillList = new ArrayList<>();

        for (int i = 0; i < adapter.getItemCount(); i++) {
            SkillsAdapter.SkillsHolder holder = (SkillsAdapter.SkillsHolder) recyclerView.findViewHolderForAdapterPosition(i);
            if (holder != null) {
                extractedSkillList.add(holder.getSkill());
            }
        }

        if (extractedSkillList.size() > 0) {
            Collections.sort(extractedSkillList, Skill.sortBySkillLevel);
            skills = formulateSkillString(extractedSkillList);
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

            if(mNoInstrumentList.contains(skill.getSkill())) {
                skillString += (skill.getSkill() + " - " + skill.getSkillLevel() + "|");
            }
            else {
                skillString += (skill.getSkill() + "(" + ownsInstrument + ")" + " - " + skill.getSkillLevel() + "|");
            }
        }
        return skillString;
    }
}
