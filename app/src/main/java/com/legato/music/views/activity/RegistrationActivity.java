package com.legato.music.views.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.legato.music.R;
import com.legato.music.repositories.BaseRepository;
import com.legato.music.viewmodels.RegistrationViewModel;
import com.legato.music.viewmodels.RegistrationViewModelFactory;

public class RegistrationActivity extends AppCompatActivity {

    @NonNull private RegistrationViewModel registrationViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        registrationViewModel = getViewModel();

        if (registrationViewModel.hasUser()) {
            Intent intent = new Intent(this, NearbyUsersActivity.class);
            startActivity(intent);
            finish();
        } else {
            registrationViewModel.sendVerification();
        }

        setContentView(R.layout.activity_registration);
    }

    public void onSolo(View view) {
        Intent intent = new Intent(this, SoloRegistrationActivity.class);
        startActivity(intent);
    }

    @NonNull
    private RegistrationViewModel getViewModel() {
        BaseRepository baseRepository = BaseRepository.getInstance();
        ViewModelProvider.Factory factory = new RegistrationViewModelFactory(baseRepository);
        return ViewModelProviders.of(this, factory).get(RegistrationViewModel.class);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        BaseRepository.getInstance().logout();
        registrationViewModel.navToLogin(getApplicationContext());
    }
}
