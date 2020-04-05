package com.giphy.browser;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.giphy.browser.common.BaseActivity;
import com.giphy.browser.databinding.ActivityMainBinding;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;
    private MainViewModel viewModel;
    private MainAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        final Repository repository = ((GiphyApp) getApplication()).getRepository();
        viewModel = new ViewModelProvider(this, new MainViewModelFactory(repository))
                .get(MainViewModel.class);
        viewModel.getStateLiveData().observe(this, this::render);

        adapter = new MainAdapter((position, item) -> viewModel.gifClicked(position, item));
        binding.content.setAdapter(adapter);
        binding.content.setLayoutManager(new GridLayoutManager(this, 2));

        binding.swipeContainer.setOnRefreshListener(() -> viewModel.screenRefreshed());
    }

    private void render(MainState state) {
        adapter.submitList(state.getItems());

        binding.progressBar.setVisibility(state.isLoading() ? View.VISIBLE : View.GONE);
        binding.swipeContainer.setRefreshing(state.isRefreshing());
        binding.swipeContainer.setEnabled(!state.isLoading());

        if (state.getToast() != null) {
            state.getToast().maybeConsume((string) -> {
                Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
                return null;
            });
        }
    }
}
