package com.legato.music.views.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.legato.music.R;
import com.legato.music.repositories.BaseRepository;
import com.legato.music.viewmodels.RegistrationViewModel;
import com.legato.music.viewmodels.RegistrationViewModelFactory;

import co.chatsdk.core.session.ChatSDK;
import co.chatsdk.core.utils.DisposableList;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class RegistrationActivity extends AppCompatActivity {

    private static String TAG = RegistrationActivity.class.getName();
    @NonNull private RegistrationViewModel registrationViewModel;

    private DisposableList disposableList = new DisposableList();


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

    @Override
    public void onBackPressed() {
        disposableList.add(ChatSDK.auth().logout()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action() {
                            @Override
                            public void run() throws Exception {
                                ChatSDK.ui().startSplashScreenActivity(getApplicationContext());
                                registrationViewModel.destroyInstance();
                                Log.d(TAG,"ChatSDK logged out successfully!!");
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Log.d(TAG,throwable.toString());
                            }
                        })
        );

    }

    public void onSolo(View view) {
        Intent intent = new Intent(this, SoloRegistrationActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        disposableList.dispose();
    }

    @NonNull
    private RegistrationViewModel getViewModel() {
        BaseRepository baseRepository = BaseRepository.getInstance();
        ViewModelProvider.Factory factory = new RegistrationViewModelFactory(baseRepository);
        return ViewModelProviders.of(this, factory).get(RegistrationViewModel.class);
    }
}
