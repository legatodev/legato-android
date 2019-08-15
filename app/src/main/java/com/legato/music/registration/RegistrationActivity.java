package com.legato.music.registration;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.legato.music.Keys;
import com.legato.music.NearbyUsersActivity;
import com.legato.music.R;
import com.legato.music.registration.band.BandRegistrationActivity;
import com.legato.music.registration.solo.SoloRegistrationActivity;

import co.chatsdk.core.session.ChatSDK;

public class RegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ChatSDK.currentUser() != null) {
            if (ChatSDK.currentUser().metaStringForKey(Keys.skills) != null && !ChatSDK.currentUser().metaStringForKey(Keys.skills).isEmpty()) {
                Intent intent = new Intent(this, NearbyUsersActivity.class);
                startActivity(intent);
                finish();
            }
        }

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
