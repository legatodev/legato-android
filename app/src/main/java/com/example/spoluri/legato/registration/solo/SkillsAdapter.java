package com.example.spoluri.legato.registration.solo;

import android.content.Context;

import androidx.annotation.NonNull;
import 	androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.example.spoluri.legato.R;

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

    // 2. Override the onCreateViewHolder method
    @NonNull
    @Override
    public SkillsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // 3. Inflate the view and return the new ViewHolder
        View view = LayoutInflater.from(parent.getContext())
                .inflate(this.itemResource, parent, false);
        return new SkillsHolder(this.context, view);
    }

    // 4. Override the onBindViewHolder method
    @Override
    public void onBindViewHolder(@NonNull SkillsHolder holder, int position) {
        // 5. Use position to access the correct NearbyUser object
        Skill skill = this.skills.get(position);

        // 6. Bind the nearbyUser object to the holder
        holder.bindSkill(skill);
    }

    @Override
    public int getItemCount() {
        return this.skills.size();
    }

    public class SkillsHolder extends RecyclerView.ViewHolder implements Spinner.OnItemSelectedListener {

        private final Spinner mSkillSpinner;
        private final SeekBar mSkillLevelSeekBar;

        private final String[] mSkillsArray;

        public SkillsHolder(Context context, View itemView) {
            super(itemView);

            // 1. Set the context
            this.mSkillSpinner = itemView.findViewById(R.id.skillsSpinner1);
            this.mSkillLevelSeekBar = itemView.findViewById(R.id.skillLevelSlider1);
            mSkillsArray = itemView.getResources().getStringArray(R.array.skills_array);
            mSkillSpinner.setOnItemSelectedListener(this);
        }

        public void bindSkill(Skill skill) {
            // 4. Bind the data to the ViewHolder
            this.mSkillLevelSeekBar.setProgress(skill.getSkillLevel());
            this.mSkillSpinner.setSelection(Arrays.asList(mSkillsArray).indexOf(skill.getSkill()));
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            mOnSkillSelectedListener.onSkillSelected(view, position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

        public String getSkill() {
            String skill = "";
            if (!((String)mSkillSpinner.getSelectedItem()).isEmpty()) {
                skill += ((String)mSkillSpinner.getSelectedItem() + " - " + mSkillLevelSeekBar + "|");
            }

            return skill;
        }
    }
}
