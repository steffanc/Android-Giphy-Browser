package com.giphy.browser.gif_list;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.giphy.browser.GiphyApp;
import com.giphy.browser.R;
import com.giphy.browser.common.BaseActivity;
import com.giphy.browser.common.InfiniteScrollListener;
import com.giphy.browser.common.Repository;
import com.giphy.browser.databinding.ActivityGifListBinding;
import com.giphy.browser.gif_detail.GifDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class GifListActivity extends BaseActivity {

    private ActivityGifListBinding binding;
    private GifListViewModel viewModel;
    private SearchView searchView;
    private GifListAdapter adapter;
    @Nullable
    private Toast toast;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gif_list);

        final Context context = binding.getRoot().getContext();
        final TypedArray array = context.getResources().obtainTypedArray(R.array.backgroundColors);
        final List<Integer> backgroundColors = new ArrayList<>(array.length());
        for (int i = 0; i < array.length(); i++) {
            backgroundColors.add(array.getResourceId(i, 0));
        }
        array.recycle();

        final Repository repository = ((GiphyApp) getApplication()).getRepository();
        viewModel = new ViewModelProvider(this, new GifListViewModelFactory(repository, backgroundColors))
                .get(GifListViewModel.class);
        viewModel.getStateLiveData().observe(this, this::render);

        adapter = new GifListAdapter((position, item) -> viewModel.gifClicked(position, item));
        binding.content.setAdapter(adapter);
        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, RecyclerView.VERTICAL);
        binding.content.setLayoutManager(layoutManager);
        binding.content.addOnScrollListener(new InfiniteScrollListener(
                layoutManager, 10, () -> viewModel.scrollThresholdReached()));

        binding.swipeContainer.setOnRefreshListener(() -> viewModel.screenRefreshed());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gif_list_menu, menu);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();

        searchView.setQuery(viewModel.getState().getQuery(), false);
        searchView.setIconified(!viewModel.getState().isSearchVisible());

        // Don't expand search view to full screen in landscape mode
        searchView.setImeOptions(searchView.getImeOptions() | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
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

    private void render(@NonNull GifListState state) {
        adapter.submitList(state.getItems());

        binding.progressBar.setVisibility(state.isLoading() ? View.VISIBLE : View.GONE);
        binding.swipeContainer.setRefreshing(state.isRefreshing());
        binding.swipeContainer.setEnabled(!state.isLoading());

        if (state.getToast() != null) {
            state.getToast().maybeConsume((strRes) -> {
                // Prevent multiple toasts from stacking
                if (toast != null) toast.cancel();
                toast = Toast.makeText(this, getString(strRes), Toast.LENGTH_SHORT);
                toast.show();
            });
        }

        if (state.getNavigateGifDetail() != null) {
            state.getNavigateGifDetail().maybeConsume((args) ->
                    startActivity(GifDetailActivity.newIntent(this, args)));
        }
    }
}
