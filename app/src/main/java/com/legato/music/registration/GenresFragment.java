package com.legato.music.registration;

import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.legato.music.Keys;
import com.legato.music.R;
import com.legato.music.registration.solo.SoloRegistrationActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.chatsdk.core.dao.User;
import co.chatsdk.core.session.ChatSDK;

public class GenresFragment extends Fragment {
    @BindView(R.id.genresListView)
    ListView genresListView;
    @Nullable private ArrayAdapter<CharSequence> adapter;
    private boolean valid;

    public GenresFragment() {
        valid = false;
        adapter = null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_genres, container, false);
        ButterKnife.bind(this, view);
        User user = ChatSDK.currentUser();
        String dbgenres = user.metaStringForKey(Keys.genres);

        if (getContext() != null) {
            adapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.genres_array, android.R.layout.simple_list_item_multiple_choice);

            if (genresListView != null) {
                genresListView.setAdapter(adapter);
                int listCount = genresListView.getCount();
                if (dbgenres != null) {
                    for (int i = 0; i < listCount; i++) {
                        if (dbgenres.contains(adapter.getItem(i).toString())) {
                            genresListView.setItemChecked(i, true);
                        }
                    }
                }

                genresListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        validate();
                    }
                });
            }
        }

        validate();

        return view;
    }

    private void validate() {
        int checkedCount = genresListView.getCheckedItemCount();
        valid = (checkedCount > 0);
        if (getActivity() != null) {
            ((SoloRegistrationActivity) getActivity()).setVisibleTabCount();
        }
    }

    public String extractData() {
        String genres = "";
        if (genresListView != null && adapter != null) {
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

    public boolean isInputValid() {
        return valid;
    }
}
