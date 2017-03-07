package com.waynebjackson.githubsearch.search;

import java.io.IOException;
import java.util.List;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.waynebjackson.githubsearch.R;
import com.waynebjackson.githubsearch.data.model.Repo;
import com.waynebjackson.githubsearch.data.model.RepoCollection;
import com.waynebjackson.githubsearch.data.service.GithubService;
import com.waynebjackson.githubsearch.data.service.GithubServiceFactory;
import com.waynebjackson.githubsearch.utils.NetworkUtils;
import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;

import static com.google.common.base.Preconditions.checkNotNull;

public class SearchActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<RepoCollection>, RepoAdapter.RepoClickListener {

    private static final float THANK_YOU_SCALE_FACTOR = 1.2f;
    private static final long THANK_YOU_ANIMATION_DURATION = 315;
    private static final int THANK_YOU_ANIMATION_PULSE_COUNT = 3;

    private TextView mResultsCountView;
    private RecyclerView mRepoResultsRecyclerView;
    private LinearLayout mEmptyView;

    private RepoAdapter mRepoAdapter;

    private SearchResultLoader mSearchResultLoader;
    private NavigationView mNavigationView;
    private TextView mThankYouHeaderView;

    private ObjectAnimator mThankYouAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Toolbar
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Navigation Drawer
        configureNavDrawer(toolbar);

        // Init UI Elements
        mResultsCountView = (TextView) findViewById(R.id.results_count_view);
        mRepoResultsRecyclerView = (RecyclerView) findViewById(R.id.repo_results_recyclerview);
        mEmptyView = (LinearLayout) findViewById(R.id.empty_view);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mThankYouHeaderView = (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.thankyou_header_view);

        // Setup RecyclerView
        mRepoResultsRecyclerView.setHasFixedSize(true);
        mRepoAdapter = new RepoAdapter(this, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRepoResultsRecyclerView.setLayoutManager(layoutManager);
        mRepoResultsRecyclerView.setAdapter(mRepoAdapter);

        // Init Search Result Loader
        mSearchResultLoader = (SearchResultLoader) getSupportLoaderManager()
                .initLoader(0, null, this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            mSearchResultLoader.executeQuery(query);
        } else {
            super.onNewIntent(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!NetworkUtils.isConnected(this)) {
            showNoNetworkDialog();
        }

    }

    private void configureNavDrawer(final Toolbar toolbar) {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                toolbar, R.string.nav_open, R.string.nav_close) {

            // Called when a drawer has settled in a completely closed state
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                toolbar.setTitle(R.string.search_activity_title);
                if (mThankYouAnimator != null && mThankYouAnimator.isRunning()) {
                    cancelThankYouAnimation();
                }
                supportInvalidateOptionsMenu(); // creates a call to onPrepareOptionsMenu();
            }

            // Called when a drawer has settled in a completely open state
            @Override
            public void onDrawerOpened(View drawerView) {
                Timber.d("[onDrawerOpened]");
                super.onDrawerOpened(drawerView);
                animateThankYou();
                supportInvalidateOptionsMenu(); // creates a call to onPrepareOptionsMenu()
            }
        };
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = searchItem != null ? (SearchView) searchItem.getActionView() : null;

        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public Loader<RepoCollection> onCreateLoader(int id, Bundle args) {
        return new SearchResultLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<RepoCollection> loader, RepoCollection data) {
        if (data == null || data.getRepositories().isEmpty()) {
            showNoResults();
        } else {
            showRepositories(data.getRepositories());
        }
    }

    @Override
    public void onLoaderReset(Loader<RepoCollection> loader) {
        Timber.d("[onLoaderReset]");
    }

    @Override
    public void onRepoClicked(@NonNull Repo repository) {
        final String repoUrl = repository.getHtmlUrl();
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(repoUrl));
        startActivity(browserIntent);
    }

    private void showNoResults() {
        mRepoAdapter.clearRepos();
        mRepoResultsRecyclerView.setVisibility(View.GONE);
        mResultsCountView.setVisibility(View.VISIBLE);
        final String query = mSearchResultLoader.getQuery().toLowerCase();
        mResultsCountView.setText(getString(R.string.no_results, query));
    }

    private void showRepositories(@NonNull List<Repo> repositories) {
        setResultsCountText(repositories);
        mEmptyView.setVisibility(View.GONE);
        findViewById(R.id.main_content).setVisibility(View.VISIBLE);
        mRepoResultsRecyclerView.setVisibility(View.VISIBLE);
        mRepoAdapter.bindRepos(repositories);
    }

    private void setResultsCountText(@NonNull List<Repo> repositories) {
        final int count = repositories.size();
        final String countString = getResources().getQuantityString(R.plurals.search_results_found,
                count, count);
        final String query = mSearchResultLoader.getQuery().toLowerCase();
        mResultsCountView.setText(String.format("%s %s.", countString, query));
    }

    private void animateThankYou() {
        if (mThankYouAnimator == null) {
            mThankYouAnimator = ObjectAnimator.ofPropertyValuesHolder(mThankYouHeaderView,
                                                                      PropertyValuesHolder.ofFloat("scaleX", THANK_YOU_SCALE_FACTOR),
                                                                      PropertyValuesHolder.ofFloat("scaleY", THANK_YOU_SCALE_FACTOR));
            mThankYouAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            mThankYouAnimator.setDuration(THANK_YOU_ANIMATION_DURATION);

            mThankYouAnimator.setRepeatCount(THANK_YOU_ANIMATION_PULSE_COUNT);
            mThankYouAnimator.setRepeatMode(ObjectAnimator.REVERSE);
        }
        mThankYouAnimator.start();
    }

    private void cancelThankYouAnimation() {
        mThankYouAnimator.cancel();
        mThankYouHeaderView.setScaleX(1f);
        mThankYouHeaderView.setScaleY(1f);
    }

    private void showNoNetworkDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.no_network_connection, getString(R.string.app_name)));
        builder.setCancelable(false);

        builder.setPositiveButton(
            R.string.ok,
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    finish();
                }
            });

        AlertDialog noNetworkDialog = builder.create();
        noNetworkDialog.show();
    }

    public static class SearchResultLoader extends AsyncTaskLoader<RepoCollection> {
        private GithubService mGithubService;
        private RepoCollection mSearchResponse;
        private String mQuery;

        public SearchResultLoader(Context context) {
            super(context);
            mGithubService = new GithubServiceFactory().getGithubService();
        }

        public void executeQuery(@NonNull String query) {
            checkNotNull(query);
            if (query.equals(mQuery)) {
                // Returned cached response if the query is the same
                deliverResult(mSearchResponse);
            } else {
                mQuery = query;
                forceLoad();
            }
        }

        @Override
        public RepoCollection loadInBackground() {
            Call<RepoCollection> call = mGithubService.getRepos(mQuery);
            try {
                Response<RepoCollection> response = call.execute();
                return response.body();
            } catch (IOException e) {
                Timber.e("Error fetching repos from GithubService.class: %s", e.getMessage());
            }
            return null;
        }

        @Override
        public void deliverResult(RepoCollection searchResponse) {
            // Cache results for later retrieval for identical back to back queries
            mSearchResponse = searchResponse;
            super.deliverResult(searchResponse);
        }

        @Override
        protected void onReset() {
            mSearchResponse = null;
            mQuery = null;
            super.onReset();
        }

        public String getQuery() {
            return mQuery;
        }
    }
}
