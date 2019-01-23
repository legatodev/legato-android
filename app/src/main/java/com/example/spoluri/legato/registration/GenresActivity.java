package com.example.spoluri.legato.registration;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.spoluri.legato.R;

public class GenresActivity extends AppCompatActivity {

    //TODO: Perhaps I should store this in an xml file that a non-programmer can edit
    String genres[] = {"rap", "rock", "classical", "jazz", "blues", "r&b", "pop", "country", "alternative" };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genres);
    }
}
