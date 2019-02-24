package com.example.spoluri.legato.registration.solo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.example.spoluri.legato.R;

public class SkillsHolder extends RecyclerView.ViewHolder {

    private final Spinner skillSpinner;
    private final SeekBar skillLevelSeekBar;

    private Skills skill;
    private Context context;

    public SkillsHolder(Context context, View itemView) {
        super(itemView);

        // 1. Set the context
        this.context = context;

        // 2. Set up the UI widgets of the holder
        this.skillLevelSeekBar = (SeekBar) itemView.findViewById(R.id.skillLevelSlider1);
        this.skillSpinner = (Spinner) itemView.findViewById(R.id.skillsSpinner1);

        // Creating ArrayAdapter using the string array and default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.skills_array, android.R.layout.simple_spinner_item);
        // Specify layout to be used when list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Applying the adapter to our spinner
        skillSpinner.setAdapter(adapter);
    }

    public void bindSkill(Skills skill) {
        // 4. Bind the data to the ViewHolder
        this.skill = skill;
        this.skillLevelSeekBar.setProgress(skill.getSkillLevel());
        //this.skillSpinner.setSelection(1);
    }
}
