package com.legato.music.viewmodels;

import android.util.SparseBooleanArray;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;

import com.legato.music.models.NearbyUser;
import com.legato.music.repositories.BaseRepository;

public class GenresViewModel extends ViewModel {
    private BaseRepository mBaseRepository = BaseRepository.getInstance();
    private NearbyUser mNearbyUser;

    private boolean valid = false;

    public GenresViewModel() {
        mNearbyUser = mBaseRepository.getCurrentUser();
    }

    public @Nullable String getGenres() { return mNearbyUser.getGenres(); }

    public boolean contains(String item) {
        return (mNearbyUser.getGenres() != null) && mNearbyUser.getGenres().contains(item);
    }

    public boolean getValid() { return valid; }

    public void updateValidList(ListView listView) { valid = (listView.getCheckedItemCount() > 0); }

    public ListView setCheckedItems(ListView listView, ArrayAdapter<CharSequence> adapter) {
        listView.setAdapter(adapter);

        int listCount = listView.getCount();
        for (int i = 0; i < listCount; i++) {
            String genre = adapter.getItem(i).toString();
            listView.setItemChecked(i, contains(genre));
        }

        return listView;
    }

    public String extractData(
            @Nullable ListView listView,
            @Nullable ArrayAdapter<CharSequence> adapter) {

        if (listView == null || adapter == null)
            return "";

        StringBuilder sb = new StringBuilder();

        SparseBooleanArray checked = listView.getCheckedItemPositions();
        for (int i = 0; i < checked.size(); i++) {
            int position = checked.keyAt(i);
            if (checked.valueAt(i)) {
                if (adapter.getItem(position) != null) {
                    String genre = adapter.getItem(position).toString();
                    sb.append(genre + "|");
                }
            }
        }

        return sb.toString();
    }
}
