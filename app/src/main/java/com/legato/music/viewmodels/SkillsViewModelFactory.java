package com.legato.music.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.legato.music.repositories.BaseRepository;

public class SkillsViewModelFactory implements ViewModelProvider.Factory {
    private final BaseRepository baseRepository;

    public SkillsViewModelFactory(BaseRepository baseRepository) {
        this.baseRepository = baseRepository;
    }

    @Override
    @NonNull
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SkillsViewModel.class)) {
            return (T) new SkillsViewModel(baseRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
