package com.legato.music.viewmodels;

import android.location.Location;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.legato.music.models.NearbyUser;
import com.legato.music.repositories.BaseRepository;

import java.util.List;

import co.chatsdk.core.dao.User;

public class NearbyUsersViewModel extends ViewModel {

    private BaseRepository mBaseRepository;

    @Nullable
    private Bundle mDialogSavedStateBundle = null;

    public NearbyUsersViewModel(){ mBaseRepository = BaseRepository.getInstance(); }

    public LiveData<List<NearbyUser>> getNearbyUsers() {
        return mBaseRepository.getNearbyUsers();
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

    public Boolean isProximityAlertEnabled(){
        return mBaseRepository.isProximityAlertEnabled();
    }

    public String getSearchRadius(){
        return mBaseRepository.getSearchRadius();
    }

    @Nullable
    public Bundle getDialogSavedStateBundle() {
        return mDialogSavedStateBundle;
    }

    public void setDialogSavedStateBundle(Bundle savedState) {
        mDialogSavedStateBundle = savedState;
    }
}
