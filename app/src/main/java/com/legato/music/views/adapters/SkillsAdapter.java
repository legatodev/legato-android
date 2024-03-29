package com.legato.music.views.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;

import com.legato.music.R;
import com.legato.music.models.Skill;
import com.legato.music.utils.SearchableSpinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SkillsAdapter extends RecyclerView.Adapter<SkillsAdapter.SkillsHolder> {

    public interface SkillSelectedListener {
        void onSkillSelected(View v, int position);
        void onSkillDeleteValidate(View v, int position);
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

        // Since we save the skills as a range [1,10], we need to subtract 1 to match the
        // seek bar progress range limited to [0,9]
        skill.setSkillLevel(skill.getSkillLevel() - 1);

        holder.bindSkill(skill);
    }

    @Override
    public int getItemCount() {

        return this.skills.size();
    }

    public class SkillsHolder extends RecyclerView.ViewHolder implements Spinner.OnItemSelectedListener, View.OnClickListener {

        @BindView(R.id.skillsSpinner1)
        SearchableSpinner mSkillSpinner;
        @BindView(R.id.skillLevelSlider1)
        SeekBar mSkillLevelSeekBar;
        @BindView(R.id.skillLevelValueLabel1)
        TextView mSkillLevelValueTextView;
        @BindView(R.id.deleteSkill)
        ImageButton mDeleteSkillButton;
        @BindView(R.id.ownsInstrumentSwitch)
        Switch mOwnsInstrumentSwitch;

        @BindArray(R.array.skills_array)
        String[] mSkillsArray;
        @BindArray(R.array.no_instrument)
        String[] no_instrument;
        private List<String> excludeInstrumentOwned;

        public SkillsHolder(Context context, View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            if (mSkillLevelSeekBar != null) {
                mSkillLevelSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        // The SeekBar progress range is limited to [0,9],
                        // so we need to add 1 to the view so the user sees [1,10]
                        progress += 1;

                        if (mSkillLevelValueTextView != null) {
                            mSkillLevelValueTextView.setText(progress + "");
                        }

                        skills.get(getAdapterPosition()).setSkillLevel(progress);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        //write custom code to on start progress
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });
                mSkillLevelSeekBar.setEnabled(false);
            }

            if (mDeleteSkillButton != null) {
                mDeleteSkillButton.setOnClickListener(this);
            }

            if (mSkillSpinner != null) {
                mSkillSpinner.setOnItemSelectedListener(this);
            }

            this.excludeInstrumentOwned = Arrays.asList(no_instrument);
        }

        public void bindSkill(Skill skill) {
            if (skill != null) {
                if (mSkillLevelSeekBar != null) {
                    this.mSkillLevelSeekBar.setEnabled(skill.getSkillLevel() > 0);
                    this.mSkillLevelSeekBar.setProgress(skill.getSkillLevel());
                }

                int indexOfSkill = Arrays.asList(mSkillsArray).indexOf(skill.getSkill());
                if (mSkillSpinner != null) {
                    this.mSkillSpinner.setSelectionM(indexOfSkill);
                }

                if (excludeInstrumentOwned.contains(skill.getSkill())) {
                    this.mOwnsInstrumentSwitch.setVisibility(View.INVISIBLE);
                } else {
                    this.mOwnsInstrumentSwitch.setVisibility(View.VISIBLE);
                    this.mOwnsInstrumentSwitch.setChecked(skill.getOwnsInstrument());
                }
            }
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            mOnSkillSelectedListener.onSkillSelected(view, position);
            mSkillLevelSeekBar.setEnabled(true);
            String skill = Arrays.asList(mSkillsArray).get(position);
            skills.get(getAdapterPosition()).setSkill(skill);
            if (excludeInstrumentOwned.contains(skill)) {
                this.mOwnsInstrumentSwitch.setVisibility(View.INVISIBLE);
            } else {
                this.mOwnsInstrumentSwitch.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

        @Nullable
        public Skill getSkill() {
            if (mSkillSpinner != null &&
                !TextUtils.isEmpty((String) mSkillSpinner.getSelectedItem()) &&
                mSkillLevelSeekBar != null &&
                mOwnsInstrumentSwitch != null) {

                // Returning the skill levels with range [1,10]
                return (
                    new Skill(
                        mSkillSpinner.getSelectedItem().toString(),
                        mSkillLevelSeekBar.getProgress() + 1,
                        mOwnsInstrumentSwitch.isChecked()));
            }
            return null;
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.deleteSkill) {
                int position = getAdapterPosition();
                skills.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, skills.size());
                //Reset this item. Necessary because when you delete the item at the end, the viewholder lingers.
                if (mSkillSpinner != null) {
                    this.mSkillSpinner.setSelectionM(SearchableSpinner.NO_ITEM_SELECTED);
                }
                if (mSkillLevelSeekBar != null) {
                    this.mSkillLevelSeekBar.setEnabled(false);
                }
                if(skills.size() == 0){
                    mOnSkillSelectedListener.onSkillDeleteValidate(view,position);
                }
            }
        }
    }
}
