package com.example.spoluri.legato.registration;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.spoluri.legato.R;

import java.util.ArrayList;

public class GenresActivity extends AppCompatActivity {
    ListView genresListView;
    ArrayAdapter<CharSequence> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genres);

        adapter = ArrayAdapter.createFromResource(this,
                R.array.genres_array, android.R.layout.simple_list_item_multiple_choice);

        // Getting the reference to the listview object of the layout
        genresListView = (ListView) findViewById(R.id.genresListView);

        // Setting adapter to the listview
        genresListView.setAdapter(adapter);
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
