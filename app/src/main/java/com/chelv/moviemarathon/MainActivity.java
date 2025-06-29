package com.chelv.moviemarathon;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.LoadState;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.RequestManager;
import com.chelv.moviemarathon.adapter.data_adapter.MoviesAdapter;
import com.chelv.moviemarathon.adapter.loadstate_adapter.MoviesLoadStateAdapter;
import com.chelv.moviemarathon.databinding.ActivityMainBinding;
import com.chelv.moviemarathon.util.GridSpace;
import com.chelv.moviemarathon.util.networkstate.NetworkStatusLiveData;
import com.chelv.moviemarathon.viewmodel.MovieViewModel;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.disposables.CompositeDisposable;


@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    MovieViewModel mainMovieViewModel;
    ActivityMainBinding mainBinding;
    MoviesAdapter moviesAdapter;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private boolean isShowingInitialLoadPrompt = true;

    private NetworkStatusLiveData networkStatusLiveData;
    private ImageView networkStatusIcon;
    private TextView networkStatusMessage;
    private Boolean isNetworkMessageExpanded = false;

    @Inject
    RequestManager requestManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(mainBinding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(mainBinding.main, (v, insets) -> { // <--- Use mainBinding.main
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initComponents();
        setupInitialLoadingUI();
        initRecyclerViewAndAdapter();
        subscribeToEvents();
    }

    private void setupInitialLoadingUI() {
        mainBinding.initialLoadPromptTextView.setVisibility(View.VISIBLE);
        mainBinding.initialLoadPromptProgressBar.setVisibility(View.VISIBLE);

        mainBinding.recyclerViewMovies.setVisibility(View.GONE);
        mainBinding.globalProgressBar.setVisibility(View.GONE); // Hide the main circular progress bar
        mainBinding.globalErrorTextView.setVisibility(View.GONE);
        mainBinding.emptyViewTextView.setVisibility(View.GONE);

        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setEnabled(false); // Disable swipe during this initial phase
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void hideInitialLoadingUI() {
        isShowingInitialLoadPrompt = false;
        mainBinding.initialLoadPromptTextView.setVisibility(View.GONE);
        mainBinding.initialLoadPromptProgressBar.setVisibility(View.GONE);
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setEnabled(true); // Enable swipe now
        }
    }

    private void subscribeToEvents() {

        compositeDisposable.add(
                mainMovieViewModel.pagingDataFlowable
                        .subscribe(
                                moviePagingData -> {
                                    moviesAdapter.submitData(getLifecycle(), moviePagingData);
                                },
                                throwable -> {
                                    Log.e("MainActivity", "Error subscribing to PagingData", throwable);
                                    if (isShowingInitialLoadPrompt) {
                                        hideInitialLoadingUI(); // Hide prompt even on subscription error
                                    }
                                    // Your existing error handling for globalErrorTextView
                                    if (mainBinding.globalErrorTextView != null && moviesAdapter.getItemCount() == 0) {
                                        mainBinding.globalErrorTextView.setText("Failed to load movie stream: " + throwable.getLocalizedMessage());
                                        mainBinding.globalErrorTextView.setVisibility(View.VISIBLE);
                                        if (mainBinding.globalProgressBar != null) mainBinding.globalProgressBar.setVisibility(View.GONE);
                                        mainBinding.recyclerViewMovies.setVisibility(View.GONE);
                                    }
                                }
                        )
        );

        //observing the networkChanges
        networkStatusLiveData.observe(this, isConnected ->{
            if(isConnected){
                networkStatusIcon.setVisibility(View.GONE);
                networkStatusMessage.setVisibility(View.GONE);
                isNetworkMessageExpanded = false;
            }else{
                networkStatusIcon.setVisibility(View.VISIBLE);
                networkStatusMessage.setVisibility(View.GONE);
            }
        });

        networkStatusIcon.setOnClickListener(v -> {
            if (networkStatusIcon.getVisibility() == View.VISIBLE) { // Only if icon itself is visible
                isNetworkMessageExpanded = !isNetworkMessageExpanded;
                networkStatusMessage.setVisibility(isNetworkMessageExpanded ? View.VISIBLE : View.GONE);
            }
        });
    }



    private void initRecyclerViewAndAdapter() {
        recyclerView = mainBinding.recyclerViewMovies;

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new GridSpace(2, 20, true));
        recyclerView.setHasFixedSize(true);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return moviesAdapter.getItemViewType(position) == MoviesAdapter.LOADING_ITEM ? 1:2;
            }
        });

        MoviesLoadStateAdapter moviesLoadStateAdapter = new MoviesLoadStateAdapter((view) -> moviesAdapter.retry());

        recyclerView.setAdapter(moviesAdapter.withLoadStateFooter(moviesLoadStateAdapter));


        final LoadState[][] previousRefreshState = {new LoadState[1]};
        boolean[] isRecyclerViewDimmedForRefresh = new boolean[]{false};
        boolean[] wasSwipeRefreshing = new boolean[]{false};

        moviesAdapter.addLoadStateListener(combinedLoadStates -> {
            LoadState refreshState = combinedLoadStates.getRefresh();
            LoadState currentRefreshState = combinedLoadStates.getRefresh();

            // --- Logic for Initial Load Prompt ---
            if (isShowingInitialLoadPrompt) {
                if (!(refreshState instanceof LoadState.Loading)) {
                    // Initial load has finished (succeeded or failed) or is not applicable.
                    // Time to hide the initial prompt and let the normal UI take over.
                    hideInitialLoadingUI();
                } else {
                    // Still in initial "Loading Movies..." phase (refreshState is Loading).
                    // Keep the prompt visible and prevent other UI updates for now.
                    // Ensure other conflicting UI elements are hidden.
                    mainBinding.recyclerViewMovies.setVisibility(View.GONE);
                    mainBinding.globalProgressBar.setVisibility(View.GONE);
                    if (swipeRefreshLayout != null) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    return null; // Stop further processing in this listener callback for now
                }
            }
            // --- End of Logic for Initial Load Prompt ---

            // From this point onwards, isShowingInitialLoadPrompt is false.

            // Handle SwipeRefreshLayout spinner
            if (swipeRefreshLayout != null) {
                // Show swipe refresh spinner if it's loading AND
                // (EITHER items are already present (it's a user refresh)
                // OR no items are present BUT it's not the global progress bar's job anymore)
               /* boolean showSwipeSpinner = refreshState instanceof LoadState.Loading &&
                        !(refreshState instanceof LoadState.Loading && moviesAdapter.getItemCount() == 0 && mainBinding.globalProgressBar.getVisibility() == View.VISIBLE);
                */
                boolean isActuallyRefreshing = currentRefreshState instanceof LoadState.Loading &&
                        (moviesAdapter.getItemCount() > 0 || (mainBinding.globalProgressBar != null && mainBinding.globalProgressBar.getVisibility() == View.GONE));
                swipeRefreshLayout.setRefreshing(isActuallyRefreshing);
            }


            if (currentRefreshState instanceof LoadState.Loading) {
                if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing() && moviesAdapter.getItemCount() > 0) {
                    mainBinding.recyclerViewMovies.animate().alpha(0.5f).setDuration(300).start();
                    isRecyclerViewDimmedForRefresh[0] = true;
                    wasSwipeRefreshing[0] = true; // Mark that this loading is due to swipe
                }
                // If it's the initial load (empty list), don't mark as swipe refreshing for the toast
                else if (moviesAdapter.getItemCount() == 0) {
                    wasSwipeRefreshing[0] = false;
                }
            } else { // currentRefreshState is NotLoading or Error
                if (isRecyclerViewDimmedForRefresh[0]) {
                    mainBinding.recyclerViewMovies.animate().alpha(1.0f).setDuration(300).start();
                    isRecyclerViewDimmedForRefresh[0] = false;
                }
                // --- Show "Fresh movies brewed" Toast ---
                // Check if the previous state was Loading (and was a swipe refresh)
                // and the current state is NotLoading (i.e., refresh completed successfully)
                if (previousRefreshState[0][0] instanceof LoadState.Loading &&
                        currentRefreshState instanceof LoadState.NotLoading) { // Ensure it was the swipe refresh that just finished
                    Toast.makeText(MainActivity.this, "Fresh movies brewed!", Toast.LENGTH_SHORT).show();
                }
                // Reset wasSwipeRefreshing when refresh is no longer loading
                wasSwipeRefreshing[0] = false;
            }
            // --- End of Dimming & Brewed Toast Logic ---

            // Handle Global ProgressBar (for the very first data set loading AFTER the initial prompt was hidden)
            if (mainBinding.globalProgressBar != null) {
                // Show if refresh is loading AND there are no items yet AND we are past the initial prompt phase.
                boolean showGlobalProgress = refreshState instanceof LoadState.Loading &&
                        moviesAdapter.getItemCount() == 0 &&
                        !isShowingInitialLoadPrompt; // This condition is implicitly true if we passed the block above
                mainBinding.globalProgressBar.setVisibility(showGlobalProgress ? View.VISIBLE : View.GONE);
            }

            // Handle Global Error TextView
            if (mainBinding.globalErrorTextView != null) {
                boolean isErrorAndEmpty = refreshState instanceof LoadState.Error && moviesAdapter.getItemCount() == 0;
                if (isErrorAndEmpty) {
                    mainBinding.globalErrorTextView.setText(((LoadState.Error) refreshState).getError().getLocalizedMessage());
                    mainBinding.globalErrorTextView.setVisibility(View.VISIBLE);
                } else {
                    mainBinding.globalErrorTextView.setVisibility(View.GONE);
                }
            }

            // Handle Empty View TextView
            if (mainBinding.emptyViewTextView != null) {
                boolean isListEmptyAndNotLoadingOrError =
                        !(refreshState instanceof LoadState.Loading) &&
                                !(refreshState instanceof LoadState.Error) &&
                                combinedLoadStates.getAppend().getEndOfPaginationReached() &&
                                moviesAdapter.getItemCount() == 0;
                mainBinding.emptyViewTextView.setVisibility(isListEmptyAndNotLoadingOrError ? View.VISIBLE : View.GONE);
            }




            // Determine RecyclerView visibility
            // Show RecyclerView if not showing initial prompt, not showing global progress, not showing global error, and not showing empty view.
            boolean showRecyclerView = !isShowingInitialLoadPrompt &&
                    (mainBinding.globalProgressBar == null || mainBinding.globalProgressBar.getVisibility() == View.GONE) &&
                    (mainBinding.globalErrorTextView == null || mainBinding.globalErrorTextView.getVisibility() == View.GONE) &&
                    (mainBinding.emptyViewTextView == null || mainBinding.emptyViewTextView.getVisibility() == View.GONE);

            recyclerView.setVisibility(showRecyclerView ? View.VISIBLE : View.GONE);

            previousRefreshState[0] = new LoadState[]{currentRefreshState};

            return null;
        });

        if (mainBinding.swipeRefreshLayout != null) {
            mainBinding.swipeRefreshLayout.setOnRefreshListener(() -> {
                // If the initial prompt is somehow still showing and user swipes, hide it.
                if (isShowingInitialLoadPrompt) {
                    hideInitialLoadingUI();
                }
                moviesAdapter.refresh();
                // Tell the PagingAdapter to refresh its data
            });
        }
    }

    private void initComponents() {
        moviesAdapter = new MoviesAdapter(requestManager);
        mainMovieViewModel = new ViewModelProvider(this).get(MovieViewModel.class);
        swipeRefreshLayout = mainBinding.swipeRefreshLayout;

        networkStatusIcon = mainBinding.networkStatusIcon;
        networkStatusMessage  = mainBinding.networkStatusMessage;
        networkStatusLiveData = new NetworkStatusLiveData(getApplicationContext());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clear subscriptions to prevent memory leaks
        compositeDisposable.clear();
    }
}