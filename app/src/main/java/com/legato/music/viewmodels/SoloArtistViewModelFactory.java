package com.legato.music.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.legato.music.repositories.BaseRepository;

public class SoloArtistViewModelFactory implements ViewModelProvider.Factory {
    private final BaseRepository baseRepository;

    public SoloArtistViewModelFactory(BaseRepository baseRepository) {
        this.baseRepository = baseRepository;
    }

    @Override
    @NonNull
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SoloArtistViewModel.class)) {
            return (T) new SoloArtistViewModel(baseRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
