package com.legato.music.viewmodels;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.legato.music.models.NearbyUser;
import com.legato.music.repositories.BaseRepository;

public class NearbyUserViewModel extends ViewModel {

    private BaseRepository mBaseRepository;

    public NearbyUserViewModel(){
        mBaseRepository = BaseRepository.getInstance();
    }

    public LiveData<NearbyUser> getNearbyUser() {
        return mBaseRepository.getNearbyUser();
    }

    public void searchNearbyUserByRadius(double searchRadius){
        mBaseRepository.searchNearbyUserByRadius(searchRadius);
    }

    public void setLocation(Location location) {
        mBaseRepository.setLocation(location);
    }

    public Boolean isEmailVerified(){
        return mBaseRepository.isEmailVerified();
    }

    public void sendEmailVerification(){
        mBaseRepository.sendEmailVerification();
    }
}
