package com.legato.music.viewmodels;

import android.content.Context;
import android.text.TextUtils;

import androidx.lifecycle.ViewModel;

import com.legato.music.models.NearbyUser;
import com.legato.music.repositories.BaseRepository;

public class RegistrationViewModel extends ViewModel {
    private BaseRepository mBaseRepository;

    public RegistrationViewModel(BaseRepository baseRepository) {
        mBaseRepository = baseRepository;
    }

    public boolean hasUser() {
        mBaseRepository.login();

        NearbyUser nearbyUser = mBaseRepository.getCurrentUser();
        if (nearbyUser != null) {
            if (!TextUtils.isEmpty(nearbyUser.getSkills())) {
                return true;
            }
        }

        return false;
    }

    public void sendVerification() {
        if (!mBaseRepository.isEmailVerified()) {
            mBaseRepository.sendEmailVerification();
        }
    }

    public void navToLogin(Context context) {
        mBaseRepository.navToLogin(context);
    }
}
