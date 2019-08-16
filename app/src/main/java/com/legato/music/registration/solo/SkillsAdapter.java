package com.legato.music.registration.solo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.legato.music.R;
import com.legato.music.SearchableSpinner;

import java.util.Arrays;
import java.util.List;

class SkillsAdapter extends RecyclerView.Adapter<SkillsAdapter.SkillsHolder>{

    public interface SkillSelectedListener{
        void onSkillSelected(View v, int position);
    }

    private final List<Skill> skills;
    private final Context context;
    private final int itemResource;
    private final SkillSelectedListener mOnSkillSelectedListener;

    public SkillsAdapter(Context context, SkillSelectedListener onSkillSelectedListener, int itemResource, List<Skill> skills) {
        this.skills = skills;
        this.context = context;
        this.itemResource = itemResource;
        this.mOnSkillSelectedListener = onSkillSelectedListener;
    }

    @NonNull
    @Override
    public SkillsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(this.itemResource, parent, false);
        return new SkillsHolder(this.context, view);
    }

    @Override
    public void onBindViewHolder(@NonNull SkillsHolder holder, int position) {
        Skill skill = this.skills.get(position);
        holder.bindSkill(skill);
    }

    @Override
    public int getItemCount() {
        return this.skills.size();
    }

    public class SkillsHolder extends RecyclerView.ViewHolder implements Spinner.OnItemSelectedListener {

        private final SearchableSpinner mSkillSpinner;
        private final SeekBar mSkillLevelSeekBar;
        private TextView mSkillLevelValueTextView;

        private final String[] mSkillsArray;

        public SkillsHolder(Context context, View itemView) {
            super(itemView);

            mSkillSpinner = itemView.findViewById(R.id.skillsSpinner1);
            mSkillLevelValueTextView = itemView.findViewById(R.id.skillLevelValueLabel1);
            mSkillLevelSeekBar = itemView.findViewById(R.id.skillLevelSlider1);
            mSkillLevelSeekBar.setEnabled(false);
            mSkillLevelSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    mSkillLevelValueTextView.setText(progress+"");
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    //write custom code to on start progress
                }
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            mSkillsArray = itemView.getResources().getStringArray(R.array.skills_array);
            mSkillSpinner.setOnItemSelectedListener(this);
        }

        public void bindSkill(Skill skill) {
            this.mSkillLevelSeekBar.setProgress(skill.getSkillLevel());
            int indexOfSkill = Arrays.asList(mSkillsArray).indexOf(skill.getSkill());
            this.mSkillSpinner.setSelectionM(indexOfSkill);
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            mOnSkillSelectedListener.onSkillSelected(view, position);
            mSkillLevelSeekBar.setEnabled(true);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

        public String getSkill() {
            String skill = "";
            if(mSkillSpinner.getSelectedItem() != null){
                if (!((String)mSkillSpinner.getSelectedItem()).isEmpty()) {
                    skill += (mSkillSpinner.getSelectedItem() + " - " + mSkillLevelSeekBar.getProgress() + "|");
                }
            }

            return skill;
        }
    }
}