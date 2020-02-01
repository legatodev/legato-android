package com.legato.music.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.legato.music.repositories.BaseRepository;

public class NearbyUsersViewModelFactory implements ViewModelProvider.Factory{
    private final BaseRepository baseRepository;

    public NearbyUsersViewModelFactory(BaseRepository baseRepository) {
        this.baseRepository = baseRepository;
    }

    @Override
    @NonNull
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(NearbyUsersViewModel.class)) {
            return (T) new NearbyUsersViewModel(baseRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }



}
