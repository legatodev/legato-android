package com.example.spoluri.legato.registration;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.spoluri.legato.R;

public class GenresFragment extends Fragment {
    private ListView genresListView;
    private ArrayAdapter<CharSequence> adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_genres, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        if (view != null) {
            adapter = ArrayAdapter.createFromResource(view.getContext(),
                    R.array.genres_array, android.R.layout.simple_list_item_multiple_choice);

            // Getting the reference to the listview object of the layout
            genresListView = view.findViewById(R.id.genresListView);

            // Setting adapter to the listview
            genresListView.setAdapter(adapter);
        }
    }

    //call this function when proceeding to the next screen
    private void sendSelectedGenres(){
        SparseBooleanArray checked = genresListView.getCheckedItemPositions();
        CharSequence selectedItems[] = new CharSequence[checked.size()];
        for (int i = 0; i < checked.size(); i++) {
            // Item position in adapter
            int position = checked.keyAt(i);
            // Add sport if it is checked i.e.) == TRUE!
            if (checked.valueAt(i))
                selectedItems[i]= adapter.getItem(position);
        }

//        Intent intent = new Intent(getApplicationContext(),
//                ResultActivity.class);
//
//        // Create a bundle object
//        Bundle b = new Bundle();
//        b.putStringArray("selectedItems", selectedItems);
//
//        // Add the bundle to the intent.
//        intent.putExtras(b);
//
//        // start the ResultActivity
//        startActivity(intent);
    }
}
