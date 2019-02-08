package com.example.spoluri.legato.registration.solo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.example.spoluri.legato.R;

public class SkillsFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_skills, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        Spinner spinner = (Spinner) view.findViewById(R.id.skillsSpinner1);
        // Creating ArrayAdapter using the string array and default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(),
                R.array.skills_array, android.R.layout.simple_spinner_item);
        // Specify layout to be used when list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Applying the adapter to our spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        SeekBar skillLevelSlider = (SeekBar) view.findViewById(R.id.skillLevelSlider1);
        //Do get progress at the end
        final int progress = skillLevelSlider.getProgress();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
