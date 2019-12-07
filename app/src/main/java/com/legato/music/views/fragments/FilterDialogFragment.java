package com.legato.music.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

import com.legato.music.AppConstants;
import com.legato.music.models.Filters;
import com.legato.music.R;
import com.legato.music.viewmodels.NearbyUsersViewModel;
import com.legato.music.views.activity.NearbyUsersActivity;
import com.legato.music.views.adapters.NearbyUsersAdapter;
import com.legato.music.utils.Keys;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import co.chatsdk.core.session.ChatSDK;

public class FilterDialogFragment extends DialogFragment {

    public static final String TAG = FilterDialogFragment.class.getSimpleName();

    private static final String SELECTED_SKILL = "selected_skill";
    private static final String SELECTED_GENRE = "selected_genre";
    private static final String SELECTED_LOOKING_FOR = "selected_looking_for";
    private static final String SELECTED_SORT_BY = "selected_sort_by";
    private static final String SELECTED_SEARCH_RADIUS = "selected_search_radius";


    @BindView(R.id.lookfor)
    Spinner mLookingfor;
    @BindView(R.id.genres)
    Spinner mGenres;
    @BindView(R.id.skills)
    Spinner mSkills;
    @BindView(R.id.sortby)
    Spinner mSortBy;
    @BindView(R.id.searchRadiusValue)
    TextView mSearchRadiusValue;
    @BindView(R.id.searchRadiusSeekBar)
    SeekBar mSeekBarSearch;

    private View mRootView;

    @Nullable private FilterListener mFilterListener;

    @NonNull private NearbyUsersViewModel nearbyUsersViewModel;

    private Unbinder unbinder;

    public interface FilterListener {

        void onFilter(Filters filters);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_filters, container, false);
        unbinder = ButterKnife.bind(this, mRootView);

        nearbyUsersViewModel = ViewModelProviders.of(requireActivity()).get(NearbyUsersViewModel.class);

        return mRootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        populateSkillsSpinner();
        populateGenresSpinner();
        populateLookingForSpinner();
        populateSortBy();
        populateSearchSeekBar();

        if (savedInstanceState != null) {
            loadSavedState(savedInstanceState);
        }
        else if (getActivity() != null) {
            loadSavedState(
                    nearbyUsersViewModel.getDialogSavedStateBundle());
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState = getSavedState();
    }

    private Bundle getSavedState() {
        Bundle outState = new Bundle();

        outState.putInt(SELECTED_LOOKING_FOR, mLookingfor.getSelectedItemPosition());
        outState.putInt(SELECTED_GENRE, mGenres.getSelectedItemPosition());
        outState.putInt(SELECTED_SKILL, mSkills.getSelectedItemPosition());
        outState.putInt(SELECTED_SORT_BY, mSortBy.getSelectedItemPosition());
        outState.putInt(SELECTED_SEARCH_RADIUS, mSeekBarSearch.getProgress());

        return outState;
    }

    private void loadSavedState(@Nullable Bundle savedState) {
        if (savedState != null) {
            mLookingfor.setSelection(savedState.getInt(SELECTED_LOOKING_FOR));
            mGenres.setSelection(savedState.getInt(SELECTED_GENRE));
            mSkills.setSelection(savedState.getInt(SELECTED_SKILL));
            mSortBy.setSelection(savedState.getInt(SELECTED_SORT_BY));

            int searchRadius = savedState.getInt(SELECTED_SEARCH_RADIUS);
            mSearchRadiusValue.setText(Integer.toString(searchRadius));
            mSeekBarSearch.setProgress(searchRadius);
        }
    }

    private void setTextView(@Nullable TextView view, String text) {
        if (view != null) { view.setText(text); }
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

    private void populateSortBy() {
        String[] sortByArray = getResources().getStringArray(R.array.sortBy);
        ArrayList<String> sortByList = new ArrayList<String>(Arrays.asList(sortByArray));
        sortByList.add(0, getResources().getString(R.string.value_default_sort));
        // Creating ArrayAdapter using the string array and default spinner layout
        ArrayAdapter<String> sortByAdapter = new ArrayAdapter<String>(getContext(),
                R.layout.item_filter_spinner, sortByList);

        mSortBy.setAdapter(sortByAdapter);
    }

    private void populateSearchSeekBar(){

        if (mSeekBarSearch != null) {
            mSeekBarSearch.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    setTextView(mSearchRadiusValue, progress + "mi");
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    //write custom code to on start progress
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
        }

        String searchRadius = AppConstants.DEFAULT_SEARCH_RADIUS;
        if (ChatSDK.currentUser().metaStringForKey(Keys.searchradius) != null && !ChatSDK.currentUser().metaStringForKey(Keys.searchradius).isEmpty()) {
            searchRadius = ChatSDK.currentUser().metaStringForKey(Keys.searchradius);
        }
        if (mSeekBarSearch != null) {
            mSeekBarSearch.setProgress(Integer.parseInt(searchRadius));
        }
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
        if (getActivity() != null) {
            nearbyUsersViewModel.setDialogSavedStateBundle(getSavedState());
        }

        if (unbinder != null) {
            unbinder.unbind();
        }

        super.onDestroyView();
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

    @Nullable
    private String getSelectedSortBy() {
        String selected = (String) mSortBy.getSelectedItem();
        if (getString(R.string.value_default_sort).equals(selected)) {
            return null;
        } else {
            return selected;
        }
    }

    private  String getSelectedSearchRadius() {
            return Integer.toString(mSeekBarSearch.getProgress());
    }

    public void resetFilters() {
        if (mRootView != null) {
            mLookingfor.setSelection(0);
            mGenres.setSelection(0);
            mSkills.setSelection(0);
            mSortBy.setSelection(0);
        }
    }

    public Filters getFilters() {
        Filters filters = new Filters();

        if(mRootView != null){
            filters.setLookingfor(getSelectedLookingfor());
            filters.setGenres(getSelectedGenres());
            filters.setSkills(getSelectedSkills());
            filters.setSortby(getSelectedSortBy());
            filters.setSearchRadius(getSelectedSearchRadius());
        }

        return filters;
    }
}
