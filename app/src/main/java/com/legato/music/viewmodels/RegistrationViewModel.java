package com.legato.music.viewmodels;

import android.text.TextUtils;

import androidx.lifecycle.ViewModel;

import com.legato.music.models.NearbyUser;
import com.legato.music.repositories.BaseRepository;

public class RegistrationViewModel extends ViewModel {
    private BaseRepository mBaseRepository;
    private NearbyUser mNearbyUser;

    public RegistrationViewModel(BaseRepository baseRepository) {
        mBaseRepository = baseRepository;

        mNearbyUser = mBaseRepository.getCurrentUser();
    }

    public boolean hasUser() {
        if (mNearbyUser != null) {
            String skills = mNearbyUser.getSkills();
            if (!TextUtils.isEmpty(skills)) {
                return true;
            }
        }

        return false;
    }

    public void sendVerification() {
        if (mBaseRepository.isEmailVerified()) {
            mBaseRepository.sendEmailVerification();
        }
    }
}
