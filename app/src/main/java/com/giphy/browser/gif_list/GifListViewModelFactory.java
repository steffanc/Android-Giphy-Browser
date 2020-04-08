package com.giphy.browser.gif_list;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.giphy.browser.common.Repository;

import java.util.List;

public class GifListViewModelFactory implements ViewModelProvider.Factory {
    @NonNull
    private final Repository repository;
    @NonNull
    private final List<Integer> backgroundColors;

    GifListViewModelFactory(@NonNull Repository repository, @NonNull List<Integer> backgroundColors) {
        this.repository = repository;
        this.backgroundColors = backgroundColors;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new GifListViewModel(repository, backgroundColors);
    }
}
