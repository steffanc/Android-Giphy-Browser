package com.giphy.browser.main;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.giphy.browser.GiphyApp;
import com.giphy.browser.R;
import com.giphy.browser.Repository;
import com.giphy.browser.common.BaseActivity;
import com.giphy.browser.common.InfiniteScrollListener;
import com.giphy.browser.databinding.ActivityMainBinding;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;
    private MainViewModel viewModel;
    private SearchView searchView;
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
        final GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        binding.content.setLayoutManager(layoutManager);
        binding.content.addOnScrollListener(new InfiniteScrollListener(
                layoutManager, 10, () -> viewModel.scrollThresholdReached()));

        binding.swipeContainer.setOnRefreshListener(() -> viewModel.screenRefreshed());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setIconifiedByDefault(true);
        searchView.setOnSearchClickListener((view) -> viewModel.searchClicked());
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                viewModel.queryUpdated(newText);
                return true;
            }
        });
        searchView.setOnCloseListener(() -> {
            viewModel.searchClosed();
            return false;
        });
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
        } else {
            super.onBackPressed();
        }
    }

    private void render(MainState state) {
        adapter.submitList(state.getItems());

        binding.progressBar.setVisibility(state.isLoading() ? View.VISIBLE : View.GONE);
        binding.swipeContainer.setRefreshing(state.isRefreshing());
        binding.swipeContainer.setEnabled(!state.isLoading());

        if (state.getToast() != null) {
            state.getToast().maybeConsume((string) ->
                    Toast.makeText(this, string, Toast.LENGTH_SHORT).show());
        }
    }
}
