package com.example.spoluri.legato.registration;

import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.spoluri.legato.R;
import com.example.spoluri.legato.registration.solo.SoloRegistrationActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GenresFragment extends Fragment {
    @BindView(R.id.genresListView)
    ListView genresListView;
    private ArrayAdapter<CharSequence> adapter;
    private boolean valid;

    public GenresFragment() {
        valid = false;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_genres, container, false);
        ButterKnife.bind(this, view);

        if (getContext() != null) {
            adapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.genres_array, android.R.layout.simple_list_item_multiple_choice);

            genresListView.setAdapter(adapter);
            genresListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    validate();
                }
            });
        }

        return view;
    }

    private void validate() {
        int checkedCount = genresListView.getCheckedItemCount();
        valid = (checkedCount > 0);
        if (getActivity() != null) {
            if (valid) {
                ((SoloRegistrationActivity) getActivity()).setVisibleTabCount(3);
            } else {
                ((SoloRegistrationActivity)getActivity()).setVisibleTabCount(2);
            }
        }
    }

    public String extractData() {
        String genres = "";
        SparseBooleanArray checked = genresListView.getCheckedItemPositions();
        for (int i = 0; i < checked.size(); i++) {
            int position = checked.keyAt(i);
            if (checked.valueAt(i)) {
                if (adapter.getItem(position) != null) {
                    String genre = adapter.getItem(position).toString();
                    genres += (genre + "|");
                }
            }
        }

        return genres;
    }

    //Called when the user navigates to this tab
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser)
            validate();
    }
}
