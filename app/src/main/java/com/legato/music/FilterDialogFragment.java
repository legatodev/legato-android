package com.legato.music;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.legato.music.R;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class FilterDialogFragment extends DialogFragment {

    public static final String TAG = "FilterDialog";
    @Nullable private NearbyUsersAdapter adapter = null;

    public FilterDialogFragment(NearbyUsersAdapter adapter){
        this.adapter = adapter;
    }

    interface FilterListener {

        void onFilter(Filters filters);

    }

    private View mRootView;

    @BindView(R.id.lookfor)
    Spinner mLookingfor;

    @BindView(R.id.genres)
    Spinner mGenres;

    @BindView(R.id.skills)
    Spinner mSkills;

    @Nullable private FilterListener mFilterListener;

    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.dialog_filters, container, false);
        unbinder = ButterKnife.bind(this, mRootView);

        populateSkillsSpinner();
        populateGenresSpinner();
        populateLookingForSpinner();

        return mRootView;
    }

    private void populateSkillsSpinner() {
        String[] skillsArray = getResources().getStringArray(R.array.skills_array);
        ArrayList<String> skillsList = new ArrayList<String>(Arrays.asList(skillsArray));
        skillsList.add(0, getResources().getString(R.string.value_any_skills));
        // Creating ArrayAdapter using the string array and default spinner layout
        ArrayAdapter<String> skillsAdapter = new ArrayAdapter<String>(getContext(),
                R.layout.item_filter_spinner, skillsList);
        mSkills.setAdapter(skillsAdapter);
    }

    private void populateGenresSpinner() {
        String[] genresArray = getResources().getStringArray(R.array.genres_array);
        ArrayList<String> genresList = new ArrayList<String>(Arrays.asList(genresArray));
        genresList.add(0, getResources().getString(R.string.value_any_genres));
        // Creating ArrayAdapter using the string array and default spinner layout
        ArrayAdapter<String> genresAdapter = new ArrayAdapter<String>(getContext(),
                R.layout.item_filter_spinner, genresList);

        mGenres.setAdapter(genresAdapter);
    }

    private void populateLookingForSpinner() {
        String[] lookingForArray = getResources().getStringArray(R.array.looking_for);
        ArrayList<String> lookingForList = new ArrayList<String>(Arrays.asList(lookingForArray));
        lookingForList.add(0, getResources().getString(R.string.value_any_lookingfor));
        // Creating ArrayAdapter using the string array and default spinner layout
        ArrayAdapter<String> lookingForAdapter = new ArrayAdapter<String>(getContext(),
                R.layout.item_filter_spinner, lookingForList);

        mLookingfor.setAdapter(lookingForAdapter);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof FilterListener) {
            mFilterListener = (FilterListener) context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @OnClick(R.id.buttonSearch)
    public void onSearchClicked() {
        if (mFilterListener != null) {
            mFilterListener.onFilter(getFilters());
        }

        dismiss();
    }

    @OnClick(R.id.buttonCancel)
    public void onCancelClicked() {
        dismiss();
    }

    @Nullable
    private String getSelectedLookingfor() {
        String selected = (String) mLookingfor.getSelectedItem();
        if (getString(R.string.value_any_lookingfor).equals(selected)) {
            return null;
        } else {
            return selected;
        }
    }
    @Nullable
    private String getSelectedGenres() {
        String selected = (String) mGenres.getSelectedItem();
        if (getString(R.string.value_any_genres).equals(selected)) {
            return null;
        } else {
            return selected;
        }
    }
    @Nullable
    private String getSelectedSkills() {
        String selected = (String) mSkills.getSelectedItem();
        if (getString(R.string.value_any_skills).equals(selected)) {
            return null;
        } else {
            return selected;
        }
    }

    public void resetFilters() {
        if (mRootView != null) {
            mLookingfor.setSelection(0);
            mGenres.setSelection(0);
            mSkills.setSelection(0);
        }
    }


    public Filters getFilters() {
        Filters filters = new Filters();

        if(mRootView != null){
            filters.setLookingfor(getSelectedLookingfor());
            filters.setGenres(getSelectedGenres());
            filters.setSkills(getSelectedSkills());
        }

        return filters;
    }
}
