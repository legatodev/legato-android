package com.legato.music.viewmodels;

import android.text.TextUtils;

import androidx.lifecycle.ViewModel;

import com.legato.music.models.NearbyUser;
import com.legato.music.repositories.BaseRepository;

public class RegistrationViewModel extends ViewModel {
    private BaseRepository mBaseRepository = BaseRepository.getInstance();
    private NearbyUser mNearbyUser;

    public RegistrationViewModel() {
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
}
