package com.legato.music.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.legato.music.repositories.BaseRepository;

public class GenresViewModelFactory implements ViewModelProvider.Factory {
    private final BaseRepository baseRepository;

    public GenresViewModelFactory(BaseRepository baseRepository) {
        this.baseRepository = baseRepository;
    }

    @Override
    @NonNull
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(GenresViewModel.class)) {
            return (T) new GenresViewModel(baseRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
