package com.legato.music.views.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.legato.music.R;

import co.chatsdk.core.session.ChatSDK;
import co.chatsdk.ui.login.SplashScreenActivity;

public class LegatoSplashScreenActivity extends SplashScreenActivity {

    @Override
    protected void startNextActivity() {
        if (ChatSDK.auth().isAuthenticatedThisSession()) {
            startMainActivity();
        } else if (ChatSDK.auth().isAuthenticated()) {
            if (!isNetworkAvailable()) {
                Toast.makeText(this, R.string.network_unreachable, Toast.LENGTH_LONG).show();
            } else {
                startProgressBar();
                ChatSDK.auth().authenticate().doFinally(() -> stopProgressBar()).subscribe(() -> {
                    // Launch the Chat SDK
                    startMainActivity();
                }, throwable -> {
                    // Show the login screen
                    startLoginActivity();
                });
            }
        } else {
            startLoginActivity();
        }
    }

    protected boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
