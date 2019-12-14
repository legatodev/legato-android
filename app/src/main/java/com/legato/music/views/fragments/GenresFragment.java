package com.legato.music.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.legato.music.R;
import com.legato.music.viewmodels.GenresViewModel;
import com.legato.music.utils.Keys;
import com.legato.music.views.activity.SoloRegistrationActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GenresFragment extends Fragment {
    @BindView(R.id.genresListView) ListView genresListView;

    @Nullable private ArrayAdapter<CharSequence> adapter = null;

    @NonNull private GenresViewModel genresViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_genres, container, false);
        ButterKnife.bind(this, view);

        genresViewModel = ViewModelProviders.of(requireActivity()).get(GenresViewModel.class);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.genres_array,
                android.R.layout.simple_list_item_multiple_choice);

        genresListView = genresViewModel.setCheckedItems(genresListView, adapter);

        genresListView.setOnItemClickListener((parent, view1, position, id) -> validate());

        validate();
    }

    private void validate() {
        genresViewModel.updateValidList(genresListView);

        if (getActivity() != null) {
            ((SoloRegistrationActivity) getActivity()).setVisibleTabCount();
        }
    }

    public String extractData() {
        return genresViewModel.extractData(genresListView, adapter);
    }

    //Called when the user navigates to this tab
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser)
            validate();
    }

    public boolean isInputValid() {
        if (genresViewModel != null)
            return genresViewModel.getValid();

        return false;
    }
}
