package com.legato.music.registration.solo;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.legato.music.Keys;
import com.legato.music.R;

import java.util.ArrayList;
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

    @BindView(R.id.skillsRecyclerView)
    @Nullable RecyclerView mSkillsRecyclerView;
    @BindView(R.id.finishSoloRegistrationButton)
    @Nullable Button finishButton;
    @BindView(R.id.addSkillButton)
    @Nullable Button addSkillButton;
    @Nullable private SkillsAdapter mSkillsAdapter;
    private ArrayList<Skill> mSkillArrayList = new ArrayList<>();
    private boolean skillSelected;
    private static final int MAX_SKILLS = 6;
    private boolean valid;

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
        User user = ChatSDK.currentUser();
        Resources res = getResources();

        /*Creating pre-selected skill objects*/
        String[] resSkills = res.getStringArray(R.array.skills_array);
        String dblookingfor = user.metaStringForKey(Keys.skills);
        if (dblookingfor != null && !dblookingfor.isEmpty()) {
            String[] dbskillsArray = dblookingfor.split("\\|", -1);
            for (int i = 0; i < resSkills.length; i++) {
                for (int j = 0; j < dbskillsArray.length; j++) {
                    if (dbskillsArray[j].contains(resSkills[i])) {
                        String skillLevel = dbskillsArray[j].replaceAll("[^\\d]", "");
                        if (!skillLevel.isEmpty()) {
                            Pattern pattern = Pattern.compile("[\\p{L}]+");
                            Matcher matcher = pattern.matcher(dbskillsArray[j]);
                            if (matcher.find()) {
                                Skill s = new Skill(matcher.group(0), Integer.parseInt(skillLevel));
                                mSkillArrayList.add(s);
                            }
                        }
                    }
                }
            }
        }

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //TODO: Why does this need to be in onActivityCreated? Why can't it be in OnCreateView?
        super.onActivityCreated(savedInstanceState);
        View view = getView();

        if (view == null) { return; }

        if(mSkillArrayList.size()==0) {
            Skill skill = new Skill("Choose Skill", 0);
            mSkillArrayList.add(skill);
        }
        mSkillsAdapter = new SkillsAdapter(getContext(), this, R.layout.item_skill, mSkillArrayList);
        if (mSkillsRecyclerView != null) {
            mSkillsRecyclerView.setAdapter(mSkillsAdapter);
            DividerItemDecoration itemDecor = new DividerItemDecoration(mSkillsRecyclerView.getContext(), DividerItemDecoration.HORIZONTAL);
            mSkillsRecyclerView.addItemDecoration(itemDecor);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            mSkillsRecyclerView.setLayoutManager(layoutManager);
        }

        if (addSkillButton != null) {
            addSkillButton.setOnClickListener(this);
            addSkillButton.setEnabled(false);
        }

        if (finishButton != null)
            finishButton.setOnClickListener(this);

        validate();
    }

    private void validate() {
        valid = skillSelected;
        if (finishButton != null)
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
                    if (addSkillButton != null)
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
        if (addSkillButton != null)
            addSkillButton.setEnabled(true);
        validate();
    }

    public String extractData() {
        String skills = "";

        if (mSkillsAdapter != null && mSkillsRecyclerView != null) {
            for (int i = 0; i < mSkillsAdapter.getItemCount(); i++) {
                SkillsAdapter.SkillsHolder holder = (SkillsAdapter.SkillsHolder) mSkillsRecyclerView.findViewHolderForAdapterPosition(i);
                if (holder != null) {
                    skills += holder.getSkill();
                }
            }
        }

        return skills;
    }

    public boolean isInputValid() {
        return valid;
    }
}
