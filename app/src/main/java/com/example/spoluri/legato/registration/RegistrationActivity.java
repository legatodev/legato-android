package com.example.spoluri.legato.registration;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spoluri.legato.R;
import com.example.spoluri.legato.registration.Band.BandRegistrationActivity;
import com.example.spoluri.legato.registration.solo.SoloRegistrationActivity;

public class RegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
    }

    public void onSolo(View view) {
        Intent intent = new Intent(this, SoloRegistrationActivity.class);
        startActivity(intent);
    }
    public void onBand(View view) {
        Intent intent = new Intent(this, BandRegistrationActivity.class);
        startActivity(intent);
    }
}
