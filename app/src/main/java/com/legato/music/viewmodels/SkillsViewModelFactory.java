package com.legato.music.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.legato.music.repositories.BaseRepository;

import java.util.List;

public class SkillsViewModelFactory implements ViewModelProvider.Factory {
    private final BaseRepository baseRepository;
    private final List<String> noInstrumentList;

    public SkillsViewModelFactory(BaseRepository baseRepository, List<String> noInstrumentList) {
        this.baseRepository = baseRepository;
        this.noInstrumentList = noInstrumentList;
    }

    @Override
    @NonNull
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SkillsViewModel.class)) {
            return (T) new SkillsViewModel(baseRepository, noInstrumentList);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
