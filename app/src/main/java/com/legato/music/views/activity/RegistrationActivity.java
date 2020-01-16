package com.legato.music.views.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.legato.music.R;
import com.legato.music.viewmodels.RegistrationViewModel;

public class RegistrationActivity extends AppCompatActivity {

    @NonNull private RegistrationViewModel registrationViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        registrationViewModel = ViewModelProviders.of(this).get(RegistrationViewModel.class);

        if (registrationViewModel.hasUser()) {
            Intent intent = new Intent(this, NearbyUsersActivity.class);
            startActivity(intent);
            finish();
        }

        setContentView(R.layout.activity_registration);
    }

    public void onSolo(View view) {
        Intent intent = new Intent(this, SoloRegistrationActivity.class);
        startActivity(intent);
    }
}
