package com.giphy.browser.main;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.giphy.browser.Repository;

import java.util.List;

public class MainViewModelFactory implements ViewModelProvider.Factory {
    @NonNull
    private final Repository repository;
    @NonNull
    private final List<Integer> backgroundColors;

    public MainViewModelFactory(@NonNull Repository repository, @NonNull List<Integer> backgroundColors) {
        this.repository = repository;
        this.backgroundColors = backgroundColors;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MainViewModel(repository, backgroundColors);
    }
}
